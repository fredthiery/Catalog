package com.fthiery.catalog.ui.baselevel

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun cornerSizes(
    default: Dp = 0.dp,
    topStart: Dp? = null,
    topEnd: Dp? = null,
    bottomEnd: Dp? = null,
    bottomStart: Dp? = null
) = CornerSizes(
    topStart = CornerSize(topStart ?: default),
    topEnd = CornerSize(topEnd ?: default),
    bottomEnd = CornerSize(bottomEnd ?: default),
    bottomStart = CornerSize(bottomStart ?: default)
)

data class CornerSizes(
    val topStart: CornerSize,
    val topEnd: CornerSize,
    val bottomEnd: CornerSize,
    val bottomStart: CornerSize
)