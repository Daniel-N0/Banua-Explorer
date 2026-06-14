package com.example.banuaexplorer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(

    primary = BanuaGreenDark,

    secondary = BanuaSecondaryDark,

    tertiary = BanuaAccentDark,

    background = BackgroundDark,

    surface = SurfaceDark,

    onPrimary = Color.Black,

    onBackground = TextPrimaryDark,

    onSurface = TextPrimaryDark

)

private val LightColorScheme = lightColorScheme(

    primary = BanuaGreen,

    secondary = BanuaSecondary,

    tertiary = BanuaAccent,

    background = BackgroundLight,

    surface = SurfaceLight,

    onPrimary = Color.White,

    onBackground = TextPrimaryLight,

    onSurface = TextPrimaryLight

)

@Composable
fun BanuaExplorerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme =
        if (darkTheme) {
            DarkColorScheme
        } else {
            LightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}