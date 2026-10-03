package com.vittiq.android.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vittiq.android.data.model.TransactionCategory
import com.vittiq.android.ui.logs.getCategoryVisuals

private val InkBlack = Color(0xFF111827)
private val CharcoalBlue = Color(0xFF334155)
private val OceanMist = Color(0xFF14B8A6)
private val AmberGold = Color(0xFFFBBF24)
private val BrightSnow = Color(0xFFF9FAFB)
private val SurfaceWhite = Color(0xFFFFFFFF)
private val BorderStroke = Color(0xFFE2E8F0)
private val DangerRed = Color(0xFFEF4444)

@Composable
fun ManageTransactionCategoriesScreen(
    categories: List<TransactionCategory>,
    onAddCategory: (String) -> Unit,
    onUpdateCategory: (TransactionCategory) -> Unit,
    onArchiveCategory: (String) -> Unit,
    onUnarchiveCategory: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeCategories = remember(categories) { categories.filter { !it.isArchived } }
    val archivedCategories = remember(categories) { categories.filter { it.isArchived } }

    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<TransactionCategory?>(null) }
    var archivingCategory by remember { mutableStateOf<TransactionCategory?>(null) }

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
                        text = "Transaction Categories",
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
                // Active Categories Section
                item {
                    Text(
                        text = "ACTIVE CATEGORIES (${activeCategories.size})",
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
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        activeCategories.forEachIndexed { index, cat ->
                            val visuals = getCategoryVisuals(cat.name, "")
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
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
                                            .background(visuals.second),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = visuals.first,
                                            contentDescription = null,
                                            tint = visuals.third,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = cat.name,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = InkBlack
                                            )
                                            if (cat.isDefault) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(Color(0xFFF1F5F9))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "Default",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = CharcoalBlue
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { editingCategory = cat },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Category",
                                            tint = CharcoalBlue,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { archivingCategory = cat },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Archive,
                                            contentDescription = "Archive Category",
                                            tint = DangerRed,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            if (index < activeCategories.lastIndex) {
                                HorizontalDivider(color = Color(0xFFF8FAFC))
                            }
                        }
                    }
                }

                // Archived Categories Section
                if (archivedCategories.isNotEmpty()) {
                    item {
                        Text(
                            text = "ARCHIVED CATEGORIES (${archivedCategories.size})",
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
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            archivedCategories.forEachIndexed { index, cat ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp),
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
                                                .background(Color(0xFFF1F5F9)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Archive,
                                                contentDescription = null,
                                                tint = CharcoalBlue.copy(alpha = 0.6f),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = cat.name,
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Normal,
                                                    color = CharcoalBlue.copy(alpha = 0.6f)
                                                )
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
                                    }

                                    IconButton(
                                        onClick = { onUnarchiveCategory(cat.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Unarchive,
                                            contentDescription = "Restore Category",
                                            tint = OceanMist,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                if (index < archivedCategories.lastIndex) {
                                    HorizontalDivider(color = Color(0xFFF8FAFC))
                                }
                            }
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
            title = { Text("New Transaction Category", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = catName,
                    onValueChange = { catName = it },
                    label = { Text("Category Name") },
                    placeholder = { Text("e.g. Subscriptions, Travel") },
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

    // Archive Confirmation Dialog
    archivingCategory?.let { cat ->
        AlertDialog(
            onDismissRequest = { archivingCategory = null },
            title = { Text("Archive Category?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Are you sure you want to archive '${cat.name}'?\n\nExisting transactions will keep this category, but it will be hidden from new transaction entries.",
                    color = CharcoalBlue
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onArchiveCategory(cat.id)
                        archivingCategory = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed, contentColor = SurfaceWhite)
                ) {
                    Text("Archive", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { archivingCategory = null }) {
                    Text("Cancel", color = CharcoalBlue)
                }
            },
            containerColor = SurfaceWhite
        )
    }
}
