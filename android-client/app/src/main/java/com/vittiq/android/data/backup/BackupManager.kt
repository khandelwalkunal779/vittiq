package com.vittiq.android.data.backup

import android.content.ContentResolver
import android.net.Uri
import com.vittiq.android.data.model.Account
import com.vittiq.android.data.model.AccountCategory
import com.vittiq.android.data.model.CurrencyRate
import com.vittiq.android.data.model.Transaction
import com.vittiq.android.data.model.TransactionCategory
import com.vittiq.android.data.model.UserProfile
import com.vittiq.android.data.repository.VittiqRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class BackupManager(
    private val repository: VittiqRepository
) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    suspend fun exportBackup(
        uri: Uri,
        contentResolver: ContentResolver
    ): Result<BackupMetadata> = withContext(Dispatchers.IO) {
        runCatching {
            val payload = repository.getBackupPayload()

            contentResolver.openOutputStream(uri)?.use { outputStream ->
                ZipOutputStream(BufferedOutputStream(outputStream)).use { zipOut ->
                    writeZipEntry(zipOut, "metadata.json", json.encodeToString(payload.metadata))
                    writeZipEntry(zipOut, "account_categories.json", json.encodeToString(payload.accountCategories))
                    writeZipEntry(zipOut, "accounts.json", json.encodeToString(payload.accounts))
                    writeZipEntry(zipOut, "transactions.json", json.encodeToString(payload.transactions))
                    writeZipEntry(zipOut, "transaction_categories.json", json.encodeToString(payload.transactionCategories))
                    writeZipEntry(zipOut, "currency_rates.json", json.encodeToString(payload.currencyRates))
                    if (payload.userProfile != null) {
                        writeZipEntry(zipOut, "user_profile.json", json.encodeToString(payload.userProfile))
                    }
                    zipOut.flush()
                }
            } ?: throw IllegalStateException("Could not open output stream for URI: $uri")

            payload.metadata
        }
    }

    suspend fun readBackupMetadata(
        uri: Uri,
        contentResolver: ContentResolver
    ): Result<BackupMetadata> = withContext(Dispatchers.IO) {
        runCatching {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                ZipInputStream(BufferedInputStream(inputStream)).use { zipIn ->
                    var entry = zipIn.nextEntry
                    while (entry != null) {
                        if (entry.name == "metadata.json") {
                            val content = readEntryContent(zipIn)
                            val metadata = json.decodeFromString<BackupMetadata>(content)
                            if (metadata.schemaVersion > 3) {
                                throw IllegalArgumentException(
                                    "Backup schema version ${metadata.schemaVersion} is newer than supported version 3. Please update Vittiq."
                                )
                            }
                            return@runCatching metadata
                        }
                        zipIn.closeEntry()
                        entry = zipIn.nextEntry
                    }
                    throw IllegalArgumentException("metadata.json missing from backup archive.")
                }
            } ?: throw IllegalStateException("Could not open input stream for URI: $uri")
        }
    }

    suspend fun parseBackupPayload(
        uri: Uri,
        contentResolver: ContentResolver
    ): Result<BackupPayload> = withContext(Dispatchers.IO) {
        runCatching {
            var metadata: BackupMetadata? = null
            var categories: List<AccountCategory>? = null
            var accounts: List<Account>? = null
            var transactions: List<Transaction>? = null
            var txCategories: List<TransactionCategory>? = null
            var currencyRates: List<CurrencyRate>? = null
            var userProfile: UserProfile? = null

            contentResolver.openInputStream(uri)?.use { inputStream ->
                ZipInputStream(BufferedInputStream(inputStream)).use { zipIn ->
                    var entry = zipIn.nextEntry
                    while (entry != null) {
                        val content = readEntryContent(zipIn)
                        when (entry.name) {
                            "metadata.json" -> {
                                metadata = json.decodeFromString<BackupMetadata>(content)
                            }
                            "account_categories.json" -> {
                                categories = json.decodeFromString<List<AccountCategory>>(content)
                            }
                            "accounts.json" -> {
                                accounts = json.decodeFromString<List<Account>>(content)
                            }
                            "transactions.json" -> {
                                transactions = json.decodeFromString<List<Transaction>>(content)
                            }
                            "transaction_categories.json" -> {
                                txCategories = json.decodeFromString<List<TransactionCategory>>(content)
                            }
                            "currency_rates.json" -> {
                                currencyRates = json.decodeFromString<List<CurrencyRate>>(content)
                            }
                            "user_profile.json" -> {
                                userProfile = json.decodeFromString<UserProfile>(content)
                            }
                        }
                        zipIn.closeEntry()
                        entry = zipIn.nextEntry
                    }
                }
            } ?: throw IllegalStateException("Could not open input stream for URI: $uri")

            val finalMetadata = metadata ?: throw IllegalArgumentException("metadata.json missing from backup archive.")
            if (finalMetadata.schemaVersion > 4) {
                throw IllegalArgumentException("Unsupported backup schema version: ${finalMetadata.schemaVersion}.")
            }

            BackupPayload(
                metadata = finalMetadata,
                accountCategories = categories ?: emptyList(),
                accounts = accounts ?: emptyList(),
                transactions = transactions ?: emptyList(),
                transactionCategories = txCategories ?: emptyList(),
                currencyRates = currencyRates ?: emptyList(),
                userProfile = userProfile
            )
        }
    }

    suspend fun restoreBackup(
        payload: BackupPayload,
        mode: RestoreMode
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            when (mode) {
                RestoreMode.REPLACE -> {
                    repository.restoreReplace(payload)
                    "Data successfully replaced with backup (${payload.accounts.size} accounts, ${payload.transactions.size} transactions)."
                }
                RestoreMode.MERGE -> {
                    val result = repository.restoreMerge(payload)
                    "Data merged: +${result.importedTransactionsCount} transactions (${result.skippedTransactionsCount} existing skipped), +${result.addedAccountsCount} accounts."
                }
            }
        }
    }

    private fun writeZipEntry(zipOut: ZipOutputStream, entryName: String, content: String) {
        val entry = ZipEntry(entryName)
        zipOut.putNextEntry(entry)
        zipOut.write(content.toByteArray(StandardCharsets.UTF_8))
        zipOut.closeEntry()
    }

    private fun readEntryContent(zipIn: ZipInputStream): String {
        val byteOut = ByteArrayOutputStream()
        val buffer = ByteArray(4096)
        var read: Int
        while (zipIn.read(buffer).also { read = it } != -1) {
            byteOut.write(buffer, 0, read)
        }
        return byteOut.toString(StandardCharsets.UTF_8.name())
    }
}
