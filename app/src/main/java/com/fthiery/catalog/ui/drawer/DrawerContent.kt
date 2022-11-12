package com.fthiery.catalog.ui.drawer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fthiery.catalog.R
import com.fthiery.catalog.models.ItemCollection
import com.fthiery.catalog.ui.baselevel.cornerSizes
import com.fthiery.catalog.viewmodels.MainViewModel

@Composable
fun DrawerContent(
    viewModel: MainViewModel,
    navController: NavController,
    angleDegrees: Float = 0f,
    items: List<ItemCollection>,
    collectionId: Long?,
    onItemClick: (collectionId: Long) -> Unit
) {
    ProvideTextStyle(TextStyle(fontStyle = FontStyle.Italic)) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .safeContentPadding()
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.h2,
                fontStyle = FontStyle.Italic,
                modifier = Modifier
                    .rotate(angleDegrees)
                    .padding(vertical = 20.dp)
            )
            items.forEachIndexed { index, collection ->
                val size by viewModel.collectionSize(collection.id).collectAsState(0L)
                DrawerItem(
                    selected = collection.id == collectionId,
                    backgroundImage = collection.photo ?: R.drawable.stripes,
                    itemColor = collection.backgroundColor(),
                    label = collection.name,
                    tag = size.toString(),
                    angleDegrees = angleDegrees,
                    corners = cornerSizes(
                        topStart = if (index == 0) 24.dp else 4.dp,
                        bottomEnd = if (index == items.size - 1) 24.dp else 4.dp,
                        default = 4.dp
                    ),
                    onClick = { onItemClick(collection.id) },
                )
            }
        }
    }
}