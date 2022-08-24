package com.fthiery.catalog.ui.drawer

import android.util.LayoutDirection.RTL
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.text.layoutDirection
import coil.compose.AsyncImage
import com.fthiery.catalog.ui.baselevel.CornerSizes
import com.fthiery.catalog.ui.baselevel.angles
import com.fthiery.catalog.ui.baselevel.cornerSizes
import com.fthiery.catalog.ui.baselevel.quadrilateralShape
import com.fthiery.catalog.ui.theme.angle
import java.util.*

@Composable
fun DrawerItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    icon: ImageVector? = null,
    backgroundImage: Any? = null,
    color: Color,
    label: String = "",
    tag: String? = null,
    angle: Float = 0f,
    corners: CornerSizes = cornerSizes(),
    button: @Composable (RowScope.() -> Unit) = {},
    onClick: () -> Unit = {}
) {
    val height by animateFloatAsState(if (selected) 128f else 64f)
    Surface(
        modifier = modifier
            .height(height.dp)
            .padding(vertical = 4.dp)
            .rotate(
                if (Locale.getDefault().layoutDirection == RTL) -angle
                else angle
            )
            .fillMaxWidth(),
        shape = quadrilateralShape(corners, angles(vertical = angle)),
        color = if (selected) color else colors.surface,
        border = null
    ) {
        /* TODO: Ajuster largeur de l'image (cf PhotoCardRow) */
        AsyncImage(
            model = backgroundImage,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.2f,
            modifier = Modifier
                .requiredHeight(192.dp)
                .rotate(-MaterialTheme.shapes.angle)
        )
        Row(
            Modifier
                .clickable { onClick() }
                .padding(start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(icon, label)
                Spacer(Modifier.width(12.dp))
            } else Spacer(Modifier.width(36.dp))

            Text(
                text = label,
                Modifier.weight(1f)
            )
            tag?.let {
                Text(text = tag)
            }
            button()
        }
    }
}

