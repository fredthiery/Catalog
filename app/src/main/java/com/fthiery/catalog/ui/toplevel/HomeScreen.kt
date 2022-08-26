package com.fthiery.catalog.ui.toplevel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fthiery.catalog.R
import com.fthiery.catalog.backgroundColor
import com.fthiery.catalog.models.Item
import com.fthiery.catalog.rememberForeverLazyGridState
import com.fthiery.catalog.ui.midlevel.SearchAppBar
import com.fthiery.catalog.ui.midlevel.SlantedTopAppBar
import com.fthiery.catalog.ui.midlevel.TransparentScaffold
import com.fthiery.catalog.ui.drawer.DrawerContent
import com.fthiery.catalog.ui.midlevel.ItemCard
import com.fthiery.catalog.ui.theme.angle
import com.fthiery.catalog.viewmodels.MainViewModel
import com.fthiery.catalog.ui.baselevel.multifab.MultiFabItem
import com.fthiery.catalog.ui.baselevel.multifab.MultiFloatingActionButton
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    navController: NavController,
    collectionId: Long? = null,
    onCollectionSelect: (collectionId: Long) -> Unit,
    onItemSelect: (itemId: Long) -> Unit,
    onNewItem: (collectionId: Long) -> Unit,
) {
    var searchPattern by rememberSaveable { mutableStateOf("") }

    val collections by viewModel.collections.collectAsState(mapOf())
    val collection by viewModel.getCollection(collectionId).collectAsState(null)
    val items = remember { mutableStateListOf<Item>() }

    val scaffoldState = rememberScaffoldState()
    var extendedFab by remember { mutableStateOf(false) }
    val scrollState = rememberForeverLazyGridState(key = collection?.name)
    val scope = rememberCoroutineScope()

    val angleOffset = sin(MaterialTheme.shapes.angle * PI / 180).toFloat()

    LaunchedEffect(collectionId, searchPattern) {
        viewModel.getItems(collectionId, searchPattern).collect {
            /* TODO: Comparer les deux listes et supprimer ou ajouter les items différents */
            items.clear()
            items.addAll(it)
        }
    }

    TransparentScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AnimatedVisibility(collections.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                SearchAppBar(
                    viewModel = viewModel,
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { scaffoldState.drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, "Open navigation drawer")
                        }
                    },
                    actions = {
                        /* TODO : Peut-être faire un composable dédié réutilisable */
                        var dropdownExpanded by remember { mutableStateOf(false) }
                        IconButton(onClick = { dropdownExpanded = true }) {
                            Icon(Icons.Filled.MoreVert, "Menu")
                        }
                        DropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }) {
                            collection?.let { collection ->
                                DropdownMenuItem(onClick = {
                                    navController.navigate("DeleteCollection/${collection.id}")
                                    dropdownExpanded = false
                                }) {
                                    Text("Delete this collection")
                                }
                            }
                        }

                    },
                    onSearch = { searchPattern = it }
                )
            }
        },
        drawerContent = {
            if (collections.isNotEmpty())
                DrawerContent(
                    viewModel = viewModel,
                    navController = navController,
                    items = collections.values.toList(),
                    collectionId = collection?.id,
                    onItemClick = {
                        scope.launch {
                            onCollectionSelect(it)
                            scaffoldState.drawerState.close()
                        }
                    }
                )
        },
        drawerGesturesEnabled = collections.isNotEmpty()
    ) {
        Crossfade(targetState = items.isEmpty()) { empty ->
            when (empty) {
                true  -> EmptyScreen(
                    noCollection = collections.isEmpty(),
                    onNewItem = { collection?.let { onNewItem(it.id) } },
                    onNewCollection = { navController.navigate("NewCollection") }
                )
                false -> LazyVerticalGrid(
                    columns = GridCells.Adaptive(160.dp),
                    state = scrollState,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = WindowInsets
                        .systemBars
                        .only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)
                        .add(WindowInsets(16.dp, 180.dp, 16.dp, 16.dp))
                        .asPaddingValues()
                ) {
                    /* TODO: Utiliser Modifier.animateItemPlacement */
                    items(items = items) { item ->
                        var offset by remember { mutableStateOf(0) }
                        Box(modifier = Modifier
                            .onGloballyPositioned { offset = (it.positionInParent().x * angleOffset).toInt() }
                            .offset { IntOffset(0, offset) }
                        ) {
                            ItemCard(item, onItemSelect)
                        }
                    }
                }
            }
        }

        SlantedTopAppBar(
            angleDegrees = MaterialTheme.shapes.angle,
            scrolled = scrollState.firstVisibleItemIndex > 0,
            backgroundImage = collection?.photo ?: R.drawable.stripes,
            backgroundColor = collection?.backgroundColor() ?: MaterialTheme.colors.surface
        ) {
            ProvideTextStyle(TextStyle(fontStyle = FontStyle.Italic)) {
                Text(collection?.name ?: stringResource(R.string.app_name))
            }
        }

// TODO: utiliser un Dialog
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
                "item"       -> collection?.let { onNewItem(it.id) }
            }
            extendedFab = false
        }
    }
}

@Composable
fun EmptyScreen(noCollection: Boolean, onNewItem: () -> Unit, onNewCollection: () -> Unit) {
    Surface {
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
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(painterResource(R.drawable.box), null)
            Crossfade(targetState = noCollection) {
                when (it) {
                    true  -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("No collection found !")
                            Text("Create a collection to start",
                                color = MaterialTheme.colors.primary,
                                modifier = Modifier.clickable { onNewCollection() })
                        }
                    }
                    false -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("This collection is empty")
                            Text("Do you want to add an item ?",
                                color = MaterialTheme.colors.primary,
                                modifier = Modifier.clickable { onNewItem() })
                        }
                    }
                }
            }
        }
    }
}