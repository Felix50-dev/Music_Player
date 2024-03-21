package hoods.com.jetaudio.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val myColorsLight = lightColorScheme(
    primary = primaryColor,
    secondary = secondaryColor,
    onPrimary = onPrimary,
    onSecondary = onSecondary,
    surface = surfaceLightColor,
    background = backgroundColorLight,
    onSurface = onSurfaceLight,
    onBackground = onBackgroundLight,
    onError = onError
)

private val myColorsDark = darkColorScheme(
    primary = primaryColor,
    secondary = secondaryColor,
    onPrimary = onPrimary,
    onSecondary = onSecondary,
    surface = surfaceDark,
    background = backgroundColorDark,
    onSurface = onSurfaceDark,
    onBackground = onBackgroundDark,
    onError = onError
)

@Composable
fun JetAudioTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (!useDarkTheme) {
        myColorsLight
    } else {
        myColorsDark
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}