package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val Challenge3DColorScheme = darkColorScheme(
    primary = PurpleMain,
    onPrimary = TextMain,
    primaryContainer = PurpleDeepLedge,
    onPrimaryContainer = TextMain,
    secondary = PinkMain,
    onSecondary = TextMain,
    secondaryContainer = PinkDeepLedge,
    onSecondaryContainer = TextMain,
    tertiary = GoldMain,
    onTertiary = BgGrad2,
    tertiaryContainer = TealMain,
    onTertiaryContainer = BgGrad1,
    background = BgGrad1,
    onBackground = TextMain,
    surface = SurfaceDark,
    onSurface = TextMain,
    surfaceVariant = SurfaceCardDark,
    onSurfaceVariant = TextMuted
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = Challenge3DColorScheme,
            typography = Typography,
            content = content
        )
    }
}
