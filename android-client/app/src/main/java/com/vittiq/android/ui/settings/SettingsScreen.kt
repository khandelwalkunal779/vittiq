package com.vittiq.android.ui.settings

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
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vittiq.android.data.model.CurrencyRate

private val InkBlack = Color(0xFF111827)
private val CharcoalBlue = Color(0xFF334155)
private val AmberGold = Color(0xFFFBBF24)
private val BrightSnow = Color(0xFFF9FAFB)
private val SurfaceWhite = Color(0xFFFFFFFF)
private val BorderStroke = Color(0xFFE2E8F0)

@Composable
fun SettingsScreen(
    currencyRates: List<CurrencyRate>,
    onRateUpdated: (CurrencyRate) -> Unit,
    onManageAccountsClick: () -> Unit,
    onManageTransactionCategoriesClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var editingRate by remember { mutableStateOf<CurrencyRate?>(null) }

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
