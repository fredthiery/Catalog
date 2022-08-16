package com.fthiery.catalog.ui.toplevel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.fthiery.catalog.viewmodels.ItemDetailViewModel
import com.google.accompanist.insets.ui.TopAppBar

@Composable
fun PhotoScreen(
    viewModel: ItemDetailViewModel,
    navController: NavController
) {

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AsyncImage(
            modifier = Modifier.align(Alignment.Center),
            model = viewModel.displayPhoto,
            contentDescription = viewModel.item.name,
            contentScale = ContentScale.Fit
        )
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Filled.ArrowBack, "Back")
                }
            },
            contentPadding = WindowInsets.systemBars.asPaddingValues(),
            backgroundColor = Color.Transparent,
            contentColor = Color.White,
            elevation = 0.dp
        )
    }
}