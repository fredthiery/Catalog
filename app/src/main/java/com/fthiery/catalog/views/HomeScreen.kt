package com.fthiery.catalog.views

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fthiery.catalog.R
import com.fthiery.catalog.models.Item
import com.fthiery.catalog.viewmodels.MainViewModel
import com.fthiery.catalog.views.multifab.MultiFabItem
import com.fthiery.catalog.views.multifab.MultiFloatingActionButton
import com.google.accompanist.insets.ui.TopAppBar

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    var currentCollectionId by rememberSaveable { mutableStateOf(0) }
    var extendedFab by remember { mutableStateOf(false) }
    var addCollectionDialog by remember { mutableStateOf(false) }
    var tabIndex by rememberSaveable { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                contentPadding = WindowInsets
                    .statusBars
                    .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                    .asPaddingValues(),
                title = { Text(text = stringResource(R.string.title_activity_main)) },
                backgroundColor = MaterialTheme.colors.background
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                if (viewModel.collections.isNotEmpty()) {
                    currentCollectionId = viewModel.collections[tabIndex].id

                    ScrollableTabRow(
                        selectedTabIndex = tabIndex,
                        backgroundColor = Color.Transparent
                    ) {
                        viewModel.collections.forEachIndexed { index, collection ->
                            Tab(
                                text = { Text(collection.name) },
                                selected = tabIndex == index,
                                onClick = { tabIndex = index }
                            )
                        }
                    }
                    Collection(
                        items = viewModel.getItems(viewModel.collections[tabIndex].id),
                        onClick = { itemId ->
                            viewModel.getItem(itemId)
                            viewModel.modeEdit = true
                        })
                }
            }
        }
    )
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
                "item"       -> {
                    viewModel.currentItem = Item(collectionId = currentCollectionId)
                    viewModel.modeEdit = !viewModel.modeEdit
                }
            }
            extendedFab = false
        }
    )
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
                TextButton(
                    onClick = {
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

@Composable
fun Collection(items: List<Item>, onClick: (itemId: Int) -> Unit) {
    val scrollState = rememberLazyListState()
    LazyColumn(
        state = scrollState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = WindowInsets
            .systemBars // Adds navigation bar height to contentPadding
            .only(WindowInsetsSides.Bottom)
            .add(WindowInsets(left = 16.dp, top = 16.dp, bottom = 16.dp, right = 16.dp))
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

internal fun Modifier.coloredShadow(
    color: Color = Color.Black,
    borderRadius: Dp = 0.dp,
    blurRadius: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp,
    spread: Float = 0f,
    modifier: Modifier = Modifier,
) = this.then(
    modifier.drawBehind {
        this.drawIntoCanvas {
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            val spreadPixel = spread.dp.toPx()
            val leftPixel = (0f - spreadPixel) + offsetX.toPx()
            val topPixel = (0f - spreadPixel) + offsetY.toPx()
            val rightPixel = (this.size.width + spreadPixel)
            val bottomPixel = (this.size.height + spreadPixel)

            if (blurRadius != 0.dp) {
                /*
                    The feature maskFilter used below to apply the blur effect only works
                    with hardware acceleration disabled.
                 */
                frameworkPaint.maskFilter =
                    (BlurMaskFilter(blurRadius.toPx(), BlurMaskFilter.Blur.NORMAL))
            }

            frameworkPaint.color = color.toArgb()
            it.drawRoundRect(
                left = leftPixel,
                top = topPixel,
                right = rightPixel,
                bottom = bottomPixel,
                radiusX = borderRadius.toPx(),
                radiusY = borderRadius.toPx(),
                paint
            )
        }
    }
)