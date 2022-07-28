package com.fthiery.catalog.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController

val lightTheme = lightColors(
    primary = Color(255, 138, 101, 255),
    onPrimary = Color.White,
    secondary = Color(102, 187, 106, 255),
    onSecondary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
)

val darkTheme = darkColors(
)

val shapes = Shapes(
    large = RoundedCornerShape(16.dp)
)

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
        shapes = shapes,
        content = content
    )
}