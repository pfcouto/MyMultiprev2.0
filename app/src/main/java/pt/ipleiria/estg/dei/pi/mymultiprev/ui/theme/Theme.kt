package pt.ipleiria.estg.dei.pi.mymultiprev.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

data class MyColors(
    val material: Colors,
    val darkRed: Color,
    val darkGreen: Color,
    val messageOverdue: Color,
    val gray: Color
) {
    val primary: Color get() = material.primary
    val primaryVariant: Color get() = material.primaryVariant
    val secondary: Color get() = material.secondary
    val secondaryVariant: Color get() = material.secondaryVariant
    val background: Color get() = material.background
    val surface: Color get() = material.surface
    val error: Color get() = material.error
    val onPrimary: Color get() = material.onPrimary
    val onSecondary: Color get() = material.onSecondary
    val onBackground: Color get() = material.onBackground
    val onSurface: Color get() = material.onSurface
    val onError: Color get() = material.onError
    val isLight: Boolean get() = material.isLight
}

private val LightColorPalette = MyColors(
    material = lightColors(primary = Teal),
    darkRed = DarkRed,
    darkGreen = DarkGreen,
    messageOverdue = MessageOverdue,
    gray = Gray
)
private val DarkColorPalette = MyColors(
    material = darkColors(
        background = Color(0xFF1D1D1D)
    ),
    darkRed = DarkRed,
    darkGreen = DarkGreen,
    messageOverdue = MessageOverdue,
    gray = Gray
)

val MaterialTheme.myColors: MyColors
    @Composable
    @ReadOnlyComposable
    get() = LocalColors.current

val LocalColors = compositionLocalOf<MyColors> { error("No colors found!") }


@Composable
fun MyMultiPrevTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = Teal
    )
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }
    CompositionLocalProvider(
        LocalColors provides colors
    ) {
        MaterialTheme(
            colors = colors.material,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}