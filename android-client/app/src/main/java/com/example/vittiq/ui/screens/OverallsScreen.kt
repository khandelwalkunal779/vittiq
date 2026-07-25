package com.example.vittiq.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vittiq.data.db.Account
import com.example.vittiq.data.db.AccountType
import com.example.vittiq.ui.theme.DebitRed
import com.example.vittiq.viewmodel.OverallsViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverallsScreen(
    viewModel: OverallsViewModel = viewModel(factory = OverallsViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showAddAccountSheet by remember { mutableStateOf(false) }

    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddAccountSheet = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Account")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Account", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Net Worth Hero Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "TOTAL NET WORTH",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currencyFormat.format(uiState.netWorth),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Cash Accounts Section
            item {
                AccountCategorySection(
                    title = "Cash & Wallets",
                    totalBalance = uiState.cashTotal,
                    accounts = uiState.cashAccounts,
                    currencyFormat = currencyFormat,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    onContainerColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Bank Accounts Section
            item {
                AccountCategorySection(
                    title = "Bank Accounts",
                    totalBalance = uiState.bankTotal,
                    accounts = uiState.bankAccounts,
                    currencyFormat = currencyFormat,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onContainerColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            // Cards Section
            item {
                AccountCategorySection(
                    title = "Cards & Credit",
                    totalBalance = uiState.cardTotal,
                    accounts = uiState.cardAccounts,
                    currencyFormat = currencyFormat,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    onContainerColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }

            // Investments Section
            item {
                AccountCategorySection(
                    title = "Investments",
                    totalBalance = uiState.investmentTotal,
                    accounts = uiState.investmentAccounts,
                    currencyFormat = currencyFormat,
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    onContainerColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(80.dp)) // Extra space for FAB
            }
        }
    }

    if (showAddAccountSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddAccountSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            AddAccountSheetContent(
                onDismiss = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showAddAccountSheet = false
                    }
                },
                onSave = { name, type, bal ->
                    viewModel.addAccount(name, type, bal)
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showAddAccountSheet = false
                    }
                }
            )
        }
    }
}

@Composable
fun AccountCategorySection(
    title: String,
    totalBalance: Double,
    accounts: List<Account>,
    currencyFormat: NumberFormat,
    containerColor: Color,
    onContainerColor: Color
) {
    var isExpanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand",
                        tint = onContainerColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = onContainerColor
                    )
                }
                Text(
                    text = currencyFormat.format(totalBalance),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (totalBalance < 0) DebitRed else onContainerColor
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (accounts.isEmpty()) {
                        Text(
                            text = "No accounts in this category yet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = onContainerColor.copy(alpha = 0.6f),
                            modifier = Modifier.padding(start = 32.dp, top = 4.dp, bottom = 4.dp)
                        )
                    } else {
                        accounts.forEach { account ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = account.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = currencyFormat.format(account.currentBalance),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (account.currentBalance < 0) DebitRed else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddAccountSheetContent(
    onDismiss: () -> Unit,
    onSave: (String, AccountType, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(AccountType.BANK_ACCOUNT) }
    var balanceStr by remember { mutableStateOf("") }
    var typeDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add New Account",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Account Name (e.g. Vacation Fund)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Account Type Selector
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = when(selectedType) {
                    AccountType.CASH -> "Cash / Wallet"
                    AccountType.BANK_ACCOUNT -> "Bank Account"
                    AccountType.CARD -> "Credit / Debit Card"
                    AccountType.INVESTMENT -> "Investment Account"
                },
                onValueChange = {},
                readOnly = true,
                label = { Text("Account Category") },
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { typeDropdownExpanded = true },
                shape = RoundedCornerShape(12.dp)
            )
            DropdownMenu(
                expanded = typeDropdownExpanded,
                onDismissRequest = { typeDropdownExpanded = false },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                AccountType.values().forEach { type ->
                    DropdownMenuItem(
                        text = {
                            Text(when(type) {
                                AccountType.CASH -> "Cash / Wallet"
                                AccountType.BANK_ACCOUNT -> "Bank Account"
                                AccountType.CARD -> "Credit / Debit Card"
                                AccountType.INVESTMENT -> "Investment Account"
                            })
                        },
                        onClick = {
                            selectedType = type
                            typeDropdownExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = balanceStr,
            onValueChange = { balanceStr = it },
            label = { Text("Initial Balance ($)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel", fontWeight = FontWeight.SemiBold)
            }
            Button(
                onClick = {
                    val bal = balanceStr.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank()) {
                        onSave(name, selectedType, bal)
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Account", fontWeight = FontWeight.Bold)
            }
        }
    }
}
