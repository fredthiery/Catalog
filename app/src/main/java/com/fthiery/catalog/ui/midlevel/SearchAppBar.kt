package com.fthiery.catalog.ui.midlevel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.textButtonColors
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fthiery.catalog.R
import com.fthiery.catalog.contentColor
import com.fthiery.catalog.ui.baselevel.AutoFocusingBasicText
import com.fthiery.catalog.viewmodels.MainViewModel

@Composable
fun SearchAppBar(
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colors.onSurface,
    navigationIcon: @Composable RowScope.() -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    onSearch: (pattern: String) -> Unit
) {
    var searching by rememberSaveable { mutableStateOf(false) }
    var pattern by rememberSaveable { mutableStateOf("") }
    fun setPattern(value: String = "") {
        pattern = value
        onSearch(value)
    }

    Box(
        modifier = modifier.fillMaxWidth().height(56.dp)
    ) {
        val padding by animateFloatAsState(if (searching) 0f else 1f)
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = (padding * 8).dp, horizontal = (padding * 16).dp),
            shape = RoundedCornerShape((padding * 50).toInt()),
            color = contentColor.contentColor().copy(alpha = 0.5f),
            contentColor = contentColor,
            elevation = 0.dp
        ) {
            Crossfade(searching) {
                if (searching) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        AutoFocusingBasicText(
                            value = pattern,
                            onValueChange = ::setPattern,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 60.dp),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onSurface),
                            cursorBrush = SolidColor(MaterialTheme.colors.onSurface)
                        )
                    }
                } else {
                    TextButton(
                        onClick = { searching = true },
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center),
                        colors = textButtonColors(contentColor = contentColor)
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
                            setPattern()
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
                        AnimatedVisibility(pattern.isNotEmpty()) {
                            IconButton(onClick = ::setPattern) {
                                Icon(
                                    Icons.Filled.Close,
                                    stringResource(R.string.reset_search_pattern)
                                )
                            }
                        }
                    } else actions()
                }
            }
        }
    }
}