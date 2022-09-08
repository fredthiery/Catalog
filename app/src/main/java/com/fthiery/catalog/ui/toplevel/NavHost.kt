package com.fthiery.catalog.ui.toplevel

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.dialog
import com.fthiery.catalog.ui.dialogs.CollectionDeleteDialog
import com.fthiery.catalog.ui.dialogs.CollectionEditDialog
import com.fthiery.catalog.viewmodels.CollectionViewModel
import com.fthiery.catalog.viewmodels.ItemDetailViewModel
import com.fthiery.catalog.viewmodels.MainViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun NavHost(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    itemDetailViewModel: ItemDetailViewModel,
    collectionViewModel: CollectionViewModel
) {
    var collectionId by rememberSaveable { mutableStateOf<Long?>(null) }

    LaunchedEffect(collectionId) {
        if (collectionId == null) collectionId = mainViewModel.firstCollection()?.id
    }

    AnimatedNavHost(navController = navController, startDestination = "Home") {
        composable(
            "Home",
            enterTransition = { slideInHorizontally { -it } },
            exitTransition = { slideOutHorizontally { -it } }
        ) {

            HomeScreen(
                viewModel = mainViewModel,
                navController = navController,
                collectionId = collectionId,
                onCollectionSelect = { collectionId = it },
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
            val id = entry.arguments?.getString("collectionId")?.toLong()
            collectionViewModel.selectCollection(id)
            CollectionEditDialog(
                viewModel = collectionViewModel,
                navController = navController,
                onComplete = { collectionId = it }
            )
        }
        dialog("NewCollection") {
            collectionViewModel.selectCollection()
            CollectionEditDialog(
                viewModel = collectionViewModel,
                navController = navController,
                onComplete = { collectionId = it }
            )
        }
        dialog("DeleteCollection/{collectionId}") { entry ->
            val id = entry.arguments?.getString("collectionId")?.toLong()
            collectionViewModel.selectCollection(id)
            CollectionDeleteDialog(
                viewModel = collectionViewModel,
                navController = navController,
                onComplete = { collectionId = null }
            )
        }
        /* TODO: Should be a dialog */
        dialog(
            "DisplayPhoto",
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            PhotoScreen(
                viewModel = itemDetailViewModel,
                navController = navController
            )
        }
    }
}