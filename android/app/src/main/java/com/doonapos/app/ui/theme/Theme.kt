package com.doonapos.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PineGreen,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = PineGreenContainer,
    onPrimaryContainer = OnPineGreenContainer,
    secondary = SecondaryContainer,
    onSecondary = OnSecondaryContainer,
    tertiary = Ochre,
    onTertiary = OnOchre,
    background = SurfaceBase,
    surface = SurfacePanel,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = OutlineColor,
    outlineVariant = OutlineVariant,
    surfaceContainerHigh = SurfaceContainerHigh
)

@Composable
fun DoonaPOSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = PineGreen.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
