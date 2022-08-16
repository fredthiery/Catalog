package com.fthiery.catalog.ui.toplevel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.fthiery.catalog.R
import com.fthiery.catalog.rememberForeverLazyListState
import com.fthiery.catalog.ui.baselevel.SearchAppBar
import com.fthiery.catalog.ui.baselevel.SlantedTopAppBar
import com.fthiery.catalog.ui.baselevel.TransparentScaffold
import com.fthiery.catalog.ui.drawer.DrawerContent
import com.fthiery.catalog.ui.midlevel.ItemCard
import com.fthiery.catalog.ui.theme.angle
import com.fthiery.catalog.viewmodels.MainViewModel
import com.fthiery.catalog.views.multifab.MultiFabItem
import com.fthiery.catalog.views.multifab.MultiFloatingActionButton
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    navController: NavController,
    onItemSelect: (itemId: Long) -> Unit,
    onNewItem: (collectionId: Long) -> Unit
) {
    val collections by viewModel.collections.collectAsState(mapOf())

    val scaffoldState = rememberScaffoldState()
    var extendedFab by remember { mutableStateOf(false) }
    val scrollState = rememberForeverLazyListState(key = viewModel.collection?.name)
    val scope = rememberCoroutineScope()

    TransparentScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            if (collections.isNotEmpty())
                SearchAppBar(
                    viewModel = viewModel,
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { scaffoldState.drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, "Open navigation drawer")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(Icons.Filled.MoreVert, "Menu")
                        }
                    }
                )
        },
        drawerContent = {
            if (collections.isNotEmpty())
                DrawerContent(
                    viewModel = viewModel,
                    navController = navController,
                    items = collections.values.toList(),
                    collectionId = viewModel.collection?.id,
                    onItemClick = {
                        scope.launch {
                            viewModel.selectCollection(it)
                            scaffoldState.drawerState.close()
                        }
                    }
                )
        },
        drawerGesturesEnabled = collections.isNotEmpty()
    ) {
        if (viewModel.items.isNotEmpty()) {
            LazyColumn(
                state = scrollState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = WindowInsets
                    .systemBars
                    .only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)
                    .add(WindowInsets(16.dp, 180.dp, 16.dp, 16.dp))
                    .asPaddingValues(),
            ) {
                items(items = viewModel.items) { item ->
                    ItemCard(item, onItemSelect)
                }
            }
        } else {
            // No items
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        WindowInsets
                            .systemBars
                            .only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)
                            .add(WindowInsets(16.dp, 180.dp, 16.dp, 16.dp))
                            .asPaddingValues()
                    ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(model = R.drawable.empty_box, contentDescription = "Empty collection")
                if (collections.isNotEmpty()) {
                    // No items in the collection
                    Text("This collection is empty")
                    TextButton(onClick = {
                        viewModel.collection?.let { collection -> onNewItem(collection.id) }
                    }) {
                        Text("Do you want to add an item ?")
                    }
                } else {
                    // No collection found
                    Text("No collection found !")
                    TextButton(onClick = { navController.navigate("NewCollection") }) {
                        Text("Create a collection to start")
                    }
                }
            }
        }

        SlantedTopAppBar(
            angleDegrees = MaterialTheme.shapes.angle,
            scrolled = scrollState.firstVisibleItemIndex > 0,
            backgroundImage = viewModel.collection?.photo
        ) {
            ProvideTextStyle(TextStyle(fontStyle = FontStyle.Italic)) {
                if (collections.isNotEmpty())
                    Text(viewModel.collection?.name ?: "")
                else
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.h1
                    )
            }
        }

// TODO: utiliser un Popup
        MultiFloatingActionButton(
            modifier = Modifier.systemBarsPadding(),
            fabIcon = Icons.Filled.Add,
            items = buildList {
                add(
                    MultiFabItem(
                        "collection",
                        Icons.Filled.PlaylistAdd,
                        stringResource(R.string.add_a_collection)
                    )
                )
                if (collections.isNotEmpty())
                    add(
                        MultiFabItem(
                            "item",
                            Icons.Filled.Add,
                            stringResource(R.string.add_an_item)
                        )
                    )
            },
            extended = extendedFab,
            stateChanged = { extendedFab = it }
        ) { fabItem ->
            when (fabItem.identifier) {
                "collection" -> navController.navigate("NewCollection")
                "item"       -> viewModel.collection?.let { collection -> onNewItem(collection.id) }
            }
            extendedFab = false
        }
    }
}

