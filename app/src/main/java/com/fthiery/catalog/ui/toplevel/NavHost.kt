package com.fthiery.catalog.ui.toplevel

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import androidx.navigation.compose.dialog
import com.fthiery.catalog.models.Item
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
//                    itemDetailViewModel.selectItem(itemId = it)
                    navController.navigate("Item/$it")
                },
                onNewItem = {
//                    itemDetailViewModel.selectItem(collectionId = it)
                    navController.navigate("NewItem/$it")
                }
            )
        }
        composable(
            "NewItem/{collectionId}",
            enterTransition = { slideInHorizontally { it } },
            popEnterTransition = { fadeIn() },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            val id = it.arguments?.getString("collectionId")?.toLong()
            ItemDetailScreen(
                item = Item(collectionId = id ?: 0),
                viewModel = itemDetailViewModel,
                navController = navController
            )
        }
        composable(
            "Item/{itemId}",
            enterTransition = { slideInHorizontally { it } },
            popEnterTransition = { fadeIn() },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            val id = it.arguments?.getString("itemId")?.toLong()
            val item by itemDetailViewModel.getItem(id).collectAsState(initial = Item(0))
            ItemDetailScreen(
                item = item,
                viewModel = itemDetailViewModel,
                navController = navController
            )
        }
        composable("ReloadItem/{itemId}") {
            val id = it.arguments?.getString("itemId")?.toLong()
            val item by itemDetailViewModel.getItem(id).collectAsState(initial = Item(0))
            ItemDetailScreen(
                item = item,
                viewModel = itemDetailViewModel,
                navController = navController
            )
        }
        dialog("EditCollection/{collectionId}") {
            val id = it.arguments?.getString("collectionId")?.toLong()
            collectionViewModel.selectCollection(id)
            CollectionEditDialog(
                viewModel = collectionViewModel,
                navController = navController,
                onComplete = { editId -> collectionId = editId }
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
        dialog("DeleteCollection/{collectionId}") {
            val id = it.arguments?.getString("collectionId")?.toLong()
            collectionViewModel.selectCollection(id)
            CollectionDeleteDialog(
                viewModel = collectionViewModel,
                navController = navController,
                onComplete = { collectionId = null }
            )
        }
        /* TODO: Should be a dialog */
        dialog(
            "DisplayPhoto?uri={uri}",
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            val uri = it.arguments?.getString("uri")?.toUri()
            PhotoScreen(
                photo = uri,
                viewModel = itemDetailViewModel,
                navController = navController
            )
        }
    }
}
