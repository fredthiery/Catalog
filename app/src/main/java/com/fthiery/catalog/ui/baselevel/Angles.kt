package com.fthiery.catalog.ui.baselevel

fun angles(
    top: Float? = null,
    end: Float? = null,
    bottom: Float? = null,
    start: Float? = null,
    vertical: Float? = null,
    horizontal: Float? = null,
    default: Float = 0f
) = Angles(
    top = top ?: horizontal ?: default,
    end = end ?: vertical ?: default,
    bottom = bottom ?: horizontal ?: default,
    start = start ?: vertical ?: default
)

class Angles(
    val top: Float = 0f,
    val end: Float = 0f,
    val bottom: Float = 0f,
    val start: Float = 0f
)