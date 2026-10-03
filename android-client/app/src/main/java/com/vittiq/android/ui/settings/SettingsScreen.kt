package com.vittiq.android.ui.settings

import android.content.ContentResolver
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vittiq.android.data.backup.RestoreMode
import com.vittiq.android.data.model.CurrencyRate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val InkBlack = Color(0xFF111827)
private val CharcoalBlue = Color(0xFF334155)
private val AmberGold = Color(0xFFFBBF24)
private val BrightSnow = Color(0xFFF9FAFB)
private val SurfaceWhite = Color(0xFFFFFFFF)
private val BorderStroke = Color(0xFFE2E8F0)

@Composable
fun SettingsScreen(
    currencyRates: List<CurrencyRate>,
    uiState: SettingsUiState,
    onRateUpdated: (CurrencyRate) -> Unit,
    onManageAccountsClick: () -> Unit,
    onManageTransactionCategoriesClick: () -> Unit,
    onExportBackup: (Uri, ContentResolver) -> Unit = { _, _ -> },
    onPrepareRestore: (Uri, ContentResolver) -> Unit = { _, _ -> },
    onConfirmRestore: (RestoreMode) -> Unit = {},
    onDismissRestoreDialog: () -> Unit = {},
    onClearBackupMessage: () -> Unit = {},
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var editingRate by remember { mutableStateOf<CurrencyRate?>(null) }
    val context = LocalContext.current

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        if (uri != null) {
            onExportBackup(uri, context.contentResolver)
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            onPrepareRestore(uri, context.contentResolver)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrightSnow
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.dp, BorderStroke, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = InkBlack
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkBlack
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section: Account Management
                item {
                    Text(
                        text = "ACCOUNTS & STRUCTURE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalBlue,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(SurfaceWhite)
                            .border(1.dp, BorderStroke, RoundedCornerShape(20.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onManageAccountsClick)
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFEF3C7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountTree,
                                        contentDescription = null,
                                        tint = InkBlack,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Manage Accounts & Categories",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = InkBlack
                                    )
                                    Text(
                                        text = "Add, edit, or archive accounts and categories",
                                        fontSize = 12.sp,
                                        color = CharcoalBlue
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = CharcoalBlue
                            )
                        }

                        HorizontalDivider(color = Color(0xFFF1F5F9))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onManageTransactionCategoriesClick)
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFCCFBF1)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Category,
                                        contentDescription = null,
                                        tint = InkBlack,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Manage Transaction Categories",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = InkBlack
                                    )
                                    Text(
                                        text = "Add, rename, or archive transaction tags",
                                        fontSize = 12.sp,
                                        color = CharcoalBlue
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = CharcoalBlue
                            )
                        }
                    }
                }

                // Section: Currency Conversion Rates
                item {
                    Text(
                        text = "CURRENCY CONVERSION (IN INR)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalBlue,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(SurfaceWhite)
                            .border(1.dp, BorderStroke, RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE0F2FE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CurrencyExchange,
                                    contentDescription = null,
                                    tint = InkBlack,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Exchange Rates to INR (₹)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = InkBlack
                                )
                                Text(
                                    text = "Used when recording foreign currency transactions",
                                    fontSize = 12.sp,
                                    color = CharcoalBlue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9))

                        currencyRates.forEachIndexed { index, rate ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { editingRate = rate }
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${rate.currencyCode} (${rate.symbol})",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = InkBlack
                                    )
                                    Text(
                                        text = "1 ${rate.currencyCode} = ₹${rate.rateToInr}",
                                        fontSize = 13.sp,
                                        color = CharcoalBlue
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "₹${rate.rateToInr}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = InkBlack
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Rate",
                                        tint = CharcoalBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            if (index < currencyRates.lastIndex) {
                                HorizontalDivider(color = Color(0xFFF8FAFC))
                            }
                        }
                    }
                }

                // Section: Data & Storage (Backup & Restore)
                item {
                    Text(
                        text = "DATA & STORAGE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalBlue,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(SurfaceWhite)
                            .border(1.dp, BorderStroke, RoundedCornerShape(20.dp))
                    ) {
                        // Export Backup Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                                    exportLauncher.launch("vittiq_backup_$timestamp.zip")
                                }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFEF3C7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudUpload,
                                        contentDescription = null,
                                        tint = InkBlack,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Export Backup (.zip)",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = InkBlack
                                    )
                                    Text(
                                        text = "Save your accounts, categories, and transactions to a local file or Google Drive.",
                                        fontSize = 12.sp,
                                        color = CharcoalBlue
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = CharcoalBlue
                            )
                        }

                        HorizontalDivider(color = Color(0xFFF1F5F9))

                        // Import Backup Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    importLauncher.launch(arrayOf("application/zip", "application/x-zip-compressed", "*/*"))
                                }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFCCFBF1)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudDownload,
                                        contentDescription = null,
                                        tint = InkBlack,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Import Backup (.zip)",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = InkBlack
                                    )
                                    Text(
                                        text = "Restore or merge your financial data from a backup archive.",
                                        fontSize = 12.sp,
                                        color = CharcoalBlue
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = CharcoalBlue
                            )
                        }
                    }
                }

                // Section: About
                item {
                    Text(
                        text = "ABOUT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalBlue,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(SurfaceWhite)
                            .border(1.dp, BorderStroke, RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F5F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = InkBlack,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Vittiq Android",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = InkBlack
                                )
                                Text(
                                    text = "Version 1.0.0 · Offline-First Architecture",
                                    fontSize = 12.sp,
                                    color = CharcoalBlue
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }

    editingRate?.let { rate ->
        EditRateDialog(
            rate = rate,
            onDismiss = { editingRate = null },
            onConfirm = { updatedRate ->
                onRateUpdated(updatedRate)
                editingRate = null
            }
        )
    }

    // Restore Confirmation Dialog
    if (uiState.pendingRestorePayload != null) {
        val payload = uiState.pendingRestorePayload
        val metadata = payload.metadata
        val formattedDate = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault()).format(Date(metadata.exportTimestamp))

        AlertDialog(
            onDismissRequest = onDismissRestoreDialog,
            title = {
                Text(
                    text = "Restore Backup",
                    fontWeight = FontWeight.Bold,
                    color = InkBlack,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Backup snapshot created on $formattedDate (v${metadata.appVersion}).",
                        fontSize = 13.sp,
                        color = CharcoalBlue
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(BrightSnow)
                            .border(1.dp, BorderStroke, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("• Transactions: ${metadata.transactionCount}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = InkBlack)
                            Text("• Accounts: ${metadata.accountCount}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = InkBlack)
                            Text("• Categories: ${metadata.transactionCategoryCount}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = InkBlack)
                        }
                    }
                    Text(
                        text = "Select recovery strategy:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkBlack
                    )
                    Text(
                        text = "• Merge (Keep Existing): Inserts new transactions and accounts without modifying existing data.",
                        fontSize = 12.sp,
                        color = CharcoalBlue
                    )
                    Text(
                        text = "• Replace (Full Overwrite): Wipes all local records and restores the backup snapshot completely.",
                        fontSize = 12.sp,
                        color = Color(0xFFDC2626)
                    )
                }
            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onConfirmRestore(RestoreMode.MERGE) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberGold,
                            contentColor = InkBlack
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Merge (Keep Existing)", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onConfirmRestore(RestoreMode.REPLACE) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFEE2E2),
                            contentColor = Color(0xFFDC2626)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Replace (Full Overwrite)", fontWeight = FontWeight.Bold)
                    }

                    TextButton(
                        onClick = onDismissRestoreDialog,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel", color = CharcoalBlue)
                    }
                }
            },
            containerColor = SurfaceWhite
        )
    }

    // Loading Dialog
    if (uiState.isBackupLoading) {
        AlertDialog(
            onDismissRequest = {},
            title = null,
            text = {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(color = AmberGold)
                    Text("Processing backup, please wait...", fontSize = 14.sp, color = InkBlack)
                }
            },
            confirmButton = {},
            containerColor = SurfaceWhite
        )
    }

    // Result Notification Dialog
    if (uiState.backupMessage != null) {
        AlertDialog(
            onDismissRequest = onClearBackupMessage,
            title = {
                Text("Backup & Restore", fontWeight = FontWeight.Bold, color = InkBlack)
            },
            text = {
                Text(uiState.backupMessage, color = CharcoalBlue, fontSize = 14.sp)
            },
            confirmButton = {
                Button(
                    onClick = onClearBackupMessage,
                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = InkBlack),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = SurfaceWhite
        )
    }
}

@Composable
private fun EditRateDialog(
    rate: CurrencyRate,
    onDismiss: () -> Unit,
    onConfirm: (CurrencyRate) -> Unit
) {
    var rateText by remember { mutableStateOf(rate.rateToInr.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit ${rate.currencyCode} Rate",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = InkBlack
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Specify INR value for 1 ${rate.currencyCode}:",
                    fontSize = 13.sp,
                    color = CharcoalBlue
                )
                OutlinedTextField(
                    value = rateText,
                    onValueChange = { rateText = it },
                    label = { Text("Rate to INR (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newRate = rateText.toDoubleOrNull()
                    if (newRate != null && newRate > 0.0) {
                        onConfirm(rate.copy(rateToInr = newRate))
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmberGold,
                    contentColor = InkBlack
                )
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = CharcoalBlue)
            }
        },
        containerColor = SurfaceWhite
    )
}
