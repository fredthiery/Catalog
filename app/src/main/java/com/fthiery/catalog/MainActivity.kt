package com.fthiery.catalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.fthiery.catalog.ui.theme.CatalogTheme
import com.fthiery.catalog.viewmodels.MainViewModel
import com.fthiery.catalog.views.HomeScreen
import com.fthiery.catalog.views.ItemDetailScreen
import com.fthiery.catalog.views.ItemEditScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
@OptIn(ExperimentalAnimationApi::class)
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {

            val navController = rememberAnimatedNavController()

            var selectedCollectionId by rememberSaveable { mutableStateOf<Int?>(null) }

            // Watches ItemCollections from MainViewModel and selects first collection if none selected
            val collections by mainViewModel.collections
                .onEach {
                    if (selectedCollectionId == null) selectedCollectionId = it.keys.firstOrNull()
                }
                .collectAsState(mapOf())

            CatalogTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize()) {
                    AnimatedNavHost(navController = navController, startDestination = "Home") {
                        composable(
                            "Home",
                            enterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
                            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) }
                        ) {
                            HomeScreen(
                                viewModel = mainViewModel,
                                navController = navController,
                                collectionId = selectedCollectionId,
                                onCollectionSelect = { selectedCollectionId = it }
                            )
                        }
                        composable(
                            "Detail/{itemId}",
                            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
                            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
                            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
                            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
                        ) {
                            val itemId = it.arguments?.getString("itemId")?.toInt()
                            ItemDetailScreen(
                                viewModel = mainViewModel,
                                navController = navController,
                                itemId = itemId
                            )
                        }
                        composable(
                            "EditItem/{itemId}",
                            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
                            exitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
                        ) {
                            val itemId = it.arguments?.getString("itemId")?.toInt()
                            ItemEditScreen(
                                viewModel = mainViewModel,
                                navController = navController,
                                itemId = itemId
                            )
                        }
                        composable(
                            "NewItem/{collectionId}",
                            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
                            exitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
                        ) {
                            val collectionId = it.arguments?.getString("collectionId")?.toInt()
                            ItemEditScreen(
                                viewModel = mainViewModel,
                                navController = navController,
                                collectionId = collectionId
                            )
                        }
                    }
                }
            }
        }
    }

}