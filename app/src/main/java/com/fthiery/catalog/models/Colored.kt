package com.fthiery.catalog.models

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.fthiery.catalog.setSL

open class Colored(
    var color: Int?
) {
    @Composable
    fun backgroundColor(): Color {
        return if (isSystemInDarkTheme()) darkColor()
        else lightColor()
    }

    @Composable
    fun contentColor(): Color {
        return if (isSystemInDarkTheme()) lightColor()
        else darkColor()
    }

    @Composable
    fun lightColor(): Color {
        color?.let {
            return Color(it.setSL(.8f, .6f))
        }
        return MaterialTheme.colors.primary
    }

    @Composable
    fun darkColor(): Color {
        color?.let {
            return Color(it.setSL(.6f, .3f))
        }
        return MaterialTheme.colors.primary
    }
}