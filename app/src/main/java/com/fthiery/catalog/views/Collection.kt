package com.fthiery.catalog.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fthiery.catalog.R
import com.fthiery.catalog.models.Item

@Composable
fun Collection(
    modifier: Modifier = Modifier,
    items: List<Item>,
    onClick: (itemId: Int) -> Unit
) {
    val scrollState = rememberLazyListState()
    LazyColumn(
        modifier = modifier,
        state = scrollState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = WindowInsets
            .systemBars
            .only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)
            .add(WindowInsets(16.dp, 16.dp, 16.dp, 16.dp))
            .asPaddingValues(),
    ) {
        items(items) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(item.id) }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = item.photo,
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
                        Text(text = item.description, style = MaterialTheme.typography.caption)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CollectionPreview() {
    Collection(
        items = listOf(
            Item(name = "Coucou", description = "Super description"),
            Item(name = "Wouhou", description = "Trop bien")
        ),
        onClick = {}
    )
}