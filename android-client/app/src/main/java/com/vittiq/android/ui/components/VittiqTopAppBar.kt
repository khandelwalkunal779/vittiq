package com.vittiq.android.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vittiq.android.R
import com.vittiq.android.data.model.Transaction
import com.vittiq.android.theme.BrightSnow
import com.vittiq.android.theme.CardBorder
import com.vittiq.android.theme.CardBorderSubtle
import com.vittiq.android.theme.CharcoalBlue
import com.vittiq.android.theme.InkBlack
import com.vittiq.android.theme.SurfaceWhite
import com.vittiq.android.ui.logs.TransactionRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VittiqTopAppBar(
    userName: String = "Kunal",
    greeting: String = "Good morning,",
    showSettingsAction: Boolean = false,
    onSettingsClick: () -> Unit = {},
    isSearchActive: Boolean = false,
    onSearchActiveChange: (Boolean) -> Unit = {},
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    searchResults: List<Transaction> = emptyList(),
    modifier: Modifier = Modifier
) {
    if (isSearchActive) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(BrightSnow)
                .statusBarsPadding()
        ) {
            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = searchQuery,
                        onQueryChange = onSearchQueryChange,
                        onSearch = { /* Real-time search */ },
                        expanded = isSearchActive,
                        onExpandedChange = onSearchActiveChange,
                        placeholder = {
                            Text(
                                text = "Search transactions...",
                                color = CharcoalBlue.copy(alpha = 0.6f),
                                fontSize = 15.sp
                            )
                        },
                        leadingIcon = {
                            IconButton(onClick = { onSearchActiveChange(false) }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Close search",
                                    tint = InkBlack
                                )
                            }
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear search",
                                        tint = CharcoalBlue
                                    )
                                }
                            }
                        }
                    )
                },
                expanded = isSearchActive,
                onExpandedChange = onSearchActiveChange,
                colors = SearchBarDefaults.colors(
                    containerColor = SurfaceWhite,
                    dividerColor = CardBorderSubtle
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (searchResults.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isBlank()) {
                                "Type to search by name, category, or note"
                            } else {
                                "No transactions found matching '$searchQuery'"
                            },
                            color = CharcoalBlue,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(searchResults, key = { it.id }) { tx ->
                            TransactionRow(transaction = tx)
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
    } else {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(BrightSnow)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Circular brand avatar with Vittiq emblem
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(InkBlack),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_vittiq_logo),
                    contentDescription = "Vittiq Logo",
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Two-line text column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = greeting,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = CharcoalBlue
                )
                Text(
                    text = userName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkBlack
                )
            }

            // Right Action buttons with balanced spacing and isolated touch targets
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Optional settings button (e.g. for profile or global access)
                if (showSettingsAction) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SurfaceWhite)
                            .border(1.dp, CardBorder, CircleShape)
                            .clickable(onClick = onSettingsClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = InkBlack,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Circular outlined action button with magnifying glass
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SurfaceWhite)
                        .border(1.dp, CardBorder, CircleShape)
                        .clickable(onClick = { onSearchActiveChange(true) }),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search",
                        tint = InkBlack,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
