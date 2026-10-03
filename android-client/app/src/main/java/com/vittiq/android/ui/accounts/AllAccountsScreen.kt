package com.vittiq.android.ui.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vittiq.android.data.model.AccountCategory
import com.vittiq.android.ui.components.Formatters

private val InkBlack = Color(0xFF111827)
private val CharcoalBlue = Color(0xFF334155)
private val OceanMist = Color(0xFF14B8A6)
private val AmberGold = Color(0xFFFBBF24)
private val BrightSnow = Color(0xFFF9FAFB)
private val SurfaceWhite = Color(0xFFFFFFFF)
private val BorderStroke = Color(0xFFE2E8F0)

@Composable
fun AllAccountsScreen(
    uiState: AllAccountsUiState,
    initialCategoryId: String? = null,
    onBackClick: () -> Unit,
    onSelectCategory: (String?) -> Unit,
    onAddAccount: (categoryId: String, name: String, initialBalance: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var presetCategoryId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(initialCategoryId) {
        if (initialCategoryId != null) {
            onSelectCategory(initialCategoryId)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrightSnow,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    presetCategoryId = uiState.selectedCategoryId ?: uiState.allCategories.firstOrNull()?.id
                    showAddDialog = true
                },
                containerColor = AmberGold,
                contentColor = InkBlack,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Account"
                )
            }
        }
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

                Column {
                    Text(
                        text = "All Accounts",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkBlack
                    )
                    Text(
                        text = "Total Balance: ${Formatters.formatInr(uiState.totalBalance)}",
                        fontSize = 13.sp,
                        color = CharcoalBlue
                    )
                }
            }

            // Category Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val isAllSelected = uiState.selectedCategoryId == null
                CategoryFilterChip(
                    text = "All",
                    isSelected = isAllSelected,
                    onClick = { onSelectCategory(null) }
                )

                uiState.allCategories.forEach { category ->
                    val isSelected = uiState.selectedCategoryId == category.id
                    CategoryFilterChip(
                        text = category.name,
                        isSelected = isSelected,
                        onClick = { onSelectCategory(category.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Grouped Accounts List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.categoriesWithAccounts, key = { it.category.id }) { cwa ->
                    val catTotal = cwa.accounts.sumOf { it.currentBalance }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(SurfaceWhite)
                            .border(1.dp, BorderStroke, RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        // Category Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(getCategoryBgColor(cwa.category.name)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = getCategoryIcon(cwa.category.name),
                                        contentDescription = cwa.category.name,
                                        tint = InkBlack,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = cwa.category.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = InkBlack
                                    )
                                    Text(
                                        text = "${cwa.accounts.size} active accounts",
                                        fontSize = 12.sp,
                                        color = CharcoalBlue
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = Formatters.formatInr(catTotal),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = InkBlack
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = {
                                        presetCategoryId = cwa.category.id
                                        showAddDialog = true
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add account to ${cwa.category.name}",
                                        tint = CharcoalBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        if (cwa.accounts.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(8.dp))

                            cwa.accounts.forEachIndexed { index, account ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = account.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = InkBlack
                                    )
                                    Text(
                                        text = Formatters.formatInr(account.currentBalance),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = InkBlack
                                    )
                                }
                                if (index < cwa.accounts.lastIndex) {
                                    HorizontalDivider(color = Color(0xFFF8FAFC))
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No active accounts in this category",
                                fontSize = 13.sp,
                                color = CharcoalBlue.copy(alpha = 0.6f),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    if (showAddDialog) {
        AddAccountDialog(
            categories = uiState.allCategories,
            initialCategoryId = presetCategoryId,
            onDismiss = { showAddDialog = false },
            onConfirm = { catId, name, initBal ->
                onAddAccount(catId, name, initBal)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun CategoryFilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (isSelected) AmberGold else SurfaceWhite)
            .border(
                width = 1.dp,
                color = if (isSelected) AmberGold else BorderStroke,
                shape = CircleShape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = InkBlack
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountDialog(
    categories: List<AccountCategory>,
    initialCategoryId: String?,
    onDismiss: () -> Unit,
    onConfirm: (categoryId: String, name: String, initialBalance: Double) -> Unit
) {
    var selectedCategory by remember {
        mutableStateOf(
            categories.find { it.id == initialCategoryId } ?: categories.firstOrNull()
        )
    }
    var accountName by remember { mutableStateOf("") }
    var initialBalanceText by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add New Account",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = InkBlack
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Category Picker
                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory?.name ?: "Select Category",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategory = category
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Account Name
                OutlinedTextField(
                    value = accountName,
                    onValueChange = { accountName = it },
                    label = { Text("Account Name") },
                    placeholder = { Text("e.g. ICICI Bank, Cash Wallet") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Initial Balance
                OutlinedTextField(
                    value = initialBalanceText,
                    onValueChange = { initialBalanceText = it },
                    label = { Text("Initial Balance (₹)") },
                    placeholder = { Text("0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cat = selectedCategory
                    val name = accountName.trim()
                    val initBal = initialBalanceText.toDoubleOrNull() ?: 0.0
                    if (cat != null && name.isNotBlank()) {
                        onConfirm(cat.id, name, initBal)
                    }
                },
                enabled = selectedCategory != null && accountName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmberGold,
                    contentColor = InkBlack
                )
            ) {
                Text("Add Account", fontWeight = FontWeight.Bold)
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

fun getCategoryIcon(categoryName: String): ImageVector {
    return when (categoryName.lowercase()) {
        "accounts", "bank accounts" -> Icons.Default.AccountBalance
        "cards", "credit cards" -> Icons.Default.CreditCard
        "cash" -> Icons.Default.Payments
        "investments" -> Icons.Default.TrendingUp
        "e-wallets", "wallets" -> Icons.Default.AccountBalanceWallet
        else -> Icons.Default.MoreHoriz
    }
}

fun getCategoryBgColor(categoryName: String): Color {
    return when (categoryName.lowercase()) {
        "accounts", "bank accounts" -> Color(0xFFE0F2FE)
        "cards", "credit cards" -> Color(0xFFFEE2E2)
        "cash" -> Color(0xFFDCFCE7)
        "investments" -> Color(0xFFFEF3C7)
        "e-wallets", "wallets" -> Color(0xFFF3E8FF)
        else -> Color(0xFFF1F5F9)
    }
}
