package com.fthiery.catalog.ui.baselevel

import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun SlantedTopAppBar(
    angleDegrees: Float = 0f,
    modifier: Modifier = Modifier,
    scrolled: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundImage: Uri? = null,
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = AppBarDefaults.TopAppBarElevation,
    onTitleClick: () -> Unit = {},
    titleContent: @Composable () -> Unit,
) {
    /* TODO : gérer contentPadding et calculer la taille correcte */
    val angle by animateFloatAsState(if (scrolled) 0f else angleDegrees)
    val height by animateFloatAsState(if (scrolled) 100f else 160f)

    Surface(
        modifier = modifier.height(height.dp),
        shape = quadrilateralShape(
            cornerSizes(0.dp),
            angles(bottom = angle)
        ),
        color = backgroundColor,
        contentColor = contentColor,
        elevation = elevation
    ) {
        Box() {
            /* TODO: Calculer la taille correcte de requiredHeight */
            AsyncImage(
                modifier = Modifier.fillMaxSize().requiredHeight(250.dp),
                model = backgroundImage,
                contentDescription = "Background",
                contentScale = ContentScale.FillWidth,
                alpha = 0.4f
            )
            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .padding(
                        WindowInsets.systemBars
                            .only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
                            .asPaddingValues()
                    )
                    .fillMaxSize()
            ) {
                Row(
                    Modifier
                        .rotate(angle)
                        .fillMaxSize()
                        .clickable { onTitleClick() }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Row(Modifier.weight(1f).padding(horizontal = 16.dp)) {
                        ProvideTextStyle(value = MaterialTheme.typography.h4) {
                            titleContent()
                        }
                    }
                }
            }
            Row(
                Modifier
                    .padding(WindowInsets.systemBars.asPaddingValues())
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (navigationIcon != null) navigationIcon()
                Row(Modifier.weight(1f)) {}
                actions()
            }
        }
    }
}