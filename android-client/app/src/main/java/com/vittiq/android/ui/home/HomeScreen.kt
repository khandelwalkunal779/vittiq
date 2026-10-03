package com.vittiq.android.ui.home

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vittiq.android.theme.AmberGold
import com.vittiq.android.theme.AmberGoldSoft
import com.vittiq.android.theme.BrightSnow
import com.vittiq.android.theme.CardBorder
import com.vittiq.android.theme.CardBorderSubtle
import com.vittiq.android.theme.CharcoalBlue
import com.vittiq.android.theme.DarkHeroCard
import com.vittiq.android.theme.InkBlack
import com.vittiq.android.theme.OceanMist
import com.vittiq.android.theme.OceanMistSoft
import com.vittiq.android.theme.SurfaceWhite
import com.vittiq.android.theme.TextMuted
import com.vittiq.android.ui.components.Formatters
import java.util.Locale

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onAddClick: () -> Unit,
    onSeeAllAccountsClick: () -> Unit = {},
    onCategoryClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BrightSnow)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Hero Net Worth Card (spans full width)
            item(span = { GridItemSpan(2) }) {
                HeroNetWorthCard(
                    totalNetWorth = uiState.totalNetWorth,
                    trendPercentage = uiState.monthlyTrendPercentage,
                    income = uiState.monthlyIncome,
                    expenses = uiState.monthlyExpenses,
                    savings = uiState.monthlySavings
                )
            }

            // 2. Section Header: "My Accounts" & "See all >"
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Accounts",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkBlack
                    )

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                            .background(SurfaceWhite)
                            .clickable { onSeeAllAccountsClick() }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "See all",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CharcoalBlue
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "See all",
                            tint = CharcoalBlue,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }

            // 3. 2-Column Category Grid
            items(uiState.categories, key = { it.category.id }) { item ->
                CategoryGridCard(
                    item = item,
                    onClick = { onCategoryClick(item.category.id) }
                )
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
    }
}

@Composable
private fun HeroNetWorthCard(
    totalNetWorth: Double,
    trendPercentage: Double?,
    income: Double,
    expenses: Double,
    savings: Double
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkHeroCard),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            // Label
            Text(
                text = "TOTAL NET WORTH",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Net Worth Balance with styled cents
            val (integerPart, fractionalPart) = Formatters.splitInr(totalNetWorth)
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "₹",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SurfaceWhite,
                    modifier = Modifier.padding(bottom = 6.dp, end = 4.dp)
                )
                Text(
                    text = integerPart,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SurfaceWhite
                )
                Text(
                    text = fractionalPart,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextMuted,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // Month-over-month trend pill (Hidden if no prior month transactions or prior net worth was 0)
            if (trendPercentage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                val isPositive = trendPercentage >= 0.0
                val formattedPercent = String.format(Locale.US, "%.1f", kotlin.math.abs(trendPercentage))
                val arrow = if (isPositive) "↗ +" else "↘ -"
                val pillText = "$arrow$formattedPercent% this month"
                val pillColor = if (isPositive) OceanMist else AmberGold
                val pillBg = if (isPositive) Color(0xFF133838) else Color(0xFF382A13)

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(pillBg)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = pillText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = pillColor
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "vs last month",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Summary Row: Income | Expenses | Savings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Income
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Income",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = Formatters.formatInr(income, includeDecimals = false),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurfaceWhite
                    )
                }

                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .width(1.dp)
                        .background(Color(0xFF1F2937))
                )

                // Expenses
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = "Expenses",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = Formatters.formatInr(expenses, includeDecimals = false),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurfaceWhite
                    )
                }

                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .width(1.dp)
                        .background(Color(0xFF1F2937))
                )

                // Savings
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = "Savings",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = Formatters.formatInr(savings, includeDecimals = false),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = OceanMist
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryGridCard(
    item: HomeCategoryItem,
    onClick: () -> Unit
) {
    val iconInfo = getCategoryVisual(item.category.name)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorderSubtle, RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Icon holder + Profit/Loss badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(iconInfo.second),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconInfo.first,
                        contentDescription = item.category.name,
                        tint = iconInfo.third,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (item.isProfit) OceanMistSoft else AmberGoldSoft)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (item.isProfit) "PROFIT" else "LOSS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isProfit) Color(0xFF0F766E) else Color(0xFFB45309)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Name
            Text(
                text = item.category.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = CharcoalBlue
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Aggregated Balance
            Text(
                text = Formatters.formatInr(item.totalBalance, includeDecimals = false),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = InkBlack
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Subcategories / accounts count
            val countText = if (item.accountsCount == 1) "1 Account" else "${item.accountsCount} Accounts"
            Text(
                text = countText,
                fontSize = 11.sp,
                color = TextMuted
            )
        }
    }
}

internal fun getCategoryVisual(categoryName: String): Triple<ImageVector, Color, Color> {
    return when (categoryName.lowercase()) {
        "accounts", "bank accounts" -> Triple(Icons.Outlined.AccountBalance, Color(0xFFF1F5F9), CharcoalBlue)
        "cards", "credit cards" -> Triple(Icons.Outlined.CreditCard, Color(0xFFEFF6FF), Color(0xFF2563EB))
        "cash" -> Triple(Icons.Outlined.AccountBalanceWallet, AmberGoldSoft, Color(0xFFD97706))
        "investments" -> Triple(Icons.AutoMirrored.Outlined.TrendingUp, OceanMistSoft, Color(0xFF0F766E))
        "e-wallets", "wallets" -> Triple(Icons.Outlined.Wallet, Color(0xFFF5F3FF), Color(0xFF7C3AED))
        else -> Triple(Icons.Outlined.Category, Color(0xFFF1F5F9), CharcoalBlue)
    }
}
