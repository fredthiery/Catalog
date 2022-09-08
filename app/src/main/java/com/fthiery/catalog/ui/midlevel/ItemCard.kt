package com.fthiery.catalog.ui.midlevel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fthiery.catalog.R
import com.fthiery.catalog.absDegOffset
import com.fthiery.catalog.models.Item
import com.fthiery.catalog.ui.baselevel.angles
import com.fthiery.catalog.ui.baselevel.cornerSizes
import com.fthiery.catalog.ui.baselevel.quadrilateralShape

@Composable
fun ItemCard(
    item: Item,
    angleDegrees: Float = 0f,
    onItemSelect: (itemId: Long) -> Unit
) {
    val offset = angleDegrees.absDegOffset()
    Card(
        shape = quadrilateralShape(
            corners = cornerSizes(default = 4.dp),
            angles = angles(horizontal = angleDegrees)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemSelect(item.id) }
    ) {
        BoxWithConstraints {
            AsyncImage(
                model = item.photos.getOrNull(0),
                fallback = painterResource(id = R.drawable.placeholder),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(.8f)
                    .requiredHeight(this.maxWidth * (offset + 1.25f))
            )
            Surface(
                color = MaterialTheme.colors.surface.copy(alpha = 0.9f),
                shape = quadrilateralShape(
                    cornerSizes(0.dp),
                    angles(horizontal = angleDegrees)
                ),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .offset(y = 1.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.subtitle1.copy(fontStyle = FontStyle.Italic),
                    modifier = Modifier
                        .padding(8.dp)
                        .rotate(angleDegrees)
                )
            }
        }
    }
}