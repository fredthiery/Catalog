package com.fthiery.catalog.ui.baselevel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fthiery.catalog.R
import com.fthiery.catalog.viewmodels.MainViewModel

@Composable
fun SearchAppBar(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable RowScope.() -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    var searching by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        val padding by animateFloatAsState(if (searching) 0f else 1f)
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = (padding * 8).dp, horizontal = (padding * 16).dp),
            shape = RoundedCornerShape((padding * 50).toInt()),
            elevation = 2.dp
        ) {
            Crossfade(searching) {
                if (searching) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                            AutoFocusingBasicText(
                                value = viewModel.searchPattern,
                                onValueChange = { viewModel.searchPattern = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 60.dp),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.body1
                            )
                    }
                } else {
                    TextButton(
                        onClick = { searching = true },
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center)
                    ) {
                        Text(stringResource(R.string.search_in_this_collection))
                    }
                }
            }

            Row(
                Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Crossfade(searching) {
                    if (searching) {
                        IconButton(onClick = {
                            viewModel.searchPattern = ""
                            searching = false
                        }) {
                            Icon(Icons.Filled.ArrowBack, stringResource(R.string.back))
                        }
                    } else navigationIcon()
                }
            }

            Row(
                Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Crossfade(searching) {
                    if (searching) {
                        AnimatedVisibility(viewModel.searchPattern.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchPattern = "" }) {
                                Icon(Icons.Filled.Close, stringResource(R.string.reset_search_pattern))
                            }
                        }
                    } else actions()
                }
            }
        }
    }
}