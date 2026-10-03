package com.vittiq.android.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.SyncAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vittiq.android.theme.AmberGold
import com.vittiq.android.theme.AmberGoldSoft
import com.vittiq.android.theme.BrightSnow
import com.vittiq.android.theme.CardBorderSubtle
import com.vittiq.android.theme.CharcoalBlue
import com.vittiq.android.theme.InkBlack
import com.vittiq.android.theme.OceanMist
import com.vittiq.android.theme.OceanMistSoft
import com.vittiq.android.theme.SurfaceWhite
import com.vittiq.android.ui.ai.RoadmapRow

@Composable
fun SharedScreen(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BrightSnow),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Concept Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorderSubtle, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Concentric Gradient Rings with Group Icon
                    SharedConcentricIllustration()

                    Spacer(modifier = Modifier.height(20.dp))

                    // Coming soon pill badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(AmberGoldSoft)
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Coming soon",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD97706)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Title
                    Text(
                        text = "Shared Expenses",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = InkBlack
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Subtitle description
                    Text(
                        text = "Effortlessly split bills, track shared balances with friends, and settle up with zero hassle.",
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        textAlign = TextAlign.Center,
                        color = CharcoalBlue,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // 2. Feature Roadmap Card ("What to expect")
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorderSubtle, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "What to expect",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkBlack
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Feature 1: Teal Splits
                    RoadmapRow(
                        icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                        iconTint = Color(0xFF0D9488),
                        iconBackground = OceanMistSoft,
                        text = "Split group expenses and dining checks evenly or by exact custom shares."
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Feature 2: Amber IOU Tracking
                    RoadmapRow(
                        icon = Icons.Outlined.SyncAlt,
                        iconTint = Color(0xFFD97706),
                        iconBackground = AmberGoldSoft,
                        text = "Track multi-person IOUs and simplify complex balances across your friend circle."
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Feature 3: Gray Settle Up
                    RoadmapRow(
                        icon = Icons.Outlined.CheckCircleOutline,
                        iconTint = Color(0xFF475569),
                        iconBackground = Color(0xFFF1F5F9),
                        text = "Instantly settle up debts and record payouts directly into your accounts ledger."
                    )
                }
            }
        }
    }
}

@Composable
private fun SharedConcentricIllustration(
    modifier: Modifier = Modifier
) {
    val ringGradient = Brush.sweepGradient(
        listOf(
            AmberGold,
            OceanMist,
            AmberGold
        )
    )

    val coreGradient = Brush.linearGradient(
        listOf(
            OceanMist,
            AmberGold
        )
    )

    Box(
        modifier = modifier.size(190.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer Gradient Ring
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(ringGradient)
        )

        // Middle White Ring
        Box(
            modifier = Modifier
                .size(136.dp)
                .clip(CircleShape)
                .background(SurfaceWhite)
        )

        // Inner Core Ring with Group Icon
        Box(
            modifier = Modifier
                .size(86.dp)
                .clip(CircleShape)
                .background(coreGradient),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Group,
                contentDescription = null,
                tint = InkBlack,
                modifier = Modifier.size(34.dp)
            )
        }
    }
}
