package com.fthiery.catalog.ui.midlevel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fthiery.catalog.R
import com.fthiery.catalog.models.Item

@Composable
fun ItemCard(
    item: Item,
    onItemSelect: (itemId: Long) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemSelect(item.id)
            }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.photos.getOrNull(0),
                fallback = painterResource(id = R.drawable.placeholder),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(96.dp)
                    .height(96.dp)
            )
            Column(
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(text = item.name, style = MaterialTheme.typography.subtitle1)
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.caption
                )
            }
        }
    }
}