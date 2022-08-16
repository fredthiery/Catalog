package com.fthiery.catalog.ui.toplevel

import androidx.compose.animation.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.dialog
import com.fthiery.catalog.ui.dialogs.CollectionDeleteScreen
import com.fthiery.catalog.ui.dialogs.CollectionEditScreen
import com.fthiery.catalog.viewmodels.CollectionViewModel
import com.fthiery.catalog.viewmodels.ItemDetailViewModel
import com.fthiery.catalog.viewmodels.MainViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavHost(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    itemDetailViewModel: ItemDetailViewModel,
    collectionViewModel: CollectionViewModel
) {
    AnimatedNavHost(navController = navController, startDestination = "Home") {
        composable(
            "Home",
            enterTransition = { slideInHorizontally { -it } },
            exitTransition = { slideOutHorizontally { -it } }
        ) {
            HomeScreen(
                viewModel = mainViewModel,
                navController = navController,
                onItemSelect = {
                    itemDetailViewModel.selectItem(itemId = it)
                    navController.navigate("Item")
                },
                onNewItem = {
                    itemDetailViewModel.selectItem(collectionId = it)
                    navController.navigate("Item")
                }
            )
        }
        composable(
            "Item",
            enterTransition = { slideInHorizontally { it } },
            popEnterTransition = { fadeIn() },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            ItemDetailScreen(
                viewModel = itemDetailViewModel,
                navController = navController
            )
        }
        dialog("EditCollection/{collectionId}") { entry ->
            val collectionId = entry.arguments?.getString("collectionId")?.toLong()
            collectionViewModel.selectCollection(collectionId)
            CollectionEditScreen(
                viewModel = collectionViewModel,
                navController = navController,
                onComplete = { id -> mainViewModel.selectCollection(id) },
                collection = collectionViewModel.editCollection
            )
        }
        dialog("NewCollection") {
            collectionViewModel.selectCollection()
            CollectionEditScreen(
                viewModel = collectionViewModel,
                navController = navController,
                onComplete = { id -> mainViewModel.selectCollection(id) },
                collection = collectionViewModel.editCollection
            )
        }
        dialog("DeleteCollection/{collectionId}") { entry ->
            val collectionId = entry.arguments?.getString("collectionId")?.toLong()
            collectionViewModel.selectCollection(collectionId)
            CollectionDeleteScreen(
                viewModel = collectionViewModel,
                navController = navController,
                onComplete = { id -> mainViewModel.selectCollection(id) }
            )
        }
        composable(
            "DisplayPhoto",
            enterTransition = { scaleIn() },
            exitTransition = { scaleOut() }
        ) {
            PhotoScreen(
                viewModel = itemDetailViewModel,
                navController = navController
            )
        }
    }
}