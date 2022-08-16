package com.fthiery.catalog.ui.drawer

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import com.fthiery.catalog.ui.theme.angle
import com.fthiery.catalog.viewmodels.MainViewModel

@Composable
fun DrawerContent(
    viewModel: MainViewModel,
    navController: NavController,
    items: List<ItemCollection>,
    collectionId: Long?,
    onItemClick: (collectionId: Long) -> Unit
) {
    ProvideTextStyle(TextStyle(fontStyle = FontStyle.Italic)) {
        LazyColumn(
            contentPadding = WindowInsets.systemBars.asPaddingValues(),
            modifier = Modifier.padding(16.dp)
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.h3,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier
                        .rotate(MaterialTheme.shapes.angle)
                        .padding(vertical = 20.dp)
                )
            }
            itemsIndexed(items) { index, collection ->
                val size by viewModel.collectionSize(collection.id).collectAsState(0L)
                DrawerItem(
                    selected = collection.id == collectionId,
                    backgroundImage = collection.photo,
                    label = collection.name,
                    tag = size.toString(),
                    angle = MaterialTheme.shapes.angle,
                    corners = cornerSizes(
                        topStart = if (index == 0) 24.dp else 4.dp,
                        bottomEnd = if (index == items.size - 1) 24.dp else 4.dp,
                        default = 4.dp
                    ),
                    button = {
                        var dropdownExpanded by remember { mutableStateOf(false) }
                        IconButton(onClick = { dropdownExpanded = true }) {
                            Icon(Icons.Filled.Menu, "Edit ${collection.name}")
                            DropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false }) {
                                DropdownMenuItem(onClick = {
                                    navController.navigate("EditCollection/${collection.id}")
                                    dropdownExpanded = false
                                }) { Text("Edit ${collection.name}") }
                                DropdownMenuItem(onClick = {
                                    navController.navigate("DeleteCollection/${collection.id}")
                                    dropdownExpanded = false
                                }) {
                                    Text("Delete ${collection.name}")
                                }
                            }
                        }
                    },
                    onClick = { onItemClick(collection.id) },
                )
            }
        }
    }
}