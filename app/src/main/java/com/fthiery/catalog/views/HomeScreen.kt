package com.fthiery.catalog.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.fthiery.catalog.R
import com.fthiery.catalog.models.Item
import com.fthiery.catalog.viewmodels.MainViewModel

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = stringResource(R.string.title_activity_main))
            })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {

            }
        },
        content = {
            Column() {
                var tabIndex by remember { mutableStateOf(0) }
                if (viewModel.collections.size > 1) {
                    ScrollableTabRow(selectedTabIndex = tabIndex) {
                        viewModel.collections.forEachIndexed { index, collection ->
                            Tab(
                                text = { Text(collection.name) },
                                selected = tabIndex == index,
                                onClick = { tabIndex = index }
                            )
                        }
                    }
                }
                if (viewModel.collections.isNotEmpty()) {
                    Collection(items = viewModel.getItems(viewModel.collections[tabIndex].id))
                }
            }
        }
    )
}

@Composable
fun Collection(items: List<Item>) {
    val scrollState = rememberLazyListState()
    LazyColumn(state = scrollState) {
        items(items) { item ->
            Card() {
                Column() {
                    Text(text = item.name)
                    Text(text = item.description)
                }
            }
        }
    }
}