package com.fthiery.catalog.views

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.fthiery.catalog.models.Item
import com.fthiery.catalog.viewmodels.MainViewModel
import com.google.accompanist.insets.ui.TopAppBar

@Composable
fun ItemDetailScreen(
    viewModel: MainViewModel,
    navController: NavController,
    itemId: Int?
) {
    val item by viewModel.getItem(itemId).collectAsState(Item())

    BackHandler { navController.navigateUp() }

    Scaffold(
        topBar = {
            TopAppBar(
                contentPadding = WindowInsets
                    .systemBars
                    .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                    .asPaddingValues(),
                title = { Text(text = item.name) },
                backgroundColor = MaterialTheme.colors.background,
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("EditItem/${item.id}") }) {
                        Icon(Icons.Filled.Edit, "Edit")
                    }
                }
            )
        }
    ) {
        Column(modifier = Modifier.padding(it)) {

        }
    }
}