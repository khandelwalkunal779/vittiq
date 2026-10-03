package com.vittiq.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import com.vittiq.android.data.model.AccountWithCategory
import com.vittiq.android.data.model.CurrencyRate
import com.vittiq.android.data.model.Transaction
import com.vittiq.android.data.model.TransactionType
import com.vittiq.android.theme.AmberGold
import com.vittiq.android.theme.CardBorder
import com.vittiq.android.theme.CardBorderSubtle
import com.vittiq.android.theme.CharcoalBlue
import com.vittiq.android.theme.ExpenseRed
import com.vittiq.android.theme.ExpenseRedSoft
import com.vittiq.android.theme.InkBlack
import com.vittiq.android.theme.OceanMist
import com.vittiq.android.theme.OceanMistSoft
import com.vittiq.android.theme.SurfaceWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

val PREDEFINED_CATEGORIES = listOf(
    "Food & Dining",
    "Groceries",
    "Income",
    "Transportation",
    "Housing",
    "Coffee",
    "Entertainment",
    "Shopping",
    "Utilities",
    "Other"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheet(
    accounts: List<AccountWithCategory>,
    currencyRates: List<CurrencyRate>,
    onDismiss: () -> Unit,
    onSaveTransaction: (Transaction) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var title by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.DEBIT) }
    var selectedAccount by remember(accounts) { mutableStateOf(accounts.firstOrNull()) }
    var selectedCategory by remember { mutableStateOf(PREDEFINED_CATEGORIES.first()) }
    var amountInput by remember { mutableStateOf("") }

    val defaultInrRate = remember(currencyRates) {
        currencyRates.find { it.currencyCode == "INR" }
            ?: CurrencyRate("INR", "₹", 1.0)
    }
    var selectedCurrency by remember(currencyRates) {
        mutableStateOf(defaultInrRate)
    }

    val currentTimestamp = remember { System.currentTimeMillis() }
    val formattedDate = remember(currentTimestamp) {
        SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(currentTimestamp))
    }

    var accountDropdownExpanded by remember { mutableStateOf(false) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var currencyDropdownExpanded by remember { mutableStateOf(false) }

    // Live currency conversion calculation
    val parsedAmount = amountInput.toDoubleOrNull() ?: 0.0
    val convertedInrAmount by remember(parsedAmount, selectedCurrency) {
        derivedStateOf {
            parsedAmount * selectedCurrency.rateToInr
        }
    }

    val isInputValid = title.isNotBlank() && parsedAmount > 0 && selectedAccount != null

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "New Transaction",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkBlack
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = CharcoalBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Transaction Type Toggle (Debit vs Credit)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBorderSubtle)
                    .padding(4.dp)
            ) {
                // Debit Pill (Default)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedType == TransactionType.DEBIT) ExpenseRedSoft else Color.Transparent)
                        .clickable { selectedType = TransactionType.DEBIT }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Debit (- Expense)",
                        fontWeight = if (selectedType == TransactionType.DEBIT) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedType == TransactionType.DEBIT) ExpenseRed else CharcoalBlue,
                        fontSize = 14.sp
                    )
                }

                // Credit Pill
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedType == TransactionType.CREDIT) OceanMistSoft else Color.Transparent)
                        .clickable { selectedType = TransactionType.CREDIT }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Credit (+ Income)",
                        fontWeight = if (selectedType == TransactionType.CREDIT) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedType == TransactionType.CREDIT) OceanMist else CharcoalBlue,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Title Field
            Text(text = "Title", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = CharcoalBlue)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("e.g. ICICI Deposit, Metro, Groceries", color = CharcoalBlue.copy(alpha = 0.5f)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = InkBlack,
                    unfocusedBorderColor = CardBorder
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Amount with Currency Selection & Live INR Conversion
            Text(text = "Amount & Currency", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = CharcoalBlue)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Currency Dropdown Button
                Box {
                    Row(
                        modifier = Modifier
                            .height(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                            .background(CardBorderSubtle)
                            .clickable { currencyDropdownExpanded = true }
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${selectedCurrency.symbol} ${selectedCurrency.currencyCode}",
                            fontWeight = FontWeight.Bold,
                            color = InkBlack,
                            fontSize = 15.sp
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Currency",
                            tint = CharcoalBlue
                        )
                    }

                    DropdownMenu(
                        expanded = currencyDropdownExpanded,
                        onDismissRequest = { currencyDropdownExpanded = false }
                    ) {
                        currencyRates.forEach { rate ->
                            DropdownMenuItem(
                                text = {
                                    Text("${rate.symbol} ${rate.currencyCode} (1 = ₹${rate.rateToInr})")
                                },
                                onClick = {
                                    selectedCurrency = rate
                                    currencyDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Amount Text Field
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { input ->
                        if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                            amountInput = input
                        }
                    },
                    placeholder = { Text("0.00", color = CharcoalBlue.copy(alpha = 0.5f)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = InkBlack,
                        unfocusedBorderColor = CardBorder
                    )
                )
            }

            // Converted INR Display (if foreign currency selected)
            if (selectedCurrency.currencyCode != "INR" && parsedAmount > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(OceanMistSoft)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "≈ ₹${Formatters.formatInr(convertedInrAmount)} at 1 ${selectedCurrency.currencyCode} = ₹${selectedCurrency.rateToInr}",
                        color = OceanMist,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Account Dropdown (Lists individual accounts with parent category: "Account Name · Category")
            Text(text = "Account", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = CharcoalBlue)
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                        .clickable { accountDropdownExpanded = true }
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedAccount?.let {
                            "${it.account.name} · ${it.category.name} (${Formatters.formatInr(it.account.currentBalance)})"
                        } ?: "Select Account",
                        color = if (selectedAccount != null) InkBlack else CharcoalBlue.copy(alpha = 0.5f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Account",
                        tint = CharcoalBlue
                    )
                }

                DropdownMenu(
                    expanded = accountDropdownExpanded,
                    onDismissRequest = { accountDropdownExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    accounts.forEach { accountWithCat ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = accountWithCat.account.name,
                                            fontWeight = FontWeight.SemiBold,
                                            color = InkBlack
                                        )
                                        Text(
                                            text = accountWithCat.category.name,
                                            fontSize = 11.sp,
                                            color = CharcoalBlue
                                        )
                                    }
                                    Text(
                                        text = Formatters.formatInr(accountWithCat.account.currentBalance),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = InkBlack
                                    )
                                }
                            },
                            onClick = {
                                selectedAccount = accountWithCat
                                accountDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5. Category Dropdown
            Text(text = "Category", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = CharcoalBlue)
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                        .clickable { categoryDropdownExpanded = true }
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedCategory,
                        color = InkBlack,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Category",
                        tint = CharcoalBlue
                    )
                }

                DropdownMenu(
                    expanded = categoryDropdownExpanded,
                    onDismissRequest = { categoryDropdownExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    PREDEFINED_CATEGORIES.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                categoryDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 6. Date (pre-populated with current date)
            Text(text = "Date", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = CharcoalBlue)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardBorderSubtle)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Date",
                    tint = CharcoalBlue,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = "$formattedDate (Today)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = InkBlack
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 7. Save CTA Button
            Button(
                onClick = {
                    val accountWithCat = selectedAccount ?: return@Button
                    val finalAmountInInr = if (selectedCurrency.currencyCode == "INR") {
                        parsedAmount
                    } else {
                        convertedInrAmount
                    }
                    val description = if (selectedCurrency.currencyCode != "INR") {
                        "${accountWithCat.account.name} (Paid ${selectedCurrency.symbol}$amountInput)"
                    } else {
                        accountWithCat.account.name
                    }
                    val transaction = Transaction(
                        id = UUID.randomUUID().toString(),
                        timestamp = currentTimestamp,
                        accountId = accountWithCat.account.id,
                        name = title.trim(),
                        category = selectedCategory,
                        amount = finalAmountInInr,
                        type = selectedType,
                        description = description
                    )
                    onSaveTransaction(transaction)
                    onDismiss()
                },
                enabled = isInputValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmberGold,
                    contentColor = InkBlack,
                    disabledContainerColor = AmberGold.copy(alpha = 0.4f),
                    disabledContentColor = InkBlack.copy(alpha = 0.4f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = InkBlack,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Save Transaction",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

