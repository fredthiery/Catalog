package com.fthiery.catalog.ui.baselevel

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.fthiery.catalog.degreesOffset
import kotlin.math.sign

enum class Inset {
    Inside, Center, Outside
}

fun quadrilateralShape(corners: CornerSizes, angles: Angles, inset: Inset = Inset.Center) =
    QuadrilateralShape(
        topStart = corners.topStart,
        topEnd = corners.topEnd,
        bottomEnd = corners.bottomEnd,
        bottomStart = corners.bottomStart,
        startDegrees = angles.start,
        topDegrees = angles.top,
        endDegrees = angles.end,
        bottomDegrees = angles.bottom,
        inset = inset
    )


class QuadrilateralShape(
    private val topStart: CornerSize,
    private val topEnd: CornerSize,
    private val bottomEnd: CornerSize,
    private val bottomStart: CornerSize,
    private val startDegrees: Float = 0f,
    private val topDegrees: Float = 0f,
    private val endDegrees: Float = 0f,
    private val bottomDegrees: Float = 0f,
    private val inset: Inset = Inset.Center
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {

        val ltr = layoutDirection == LayoutDirection.Ltr

        fun offsetX(x: Float, y: Float, angleOffset: Float): Float {
            val insetOffset: Float = when (inset) {
                Inset.Inside  -> size.center.y * angleOffset * sign(size.center.x - x)
                Inset.Center  -> 0f
                Inset.Outside -> -size.center.y * angleOffset * sign(size.center.x - x)
            }
            return x + (y - size.center.y) * angleOffset - insetOffset
        }

        fun offsetY(x: Float, y: Float, angleOffset: Float): Float {
            val insetOffset: Float = when (inset) {
                Inset.Inside  -> size.center.x * angleOffset * sign(size.center.y - y)
                Inset.Center  -> 0f
                Inset.Outside -> -size.center.x * angleOffset * sign(size.center.y - y)
            }
            return y + (x - size.center.x) * angleOffset - insetOffset
        }

        fun CornerSize.toPx() = toPx(size, density)
        fun Path.offsetMoveTo(x: Float, y: Float, verticalOffset: Float, horizontalOffset: Float) =
            moveTo(offsetX(x, y, verticalOffset), offsetY(x, y, horizontalOffset))

        fun Path.offsetLineTo(x: Float, y: Float, verticalOffset: Float, horizontalOffset: Float) =
            lineTo(offsetX(x, y, verticalOffset), offsetY(x, y, horizontalOffset))

        fun Path.offsetQuadraticBezierTo(
            x1: Float,
            y1: Float,
            x2: Float,
            y2: Float,
            verticalOffset: Float,
            horizontalOffset: Float
        ) = quadraticBezierTo(
            offsetX(x1, y1, verticalOffset),
            offsetY(x1, y1, horizontalOffset),
            offsetX(x2, y2, verticalOffset),
            offsetY(x2, y2, horizontalOffset)
        )

        val left = 0f
        val top = 0f
        val right = size.width
        val bottom = size.height

        val topLeft = if (ltr) topStart.toPx() else topEnd.toPx()
        val topRight = if (ltr) topEnd.toPx() else topStart.toPx()
        val bottomLeft = if (ltr) bottomStart.toPx() else bottomEnd.toPx()
        val bottomRight = if (ltr) bottomEnd.toPx() else bottomStart.toPx()

        val leftOffset = (if (ltr) startDegrees else endDegrees).degreesOffset()
        val rightOffset = (if (ltr) endDegrees else startDegrees).degreesOffset()
        val topOffset = topDegrees.degreesOffset()
        val bottomOffset = bottomDegrees.degreesOffset()

        return Outline.Generic(Path().apply {
            reset()

            offsetMoveTo(topLeft, top, leftOffset, topOffset)

            if (topLeft != 0f)
                offsetQuadraticBezierTo(left, top, left, topLeft, leftOffset, topOffset)

            offsetLineTo(left, bottom - bottomLeft, leftOffset, bottomOffset)

            if (bottomLeft != 0f)
                offsetQuadraticBezierTo(left, bottom, bottomLeft, bottom, leftOffset, bottomOffset)

            offsetLineTo(right - bottomRight, bottom, rightOffset, bottomOffset)

            if (bottomRight != 0f)
                offsetQuadraticBezierTo(
                    right,
                    bottom,
                    right,
                    bottom - bottomRight,
                    rightOffset,
                    bottomOffset
                )

            offsetLineTo(right, topRight, rightOffset, topOffset)

            if (topRight != 0f)
                offsetQuadraticBezierTo(right, top, right - topRight, 0f, rightOffset, topOffset)

            close()
        })
    }
}