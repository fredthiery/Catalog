package com.fthiery.catalog.views.multifab

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.fthiery.catalog.ui.theme.MultiFabBackground
import com.google.accompanist.insets.VerticalSide

@Composable
fun MultiFloatingActionButton(
    fabIcon: ImageVector,
    modifier: Modifier = Modifier,
    items: List<MultiFabItem>,
    extended: Boolean,
    stateChanged: (extended: Boolean) -> Unit,
    onFabItemClicked: (item: MultiFabItem) -> Unit
) {
    val rotation by animateFloatAsState(targetValue = if (extended) 45f else 0f)

    AnimatedVisibility(
        visible = extended,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .alpha(0.7f)
            .background(MaterialTheme.colors.background)
            .clickable(
                interactionSource = MutableInteractionSource(),
                enabled = extended,
                indication = null
            ) { stateChanged(false) }
        )
    }

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(16.dp,Alignment.Bottom),
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        items.forEachIndexed { index, item ->
            AnimatedVisibility(
                visible = extended,
                enter = slideInVertically { it * (items.size - index) } + fadeIn(),
                exit = slideOutVertically { it * (items.size - index) } + fadeOut()
            ) {
                MiniFabItem(item, onFabItemClicked)
            }
        }
        FloatingActionButton(onClick = {
            stateChanged(!extended)
        }) {
            Icon(
                tint = MaterialTheme.colors.onSecondary,
                imageVector = fabIcon,
                contentDescription = if (extended) "Close" else "Add item",
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}

@Composable
private fun MiniFabItem(
    item: MultiFabItem,
    onFabItemClicked: (item: MultiFabItem) -> Unit
) {
    ExtendedFloatingActionButton(
        contentColor = MaterialTheme.colors.onSecondary,
        text = { Text(item.label) },
        icon = { Icon(item.icon, null) },
        onClick = { onFabItemClicked(item) }
    )
}
