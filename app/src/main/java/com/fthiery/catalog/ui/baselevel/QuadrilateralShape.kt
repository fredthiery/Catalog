package com.fthiery.catalog.ui.baselevel

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.PI
import kotlin.math.sign
import kotlin.math.sin

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

        val direction = if (layoutDirection == LayoutDirection.Ltr) 1 else -1

        /* TODO: Précalculer les angleOffset */

        fun offsetX(x: Float, y: Float, angle: Float): Float {
            val angleOffset = sin(angle * PI / 180 * direction).toFloat()
            val insetOffset: Float = when (inset) {
                Inset.Inside  -> size.center.y * angleOffset * sign(size.center.x - x)
                Inset.Center  -> 0f
                Inset.Outside -> -size.center.y * angleOffset * sign(size.center.x - x)
            }
            return x + (y - size.center.y) * angleOffset - insetOffset
        }

        fun offsetY(x: Float, y: Float, angle: Float): Float {
            val angleOffset = sin(angle * PI / 180 * direction).toFloat()
            val insetOffset: Float = when (inset) {
                Inset.Inside  -> size.center.x * angleOffset * sign(size.center.y - y)
                Inset.Center  -> 0f
                Inset.Outside -> -size.center.x * angleOffset * sign(size.center.y - y)
            }
            return y + (x - size.center.x) * angleOffset - insetOffset
        }

        fun CornerSize.toPx() = toPx(size, density)
        fun Path.offsetMoveTo(x: Float, y: Float, verticalAngle: Float, horizontalAngle: Float) =
            moveTo(offsetX(x, y, verticalAngle), offsetY(x, y, horizontalAngle))

        fun Path.offsetLineTo(x: Float, y: Float, verticalAngle: Float, horizontalAngle: Float) =
            lineTo(offsetX(x, y, verticalAngle), offsetY(x, y, horizontalAngle))

        fun Path.offsetQuadraticBezierTo(
            x1: Float,
            y1: Float,
            x2: Float,
            y2: Float,
            verticalAngle: Float,
            horizontalAngle: Float
        ) = quadraticBezierTo(
            offsetX(x1, y1, verticalAngle),
            offsetY(x1, y1, horizontalAngle),
            offsetX(x2, y2, verticalAngle),
            offsetY(x2, y2, horizontalAngle)
        )

        val left = 0f
        val top = 0f
        val right = size.width
        val bottom = size.height

        val topLeft = if (layoutDirection == LayoutDirection.Ltr) topStart.toPx() else topEnd.toPx()
        val topRight =
            if (layoutDirection == LayoutDirection.Ltr) topEnd.toPx() else topStart.toPx()
        val bottomLeft =
            if (layoutDirection == LayoutDirection.Ltr) bottomStart.toPx() else bottomEnd.toPx()
        val bottomRight =
            if (layoutDirection == LayoutDirection.Ltr) bottomEnd.toPx() else bottomStart.toPx()

        val leftDegrees = if (layoutDirection == LayoutDirection.Ltr) startDegrees else endDegrees
        val rightDegrees = if (layoutDirection == LayoutDirection.Ltr) endDegrees else startDegrees

        return Outline.Generic(Path().apply {
            reset()
            offsetMoveTo(topLeft, top, leftDegrees, topDegrees)
            if (topLeft != 0f)
                offsetQuadraticBezierTo(left, top, left, topLeft, leftDegrees, topDegrees)
            offsetLineTo(left, bottom - bottomLeft, leftDegrees, bottomDegrees)
            if (bottomLeft != 0f)
                offsetQuadraticBezierTo(
                    left,
                    bottom,
                    bottomLeft,
                    bottom,
                    leftDegrees,
                    bottomDegrees
                )
            offsetLineTo(right - bottomRight, bottom, rightDegrees, bottomDegrees)
            if (bottomRight != 0f)
                offsetQuadraticBezierTo(
                    right,
                    bottom,
                    right,
                    bottom - bottomRight,
                    rightDegrees,
                    bottomDegrees
                )
            offsetLineTo(right, topRight, rightDegrees, topDegrees)
            if (topRight != 0f)
                offsetQuadraticBezierTo(right, top, right - topRight, 0f, rightDegrees, topDegrees)
            close()
        })
    }
}