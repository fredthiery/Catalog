package com.fthiery.catalog.ui

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.LayoutDirection.Ltr
import kotlin.math.PI
import kotlin.math.sin

class ParallelogramShape(
    private val angle: Float,
    private val topStart: CornerSize,
    private val topEnd: CornerSize,
    private val bottomEnd: CornerSize,
    private val bottomStart: CornerSize
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {

        val direction = if (layoutDirection == Ltr) 1 else -1
        val sin = sin(angle * PI / 180 * direction).toFloat()

        fun offsetY(x: Float, y: Float): Float {
            val offset = (x - size.center.x) * sin
            return y + offset
        }

        fun CornerSize.toPx() = toPx(size, density)

        fun Path.offsetMoveTo(x: Float, y: Float) = moveTo(x, offsetY(x, y))

        fun Path.offsetLineTo(x: Float, y: Float) = lineTo(x, offsetY(x, y))

        fun Path.offsetQuadraticBezierTo(x1: Float, y1: Float, x2: Float, y2: Float) =
            quadraticBezierTo(x1, offsetY(x1, y1), x2, offsetY(x2, y2))

        val left = 0f
        val top = 0f
        val right = size.width
        val bottom = size.height

        val topLeft = if (layoutDirection == Ltr) topStart.toPx() else topEnd.toPx()
        val topRight = if (layoutDirection == Ltr) topEnd.toPx() else topStart.toPx()
        val bottomLeft = if (layoutDirection == Ltr) bottomStart.toPx() else bottomEnd.toPx()
        val bottomRight = if (layoutDirection == Ltr) bottomEnd.toPx() else bottomStart.toPx()

        val path = Path().apply {
            reset()

            offsetMoveTo(topLeft, top)

            if (topLeft != 0f)
                offsetQuadraticBezierTo(left, top, left, topLeft)

            offsetLineTo(left, bottom - bottomLeft)

            if (bottomLeft != 0f)
                offsetQuadraticBezierTo(left, bottom, bottomLeft, bottom)

            offsetLineTo(right - bottomRight, bottom)

            if (bottomRight != 0f)
                offsetQuadraticBezierTo(
                    right,
                    bottom,
                    right,
                    bottom - bottomRight
                )

            offsetLineTo(right, topRight)

            if (topRight != 0f)
                offsetQuadraticBezierTo(right, top, right - topRight, 0f)

            close()
        }
        return Outline.Generic(path)
    }
}

fun ParallelogramShape(angle: Float, corners: List<CornerSize>) =
    ParallelogramShape(angle, corners[0], corners[1], corners[2], corners[3])

fun ParallelogramShape(angle: Float, corner: CornerSize) =
    ParallelogramShape(angle, corner, corner, corner, corner)

fun ParallelogramShape(angle: Float, topStart: Dp, topEnd: Dp, bottomEnd: Dp, bottomStart: Dp) =
    ParallelogramShape(
        angle,
        CornerSize(topStart),
        CornerSize(topEnd),
        CornerSize(bottomEnd),
        CornerSize(bottomStart)
    )

fun ParallelogramShape(angle: Float, size: Dp) =
    ParallelogramShape(angle, CornerSize(size))

fun ParallelogramShape(angle: Float, size: Float) =
    ParallelogramShape(angle, CornerSize(size))

fun ParallelogramShape(angle: Float, percent: Int) =
    ParallelogramShape(angle, CornerSize(percent))