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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.vittiq.android.data.model.Account
import com.vittiq.android.data.model.AccountCategory
import com.vittiq.android.ui.accounts.getCategoryBgColor
import com.vittiq.android.ui.accounts.getCategoryIcon
import com.vittiq.android.ui.components.Formatters
import kotlinx.coroutines.launch

private val InkBlack = Color(0xFF111827)
private val CharcoalBlue = Color(0xFF334155)
private val OceanMist = Color(0xFF14B8A6)
private val AmberGold = Color(0xFFFBBF24)
private val BrightSnow = Color(0xFFF9FAFB)
private val SurfaceWhite = Color(0xFFFFFFFF)
private val BorderStroke = Color(0xFFE2E8F0)
private val DangerRed = Color(0xFFEF4444)

@Composable
fun ManageAccountsScreen(
    uiState: SettingsUiState,
    onAddCategory: (String) -> Unit,
    onUpdateCategory: (AccountCategory) -> Unit,
    onDeleteCategory: (AccountCategory) -> Unit,
    onAddAccount: (categoryId: String, name: String, initialBalance: Double) -> Unit,
    onUpdateAccount: (Account) -> Unit,
    onDeleteOrArchiveAccount: (accountId: String, onResult: (Boolean) -> Unit) -> Unit,
    onUnarchiveAccount: (accountId: String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<AccountCategory?>(null) }
    var deletingCategory by remember { mutableStateOf<AccountCategory?>(null) }

    var addingAccountForCategoryId by remember { mutableStateOf<String?>(null) }
    var editingAccount by remember { mutableStateOf<Account?>(null) }
    var accountToDeleteOrArchive by remember { mutableStateOf<Account?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrightSnow,
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                        text = "Manage Accounts",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkBlack
                    )
                }

                // Add Category Button
                Button(
                    onClick = { showAddCategoryDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberGold,
                        contentColor = InkBlack
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Category", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.categoriesWithAccounts, key = { it.category.id }) { cwa ->
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(getCategoryBgColor(cwa.category.name)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = getCategoryIcon(cwa.category.name),
                                        contentDescription = null,
                                        tint = InkBlack,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = cwa.category.name,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = InkBlack
                                        )
                                        if (cwa.category.isCustom) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Color(0xFFFEF3C7))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "Custom",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFB45309)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "${cwa.accounts.size} total accounts",
                                        fontSize = 12.sp,
                                        color = CharcoalBlue
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { editingCategory = cwa.category },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Category",
                                        tint = CharcoalBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                if (cwa.category.isCustom) {
                                    IconButton(
                                        onClick = { deletingCategory = cwa.category },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Category",
                                            tint = DangerRed,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { addingAccountForCategoryId = cwa.category.id },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add Account",
                                        tint = InkBlack,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Child Accounts
                        if (cwa.accounts.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFF1F5F9))

                            cwa.accounts.forEachIndexed { index, account ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = account.name,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = if (account.isArchived) CharcoalBlue.copy(alpha = 0.5f) else InkBlack
                                            )
                                            if (account.isArchived) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(Color(0xFFF1F5F9))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "Archived",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = CharcoalBlue
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = Formatters.formatInr(account.currentBalance),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (account.isArchived) CharcoalBlue.copy(alpha = 0.5f) else OceanMist
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (account.isArchived) {
                                            IconButton(
                                                onClick = { onUnarchiveAccount(account.id) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Unarchive,
                                                    contentDescription = "Restore Account",
                                                    tint = OceanMist,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        } else {
                                            IconButton(
                                                onClick = { editingAccount = account },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = "Edit Account",
                                                    tint = CharcoalBlue,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            IconButton(
                                                onClick = { accountToDeleteOrArchive = account },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Archive,
                                                    contentDescription = "Archive or Delete",
                                                    tint = DangerRed,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                                if (index < cwa.accounts.lastIndex) {
                                    HorizontalDivider(color = Color(0xFFF8FAFC))
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No accounts yet. Tap '+' to create one.",
                                fontSize = 12.sp,
                                color = CharcoalBlue.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }

    // Add Category Dialog
    if (showAddCategoryDialog) {
        var catName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("New Account Category", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = catName,
                    onValueChange = { catName = it },
                    label = { Text("Category Name") },
                    placeholder = { Text("e.g. Crypto, Safe Deposit") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (catName.isNotBlank()) {
                            onAddCategory(catName)
                            showAddCategoryDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = InkBlack)
                ) {
                    Text("Add", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text("Cancel", color = CharcoalBlue)
                }
            },
            containerColor = SurfaceWhite
        )
    }

    // Edit Category Dialog
    editingCategory?.let { cat ->
        var newCatName by remember { mutableStateOf(cat.name) }
        AlertDialog(
            onDismissRequest = { editingCategory = null },
            title = { Text("Edit Category Name", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newCatName,
                    onValueChange = { newCatName = it },
                    label = { Text("Category Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCatName.isNotBlank()) {
                            onUpdateCategory(cat.copy(name = newCatName.trim()))
                            editingCategory = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = InkBlack)
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingCategory = null }) {
                    Text("Cancel", color = CharcoalBlue)
                }
            },
            containerColor = SurfaceWhite
        )
    }

    // Delete Category Confirmation
    deletingCategory?.let { cat ->
        AlertDialog(
            onDismissRequest = { deletingCategory = null },
            title = { Text("Delete Category?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Are you sure you want to delete '${cat.name}'? This will also remove any accounts contained within it.",
                    color = CharcoalBlue
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteCategory(cat)
                        deletingCategory = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed, contentColor = SurfaceWhite)
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingCategory = null }) {
                    Text("Cancel", color = CharcoalBlue)
                }
            },
            containerColor = SurfaceWhite
        )
    }

    // Add Account Dialog
    addingAccountForCategoryId?.let { catId ->
        var accName by remember { mutableStateOf("") }
        var initBalStr by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { addingAccountForCategoryId = null },
            title = { Text("Add Account", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = accName,
                        onValueChange = { accName = it },
                        label = { Text("Account Name") },
                        placeholder = { Text("e.g. HDFC Bank") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = initBalStr,
                        onValueChange = { initBalStr = it },
                        label = { Text("Initial Balance (₹)") },
                        placeholder = { Text("0.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = accName.trim()
                        val bal = initBalStr.toDoubleOrNull() ?: 0.0
                        if (name.isNotBlank()) {
                            onAddAccount(catId, name, bal)
                            addingAccountForCategoryId = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = InkBlack)
                ) {
                    Text("Add", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { addingAccountForCategoryId = null }) {
                    Text("Cancel", color = CharcoalBlue)
                }
            },
            containerColor = SurfaceWhite
        )
    }

    // Edit Account Dialog
    editingAccount?.let { acc ->
        var newAccName by remember { mutableStateOf(acc.name) }
        AlertDialog(
            onDismissRequest = { editingAccount = null },
            title = { Text("Edit Account Name", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newAccName,
                    onValueChange = { newAccName = it },
                    label = { Text("Account Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newAccName.isNotBlank()) {
                            onUpdateAccount(acc.copy(name = newAccName.trim()))
                            editingAccount = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = InkBlack)
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingAccount = null }) {
                    Text("Cancel", color = CharcoalBlue)
                }
            },
            containerColor = SurfaceWhite
        )
    }

    // Safe Delete / Archive Account Dialog
    accountToDeleteOrArchive?.let { acc ->
        AlertDialog(
            onDismissRequest = { accountToDeleteOrArchive = null },
            title = { Text("Delete or Archive Account", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Are you sure you want to delete '${acc.name}'?\n\nIf this account has existing transactions, it will be safely archived rather than deleted so your ledger history remains accurate.",
                    color = CharcoalBlue
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteOrArchiveAccount(acc.id) { isDeleted ->
                            scope.launch {
                                if (isDeleted) {
                                    snackbarHostState.showSnackbar("Account '${acc.name}' deleted successfully.")
                                } else {
                                    snackbarHostState.showSnackbar("Account has transaction history and was archived safely.")
                                }
                            }
                        }
                        accountToDeleteOrArchive = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed, contentColor = SurfaceWhite)
                ) {
                    Text("Confirm", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { accountToDeleteOrArchive = null }) {
                    Text("Cancel", color = CharcoalBlue)
                }
            },
            containerColor = SurfaceWhite
        )
    }
}
