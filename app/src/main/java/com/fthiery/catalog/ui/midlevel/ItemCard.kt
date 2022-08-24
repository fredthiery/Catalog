package com.fthiery.catalog.ui.midlevel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fthiery.catalog.R
import com.fthiery.catalog.models.Item
import com.fthiery.catalog.ui.baselevel.angles
import com.fthiery.catalog.ui.baselevel.cornerSizes
import com.fthiery.catalog.ui.baselevel.quadrilateralShape
import com.fthiery.catalog.ui.theme.angle

@Composable
fun ItemCard(
    item: Item,
    onItemSelect: (itemId: Long) -> Unit
) {
    Card(
        shape = quadrilateralShape(
            corners = cornerSizes(default = 4.dp),
            angles = angles(horizontal = MaterialTheme.shapes.angle)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemSelect(item.id)
            }
    ) {
        Box() {
            Surface(modifier = Modifier
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    /* TODO Calculer l'offset correctement */
                    layout(placeable.width, placeable.height - 60) {
                        placeable.placeRelative(0, -30)
                    }
                }) {
                AsyncImage(
                    model = item.photos.getOrNull(0),
                    fallback = painterResource(id = R.drawable.placeholder),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.aspectRatio(0.8f)
                )
            }
            Surface(
                color = MaterialTheme.colors.surface.copy(alpha = 0.9f),
                shape = quadrilateralShape(
                    cornerSizes(0.dp),
                    angles(horizontal = MaterialTheme.shapes.angle)
                ),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.subtitle1.copy(fontStyle = FontStyle.Italic),
                    modifier = Modifier
                        .padding(8.dp)
                        .rotate(MaterialTheme.shapes.angle)
                )
            }
        }
    }
}