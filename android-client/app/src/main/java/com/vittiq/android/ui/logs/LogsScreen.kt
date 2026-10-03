package com.vittiq.android.ui.logs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vittiq.android.data.model.AccountWithCategory
import com.vittiq.android.data.model.Transaction
import com.vittiq.android.data.model.TransactionType
import com.vittiq.android.theme.AmberGold
import com.vittiq.android.theme.AmberGoldSoft
import com.vittiq.android.theme.BlueSoft
import com.vittiq.android.theme.BrightSnow
import com.vittiq.android.theme.CardBorder
import com.vittiq.android.theme.CardBorderSubtle
import com.vittiq.android.theme.CharcoalBlue
import com.vittiq.android.theme.ExpenseRed
import com.vittiq.android.theme.ExpenseRedSoft
import com.vittiq.android.theme.InkBlack
import com.vittiq.android.theme.OceanMist
import com.vittiq.android.theme.OceanMistSoft
import com.vittiq.android.theme.SurfaceWhite
import com.vittiq.android.theme.TextMuted
import com.vittiq.android.ui.components.AddTransactionSheet
import com.vittiq.android.ui.components.Formatters
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreen(
    uiState: LogsUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onAddClick: () -> Unit,
    onEditTransaction: ((oldTransaction: Transaction, newTransaction: Transaction) -> Unit)? = null,
    onDeleteTransaction: ((Transaction) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedTransactionForDetails by remember { mutableStateOf<Transaction?>(null) }
    var editingTransaction by remember { mutableStateOf<Transaction?>(null) }
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BrightSnow)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Month Selector Bar
            item {
                MonthSelectorBar(
                    monthYearText = uiState.monthYearText,
                    onPreviousClick = onPreviousMonth,
                    onNextClick = onNextMonth
                )
            }

            // 2. Empty state or Grouped Transactions
            if (uiState.groupedTransactions.isEmpty()) {
                item {
                    EmptyLogsCard()
                }
            } else {
                uiState.groupedTransactions.forEach { (dateHeader, transactions) ->
                    item {
                        DateGroupHeader(
                            dateHeader = dateHeader,
                            count = transactions.size
                        )
                    }

                    item {
                        DateGroupCard(
                            transactions = transactions,
                            onTransactionClick = { tx ->
                                selectedTransactionForDetails = tx
                            }
                        )
                    }
                }
            }
        }

        // Floating Action Button (+ Add)
        FloatingActionButton(
            onClick = onAddClick,
            shape = RoundedCornerShape(16.dp),
            containerColor = AmberGold,
            contentColor = InkBlack,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Transaction",
                    tint = InkBlack,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Add",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkBlack
                )
            }
        }

        // Transaction Details Bottom Sheet
        selectedTransactionForDetails?.let { tx ->
            TransactionDetailsBottomSheet(
                transaction = tx,
                accounts = uiState.accountsWithCategory,
                onDismiss = { selectedTransactionForDetails = null },
                onEditClick = {
                    editingTransaction = tx
                    selectedTransactionForDetails = null
                },
                onDeleteClick = {
                    transactionToDelete = tx
                    selectedTransactionForDetails = null
                }
            )
        }

        // Edit Transaction Bottom Sheet
        editingTransaction?.let { txToEdit ->
            AddTransactionSheet(
                accounts = uiState.accountsWithCategory,
                currencyRates = uiState.currencyRates,
                transactionCategories = uiState.transactionCategories,
                transactionToEdit = txToEdit,
                onDismiss = { editingTransaction = null },
                onSaveTransaction = { updatedTx ->
                    onEditTransaction?.invoke(txToEdit, updatedTx)
                    editingTransaction = null
                },
                onDeleteTransaction = { txToDelete ->
                    onDeleteTransaction?.invoke(txToDelete)
                    editingTransaction = null
                }
            )
        }

        // Delete Confirmation Dialog
        transactionToDelete?.let { txToDelete ->
            AlertDialog(
                onDismissRequest = { transactionToDelete = null },
                title = {
                    Text(
                        text = "Delete Transaction?",
                        fontWeight = FontWeight.Bold,
                        color = InkBlack
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to delete '${txToDelete.name}'? Account balances will be automatically recalculated to revert this transaction.",
                        color = CharcoalBlue
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteTransaction?.invoke(txToDelete)
                            transactionToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ExpenseRed,
                            contentColor = SurfaceWhite
                        )
                    ) {
                        Text("Delete", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { transactionToDelete = null }) {
                        Text("Cancel", color = CharcoalBlue)
                    }
                },
                containerColor = SurfaceWhite
            )
        }
    }
}

@Composable
private fun MonthSelectorBar(
    monthYearText: String,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous Month Chevron
        IconButton(
            onClick = onPreviousClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SurfaceWhite)
                .border(1.dp, CardBorder, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "Previous Month",
                tint = CharcoalBlue,
                modifier = Modifier.size(16.dp)
            )
        }

        // Month / Year Pill
        Row(
            modifier = Modifier
                .height(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                .background(SurfaceWhite)
                .clickable { /* Select month */ }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = monthYearText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = InkBlack
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = CharcoalBlue,
                modifier = Modifier.size(18.dp)
            )
        }

        // Next Month Chevron
        IconButton(
            onClick = onNextClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SurfaceWhite)
                .border(1.dp, CardBorder, CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Next Month",
                tint = CharcoalBlue,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun DateGroupHeader(
    dateHeader: String,
    count: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = dateHeader,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = InkBlack
        )
        Text(
            text = "$count ${if (count == 1) "transaction" else "transactions"}",
            fontSize = 12.sp,
            color = TextMuted
        )
    }
}

@Composable
private fun DateGroupCard(
    transactions: List<Transaction>,
    onTransactionClick: (Transaction) -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorderSubtle, RoundedCornerShape(22.dp))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            transactions.forEachIndexed { index, tx ->
                TransactionRow(
                    transaction = tx,
                    onClick = { onTransactionClick(tx) }
                )
                if (index < transactions.size - 1) {
                    HorizontalDivider(
                        color = CardBorderSubtle,
                        thickness = 1.dp,
                        modifier = Modifier.padding(start = 68.dp, end = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
internal fun TransactionRow(
    transaction: Transaction,
    onClick: (() -> Unit)? = null
) {
    val isCredit = transaction.type == TransactionType.CREDIT
    val isTransfer = transaction.type == TransactionType.TRANSFER
    val iconInfo = if (isTransfer) {
        Triple(Icons.Default.SwapHoriz, AmberGoldSoft, InkBlack)
    } else {
        getCategoryVisuals(transaction.category, transaction.name)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category Icon Circle
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(iconInfo.second),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconInfo.first,
                contentDescription = transaction.category,
                tint = iconInfo.third,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Title & Source Account / Route
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = InkBlack
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = transaction.description ?: transaction.category,
                fontSize = 12.sp,
                color = CharcoalBlue
            )
        }

        // Amount & Category label
        Column(horizontalAlignment = Alignment.End) {
            val amountFormatted = if (isTransfer) {
                Formatters.formatInr(transaction.amount)
            } else {
                Formatters.formatTransactionAmount(transaction.amount, isCredit)
            }
            Text(
                text = amountFormatted,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCredit) OceanMist else InkBlack
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = transaction.category,
                fontSize = 12.sp,
                color = CharcoalBlue
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionDetailsBottomSheet(
    transaction: Transaction,
    accounts: List<AccountWithCategory>,
    onDismiss: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val isCredit = transaction.type == TransactionType.CREDIT
    val isTransfer = transaction.type == TransactionType.TRANSFER
    val iconInfo = if (isTransfer) {
        Triple(Icons.Default.SwapHoriz, AmberGoldSoft, InkBlack)
    } else {
        getCategoryVisuals(transaction.category, transaction.name)
    }

    val formattedDateTime = remember(transaction.timestamp) {
        val sdf = SimpleDateFormat("dd MMMM yyyy · hh:mm a", Locale.getDefault())
        sdf.format(Date(transaction.timestamp))
    }

    val accountName = remember(transaction, accounts) {
        if (isTransfer) {
            val fromName = accounts.find { it.account.id == transaction.accountId }?.account?.name ?: "Source"
            val toName = accounts.find { it.account.id == transaction.toAccountId }?.account?.name ?: "Destination"
            "$fromName ➔ $toName"
        } else {
            accounts.find { it.account.id == transaction.accountId }?.account?.name ?: transaction.description ?: "Account"
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transaction Details",
                    fontSize = 18.sp,
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

            // Large Icon Circle
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(iconInfo.second),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconInfo.first,
                    contentDescription = transaction.category,
                    tint = iconInfo.third,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Text(
                text = transaction.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = InkBlack
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Amount
            val amountFormatted = if (isTransfer) {
                Formatters.formatInr(transaction.amount)
            } else {
                Formatters.formatTransactionAmount(transaction.amount, isCredit)
            }
            Text(
                text = amountFormatted,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isCredit) OceanMist else InkBlack
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Badges row: Type & Category
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Type badge
                val (typeBg, typeTextColor, typeLabel) = when (transaction.type) {
                    TransactionType.CREDIT -> Triple(Color(0xFFCCFBF1), Color(0xFF0F766E), "INCOME")
                    TransactionType.DEBIT -> Triple(Color(0xFFFEE2E2), ExpenseRed, "EXPENSE")
                    TransactionType.TRANSFER -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), "TRANSFER")
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(typeBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = typeLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = typeTextColor
                    )
                }

                // Category badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = transaction.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CharcoalBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Details Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BrightSnow)
                    .border(1.dp, CardBorderSubtle, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailRow(label = "Date & Time", value = formattedDateTime)
                HorizontalDivider(color = Color(0xFFF1F5F9))
                DetailRow(label = if (isTransfer) "Route" else "Account", value = accountName)
                if (!transaction.description.isNullOrBlank() && transaction.description != accountName) {
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    DetailRow(label = "Note", value = transaction.description)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Edit Button
                Button(
                    onClick = onEditClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberGold,
                        contentColor = InkBlack
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Edit", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                // Delete Button
                Button(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFEE2E2),
                        contentColor = ExpenseRed
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Delete", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = CharcoalBlue
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = InkBlack
        )
    }
}

@Composable
private fun EmptyLogsCard() {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
            .border(1.dp, CardBorderSubtle, RoundedCornerShape(22.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.Receipt,
                contentDescription = null,
                tint = CharcoalBlue.copy(alpha = 0.5f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No transactions found",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = InkBlack
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tap + Add to record a new transaction for this month.",
                fontSize = 13.sp,
                color = CharcoalBlue
            )
        }
    }
}

internal fun getCategoryVisuals(category: String, title: String): Triple<ImageVector, Color, Color> {
    val lower = "${category.lowercase()} ${title.lowercase()}"
    return when {
        lower.contains("transfer") ->
            Triple(Icons.Default.SwapHoriz, AmberGoldSoft, InkBlack)
        lower.contains("paycheck") || lower.contains("income") ->
            Triple(Icons.Outlined.Work, OceanMistSoft, Color(0xFF0F766E))
        lower.contains("rent") || lower.contains("housing") ->
            Triple(Icons.Outlined.Home, OceanMistSoft, Color(0xFF0F766E))
        lower.contains("uber") || lower.contains("transportation") ->
            Triple(Icons.Outlined.DirectionsCar, BlueSoft, Color(0xFF2563EB))
        lower.contains("groceries") || lower.contains("whole foods") ->
            Triple(Icons.Outlined.Storefront, AmberGoldSoft, Color(0xFFD97706))
        lower.contains("starbucks") || lower.contains("coffee") ->
            Triple(Icons.Outlined.LocalCafe, ExpenseRedSoft, ExpenseRed)
        lower.contains("chipotle") || lower.contains("food") || lower.contains("dining") ->
            Triple(Icons.Outlined.Restaurant, ExpenseRedSoft, ExpenseRed)
        lower.contains("shopping") ->
            Triple(Icons.Outlined.ShoppingBag, AmberGoldSoft, Color(0xFFD97706))
        else ->
            Triple(Icons.Outlined.Receipt, Color(0xFFF1F5F9), CharcoalBlue)
    }
}
