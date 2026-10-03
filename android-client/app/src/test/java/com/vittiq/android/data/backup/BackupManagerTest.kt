package com.vittiq.android.data.backup

import com.vittiq.android.data.model.Account
import com.vittiq.android.data.model.AccountCategory
import com.vittiq.android.data.model.CurrencyRate
import com.vittiq.android.data.model.Transaction
import com.vittiq.android.data.model.TransactionCategory
import com.vittiq.android.data.model.TransactionType
import com.vittiq.android.data.model.UserProfile
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class BackupManagerTest {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @Test
    fun testPayloadSerializationAndDeserialization() {
        val metadata = BackupMetadata(
            schemaVersion = 4,
            exportTimestamp = 1700000000000L,
            appVersion = "1.0",
            accountCategoryCount = 1,
            accountCount = 1,
            transactionCount = 1,
            transactionCategoryCount = 1,
            currencyRateCount = 1,
            userProfileCount = 1
        )
        val category = AccountCategory(id = "cat-1", name = "Cash", displayOrder = 1)
        val account = Account(id = "acc-1", categoryId = "cat-1", name = "Wallet", initialBalance = 100.0, currentBalance = 150.0)
        val transaction = Transaction(
            id = "tx-1",
            timestamp = 1700000001000L,
            accountId = "acc-1",
            name = "Lunch",
            category = "Food",
            amount = 50.0,
            type = TransactionType.DEBIT
        )
        val txCategory = TransactionCategory(id = "txcat-1", name = "Food")
        val rate = CurrencyRate(currencyCode = "USD", symbol = "$", rateToInr = 85.0)
        val profile = UserProfile(firstName = "Kunal", lastName = "Khandelwal", handle = "kunal", avatarInitial = "K")

        val payload = BackupPayload(
            metadata = metadata,
            accountCategories = listOf(category),
            accounts = listOf(account),
            transactions = listOf(transaction),
            transactionCategories = listOf(txCategory),
            currencyRates = listOf(rate),
            userProfile = profile
        )

        // Serialize
        val metaJson = json.encodeToString(payload.metadata)
        val accountsJson = json.encodeToString(payload.accounts)
        val txJson = json.encodeToString(payload.transactions)

        assertTrue(metaJson.contains("\"schemaVersion\": 4"))
        assertTrue(accountsJson.contains("\"Wallet\""))
        assertTrue(txJson.contains("\"Lunch\""))

        // Deserialize
        val parsedMeta = json.decodeFromString<BackupMetadata>(metaJson)
        val parsedAccounts = json.decodeFromString<List<Account>>(accountsJson)
        val parsedTxs = json.decodeFromString<List<Transaction>>(txJson)

        assertEquals(4, parsedMeta.schemaVersion)
        assertEquals(1, parsedAccounts.size)
        assertEquals("Wallet", parsedAccounts[0].name)
        assertEquals(1, parsedTxs.size)
        assertEquals("Lunch", parsedTxs[0].name)
        assertEquals(TransactionType.DEBIT, parsedTxs[0].type)
    }

    @Test
    fun testZipArchivePackingAndUnpacking() {
        val meta = BackupMetadata(
            schemaVersion = 4,
            exportTimestamp = 1700000000000L,
            accountCount = 2,
            transactionCount = 5
        )
        val metaJson = json.encodeToString(meta)

        // Write ZIP in memory
        val byteOut = ByteArrayOutputStream()
        ZipOutputStream(byteOut).use { zipOut ->
            zipOut.putNextEntry(ZipEntry("metadata.json"))
            zipOut.write(metaJson.toByteArray(StandardCharsets.UTF_8))
            zipOut.closeEntry()
        }

        // Read ZIP from memory
        val byteIn = ByteArrayInputStream(byteOut.toByteArray())
        var foundMeta: BackupMetadata? = null

        ZipInputStream(byteIn).use { zipIn ->
            var entry = zipIn.nextEntry
            while (entry != null) {
                if (entry.name == "metadata.json") {
                    val content = zipIn.bufferedReader(StandardCharsets.UTF_8).readText()
                    foundMeta = json.decodeFromString<BackupMetadata>(content)
                }
                zipIn.closeEntry()
                entry = zipIn.nextEntry
            }
        }

        assertNotNull(foundMeta)
        assertEquals(4, foundMeta?.schemaVersion)
        assertEquals(2, foundMeta?.accountCount)
        assertEquals(5, foundMeta?.transactionCount)
    }

    @Test
    fun testDeduplicationKeyMatching() {
        val tx1 = Transaction(
            id = "tx-local-1",
            timestamp = 1700000000000L,
            accountId = "acc-1",
            name = "Coffee",
            category = "Dining",
            amount = 150.0,
            type = TransactionType.DEBIT
        )

        val txBackupDuplicate = Transaction(
            id = "tx-backup-diff-id",
            timestamp = 1700000000000L,
            accountId = "acc-backup-1",
            name = "Coffee",
            category = "Dining",
            amount = 150.0,
            type = TransactionType.DEBIT
        )

        val txBackupUnique = Transaction(
            id = "tx-backup-2",
            timestamp = 1700000050000L,
            accountId = "acc-backup-1",
            name = "Groceries",
            category = "Dining",
            amount = 500.0,
            type = TransactionType.DEBIT
        )

        val localList = listOf(tx1)

        val isDuplicate = localList.any { localTx ->
            localTx.id == txBackupDuplicate.id || (
                localTx.timestamp == txBackupDuplicate.timestamp &&
                localTx.amount == txBackupDuplicate.amount &&
                localTx.name.equals(txBackupDuplicate.name, ignoreCase = true)
            )
        }

        val isUniqueDuplicate = localList.any { localTx ->
            localTx.id == txBackupUnique.id || (
                localTx.timestamp == txBackupUnique.timestamp &&
                localTx.amount == txBackupUnique.amount &&
                localTx.name.equals(txBackupUnique.name, ignoreCase = true)
            )
        }

        assertTrue("Identical (timestamp, amount, name) must be detected as duplicate", isDuplicate)
        assertTrue("Different timestamp and name must NOT be detected as duplicate", !isUniqueDuplicate)
    }
}
