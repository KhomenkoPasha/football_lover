package khom.pavlo.footballlover.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GreenA200,
    onPrimary = Color(0xFF04210A),
    primaryContainer = Green700,
    onPrimaryContainer = Color(0xFFE8F8EA),
    secondary = Green300,
    onSecondary = Color(0xFF08210D),
    secondaryContainer = Green800,
    onSecondaryContainer = Green100,
    tertiary = GreenA200,
    onTertiary = Color(0xFF1B1F12),
    tertiaryContainer = Green600,
    onTertiaryContainer = Green50,
    background = GreenBackgroundDark,
    onBackground = GreenOnSurfaceDark,
    surface = GreenSurfaceDark,
    onSurface = GreenOnSurfaceDark,
    surfaceVariant = GreenSurfaceVariantDark,
    onSurfaceVariant = GreenOnSurfaceVariantDark,
    outline = GreenOutlineDark,
    surfaceTint = Green300
)

private val LightColorScheme = lightColorScheme(
    primary = Green800,
    onPrimary = Color.White,
    primaryContainer = Green200,
    onPrimaryContainer = Green900,
    secondary = Green700,
    onSecondary = Color.White,
    secondaryContainer = Green100,
    onSecondaryContainer = Green900,
    tertiary = Green600,
    onTertiary = Color.White,
    tertiaryContainer = Green200,
    onTertiaryContainer = Green900,
    background = GreenBackgroundLight,
    onBackground = GreenOnSurfaceLight,
    surface = GreenSurfaceLight,
    onSurface = GreenOnSurfaceLight,
    surfaceVariant = GreenSurfaceVariantLight,
    onSurfaceVariant = GreenOnSurfaceVariantLight,
    outline = GreenOutlineLight,
    surfaceTint = Green800
)

@Composable
fun FootballLoverTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
