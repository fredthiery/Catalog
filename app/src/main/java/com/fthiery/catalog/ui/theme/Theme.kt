package com.fthiery.catalog.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fthiery.catalog.ui.baselevel.Angles
import com.google.accompanist.systemuicontroller.rememberSystemUiController

val lightTheme = lightColors(
    primary = Color(38, 198, 218, 255),
    primaryVariant = Color(0, 150, 136, 255),
    secondary = Color(79, 195, 247, 255),
    onPrimary = Color.White,
    onSecondary = Color(29, 58, 80, 255),
    background = Color(244, 245, 249, 255),
    onBackground = Color.Black,
)
val Colors.scrimColor: Color
    get() = Color(0, 0, 0, 120)

val darkTheme = darkColors(
    primary = Color(0, 151, 167, 255),
    primaryVariant = Color(0, 137, 123, 255),
    secondary = Color(2, 119, 189, 255),
)

val shapes = Shapes(
    large = RoundedCornerShape(16.dp)
)

val Shapes.angle: Float
    get() = -5.0f

@Composable
fun CatalogTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    val colors = if (useDarkTheme) darkTheme else lightTheme

    systemUiController.setStatusBarColor(color = Color.Transparent, darkIcons = !useDarkTheme)
    systemUiController.setNavigationBarColor(color = Color.Transparent, darkIcons = false)

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = shapes,
        content = content
    )
}