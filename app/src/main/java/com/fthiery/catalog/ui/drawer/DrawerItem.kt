package com.fthiery.catalog.ui.drawer

import android.util.LayoutDirection.RTL
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
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
import com.fthiery.catalog.contentColor
import com.fthiery.catalog.ui.baselevel.CornerSizes
import com.fthiery.catalog.ui.baselevel.angles
import com.fthiery.catalog.ui.baselevel.cornerSizes
import com.fthiery.catalog.ui.baselevel.quadrilateralShape
import java.util.*

@Composable
fun DrawerItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    icon: ImageVector? = null,
    backgroundImage: Any? = null,
    itemColor: Color,
    label: String = "",
    tag: String? = null,
    angleDegrees: Float = 0f,
    corners: CornerSizes = cornerSizes(),
    onClick: () -> Unit = {}
) {
    val height = if (selected) 128f else 64f
    val backgroundColor = if (selected) itemColor else colors.surface
    Surface(
        modifier = modifier
            .height(height.dp)
            .padding(vertical = 4.dp)
            .rotate(
                if (Locale.getDefault().layoutDirection == RTL) -angleDegrees
                else angleDegrees
            )
            .fillMaxWidth(),
        shape = quadrilateralShape(corners, angles(vertical = angleDegrees)),
        color = backgroundColor,
        contentColor = backgroundColor.contentColor(),
        border = null
    ) {
        AsyncImage(
            model = backgroundImage,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.2f,
            modifier = Modifier
                .requiredHeight(192.dp)
                .rotate(-angleDegrees)
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
        }
    }
}

