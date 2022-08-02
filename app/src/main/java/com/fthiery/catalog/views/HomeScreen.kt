package com.fthiery.catalog.views

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fthiery.catalog.R
import com.fthiery.catalog.models.Item
import com.fthiery.catalog.ui.cornerSizes
import com.fthiery.catalog.viewmodels.MainViewModel
import com.fthiery.catalog.views.multifab.MultiFabItem
import com.fthiery.catalog.views.multifab.MultiFloatingActionButton
import com.google.accompanist.insets.ui.TopAppBar
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    navController: NavController,
    collectionId: Int?,
    onCollectionSelect: (collectionId: Int) -> Unit
) {
    val collections by viewModel.collections.collectAsState(mapOf())

    var extendedFab by remember { mutableStateOf(false) }
    var addCollectionDialog by remember { mutableStateOf(false) }
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                contentPadding = WindowInsets
                    .systemBars
                    .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                    .asPaddingValues(),
                title = { Text(text = stringResource(R.string.title_activity_main)) },
                backgroundColor = MaterialTheme.colors.background,
                navigationIcon = {
                    IconButton(onClick = { scope.launch { scaffoldState.drawerState.open() } }) {
                        Icon(Icons.Filled.Menu, "Open navigation drawer")
                    }
                }
            )
        },
        drawerContent = {
            LazyColumn(
                contentPadding = WindowInsets.systemBars.asPaddingValues(),
                modifier = Modifier.padding(16.dp)
            ) {
                val items = collections.values.toList()
                itemsIndexed(items) { index, collection ->
                    val size by viewModel.collectionSize(collection.id).collectAsState(0)
                    DrawerItem(
                        selected = collection.id == collectionId,
                        label = collection.name,
                        tag = size.toString(),
                        angle = -5f,
                        corners = cornerSizes(
                            topStart = if (index == 0) 24.dp else 4.dp,
                            bottomEnd = if (index == items.size - 1) 24.dp else 4.dp,
                            others = 4.dp
                        ),
                        button = {
                            IconButton(onClick = { /*TODO: Ajouter un CollectionEditScreen*/ }) {
                                Icon(Icons.Filled.Menu, "Edit ${collection.name}")
                            }
                        },
                        onClick = {
                            scope.launch {
                                onCollectionSelect(collection.id)
                                scaffoldState.drawerState.close()
                            }
                        },
                    )
                }
            }
        },
        drawerShape = RectangleShape
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (collections.isNotEmpty()) {
                collectionId?.let {
                    val items by viewModel.getItems(collectionId).collectAsState(listOf(Item()))
                    Collection(
                        items = items,
                        onClick = { navController.navigate("Detail/${it}") })
                }
            }
        }
    }

// TODO: utiliser un Popup
    MultiFloatingActionButton(
        modifier = Modifier.systemBarsPadding(),
        fabIcon = Icons.Filled.Add,
        items = listOf(
            MultiFabItem(
                "collection",
                Icons.Filled.PlaylistAdd,
                stringResource(R.string.add_a_collection)
            ),
            MultiFabItem(
                "item",
                Icons.Filled.Add,
                stringResource(R.string.add_an_item)
            )
        ),
        extended = extendedFab,
        stateChanged = { extendedFab = it },
        onFabItemClicked = { fabItem ->
            when (fabItem.identifier) {
                "collection" -> addCollectionDialog = true
                "item"       -> navController.navigate("NewItem/${collectionId}")
            }
            extendedFab = false
        }
    )

// TODO: à placer dans une fonction à part
    if (addCollectionDialog) {
        var name by rememberSaveable { mutableStateOf("") }
        AlertDialog(
            shape = MaterialTheme.shapes.large,
            text = {
                Column {
                    Text(
                        text = stringResource(id = R.string.add_a_collection),
                        style = MaterialTheme.typography.subtitle1
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Collection name") }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.newCollection(name)
                    addCollectionDialog = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { addCollectionDialog = false }) {
                    Text("Cancel")
                }
            },
            onDismissRequest = { addCollectionDialog = false }
        )
    }
}