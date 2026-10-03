package com.vittiq.android.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val VittiqLightColorScheme = lightColorScheme(
    primary = AmberGold,
    onPrimary = InkBlack,
    primaryContainer = AmberGoldSoft,
    onPrimaryContainer = InkBlack,
    secondary = OceanMist,
    onSecondary = SurfaceWhite,
    secondaryContainer = OceanMistSoft,
    onSecondaryContainer = InkBlack,
    background = BrightSnow,
    onBackground = InkBlack,
    surface = SurfaceWhite,
    onSurface = InkBlack,
    surfaceVariant = BrightSnow,
    onSurfaceVariant = CharcoalBlue,
    outline = CardBorder,
    outlineVariant = CardBorderSubtle,
    error = ExpenseRed,
    errorContainer = ExpenseRedSoft,
    onError = SurfaceWhite,
    onErrorContainer = ExpenseRed
)

@Composable
fun VittiqTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Vittiq uses the custom designed high-contrast Light/Card theme defined in design specs
    val colorScheme = VittiqLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

