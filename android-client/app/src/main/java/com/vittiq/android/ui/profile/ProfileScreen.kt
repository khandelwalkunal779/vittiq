package com.vittiq.android.ui.profile

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BrightSnow),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 96.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Avatar & Identity Block
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Circular Avatar: Amber Gold outer ring + dark core + white initial
                Box(
                    modifier = Modifier
                        .size(104.dp)
                        .clip(CircleShape)
                        .background(AmberGold),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(CharcoalBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.userProfile?.avatarInitial ?: "K",
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold,
                            color = SurfaceWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Display Name
                Text(
                    text = uiState.userProfile?.firstName ?: "Kunal",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkBlack
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Handle
                Text(
                    text = uiState.userProfile?.handle ?: "@kunal.khandelwal",
                    fontSize = 14.sp,
                    color = CharcoalBlue
                )
            }
        }

        // 2. Sync Status Card
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(OceanMistSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = null,
                                tint = OceanMist,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Login and sync coming soon",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = InkBlack
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Your profile is ready. Secure sign-in and connected account syncing will arrive in the next update.",
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                color = CharcoalBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Two side-by-side status sub-cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatusSubCard(
                            label = "Login",
                            status = "Coming soon",
                            modifier = Modifier.weight(1f)
                        )
                        StatusSubCard(
                            label = "Sync",
                            status = "Coming soon",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Highlight Info Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFE8FAF6))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = Color(0xFF0F766E),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Keep this screen handy for when secure login and connected account syncing are released.",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = CharcoalBlue
                        )
                    }
                }
            }
        }

        // 3. Settings Card Entry (Navigates to dedicated SettingsScreen)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorderSubtle, RoundedCornerShape(20.dp))
                    .clickable { onNavigateToSettings() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(AmberGoldSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Settings & Preferences",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = InkBlack
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Manage accounts, categories & exchange rates",
                            fontSize = 12.sp,
                            color = CharcoalBlue
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = CharcoalBlue.copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // 4. Primary Action Button
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { /* Sign in handler */ },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberGold,
                        contentColor = InkBlack
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        text = "Sign in to unlock sync",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Available in the next release",
                    fontSize = 12.sp,
                    color = CharcoalBlue,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun StatusSubCard(
    label: String,
    status: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(BrightSnow)
            .border(1.dp, CardBorderSubtle, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = CharcoalBlue
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = status,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = InkBlack
        )
    }
}

