package com.fthiery.catalog.views

import android.util.LayoutDirection.RTL
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.text.layoutDirection
import com.fthiery.catalog.ui.ParallelogramShape
import java.util.*

@Composable
fun DrawerItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    icon: ImageVector? = null,
    label: String = "",
    tag: String? = null,
    angle: Float = 0f,
    corners: List<CornerSize> = listOf(),
    button: @Composable (RowScope.() -> Unit) = {},
    onClick: () -> Unit = {}
) {

    Surface(
        modifier = modifier
            .height(64.dp)
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        shape = ParallelogramShape(angle, corners),
        color = if (selected) colors.primary else colors.secondary,
    ) {
        Row(
            Modifier
                .rotate(
                    if (Locale.getDefault().layoutDirection == RTL) -angle
                    else angle
                )
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
                Modifier.weight(1f),
                style = TextStyle(fontStyle = FontStyle.Italic)
            )
            tag?.let {
                Text(
                    text = tag,
                    style = TextStyle(fontStyle = FontStyle.Italic)
                )
            }
            button()
        }
    }
}

