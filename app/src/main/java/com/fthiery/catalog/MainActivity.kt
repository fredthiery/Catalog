package com.fthiery.catalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.fthiery.catalog.ui.theme.CatalogTheme
import com.fthiery.catalog.ui.toplevel.NavHost
import com.fthiery.catalog.viewmodels.CollectionViewModel
import com.fthiery.catalog.viewmodels.ItemDetailViewModel
import com.fthiery.catalog.viewmodels.MainViewModel
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint

@OptIn(ExperimentalAnimationApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val itemDetailViewModel: ItemDetailViewModel by viewModels()
    private val collectionViewModel: CollectionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val navController = rememberAnimatedNavController()

            CatalogTheme {
                Box(Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        mainViewModel = viewModel,
                        itemDetailViewModel = itemDetailViewModel,
                        collectionViewModel = collectionViewModel
                    )
                }
            }
        }
    }
}