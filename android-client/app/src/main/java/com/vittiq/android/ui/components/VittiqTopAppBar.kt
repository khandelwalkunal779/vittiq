package com.vittiq.android.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.vittiq.android.theme.BrightSnow
import com.vittiq.android.theme.CardBorder
import com.vittiq.android.theme.CharcoalBlue
import com.vittiq.android.theme.InkBlack
import com.vittiq.android.theme.SurfaceWhite

@Composable
fun VittiqTopAppBar(
    userName: String = "Kunal",
    greeting: String = "Good morning,",
    onSearchClick: () -> Unit = {},
    showSettingsAction: Boolean = false,
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
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

        // Optional settings button (e.g. for profile / currency rates)
        if (showSettingsAction) {
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(SurfaceWhite)
                    .border(1.dp, CardBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Currency Settings",
                    tint = InkBlack,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        // Right: Circular outlined action button with magnifying glass
        IconButton(
            onClick = onSearchClick,
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(SurfaceWhite)
                .border(1.dp, CardBorder, CircleShape)
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
