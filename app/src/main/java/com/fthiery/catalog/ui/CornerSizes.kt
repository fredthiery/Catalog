package com.fthiery.catalog.ui

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun cornerSizes(all: Dp = 0.dp) = cornerSizes(others = all)

fun cornerSizes(
    topStart: Dp? = null,
    topEnd: Dp? = null,
    bottomEnd: Dp? = null,
    bottomStart: Dp? = null,
    others: Dp = 0.dp
): List<CornerSize> {
    return listOf(
        CornerSize(topStart?:others),
        CornerSize(topEnd?:others),
        CornerSize(bottomEnd?:others),
        CornerSize(bottomStart?:others)
    )
}