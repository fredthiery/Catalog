package com.fthiery.catalog.ui.toplevel

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fthiery.catalog.R
import com.fthiery.catalog.copyToInternalStorage
import com.fthiery.catalog.ui.baselevel.*
import com.fthiery.catalog.ui.theme.angle
import com.fthiery.catalog.viewmodels.ItemDetailViewModel
import com.google.accompanist.insets.ui.TopAppBar

@Composable
fun ItemDetailScreen(
    viewModel: ItemDetailViewModel,
    navController: NavController
) {
    val item by mutableStateOf(viewModel.item)
    var modified by rememberSaveable { mutableStateOf(false) }

    var name by rememberSaveable(item.name) { mutableStateOf(item.name) }
    var description by rememberSaveable(item.description) { mutableStateOf(item.description) }

    /* TODO: Ajouter un dialog de confirmation si des changements ne sont pas sauvegardés */
    BackHandler { navController.navigateUp() }

    Box(Modifier.fillMaxSize()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            var nameEditDialog by rememberSaveable { mutableStateOf(false) }

            SlantedTopAppBar(
                angleDegrees = MaterialTheme.shapes.angle,
                backgroundImage = item.photos.getOrNull(0),
                onTitleClick = { nameEditDialog = true }
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    AnimatedVisibility(visible = name.isEmpty()) {
                        Text(
                            text = "Name of this item",
                            style = MaterialTheme.typography.caption,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colors.primary
                        )
                    }
                    Text(
                        text = name,
                        fontStyle = FontStyle.Italic
                    )
                }

                if (nameEditDialog) {
                    var editName by rememberSaveable(name) { mutableStateOf(name) }

                    fun dismiss() {
                        nameEditDialog = false
                        editName = name
                    }

                    AlertDialog(
                        onDismissRequest = { dismiss() },
                        title = { Text("Modify name") },
                        text = {
                            AutoFocusingOutlinedText(
                                label = "Name",
                                value = editName,
                                onValueChange = { editName = it },
                                singleLine = true
                            )
                        },
                        confirmButton = {
                            Button(onClick = {
                                nameEditDialog = false
                                name = editName
                                modified = true
                            }) { Text("Confirm") }
                        },
                        dismissButton = {
                            Button(onClick = { dismiss() }) { Text("Dismiss") }
                        }
                    )
                }
            }

            val photos = mutableStateListOf<Uri>()
            photos.addAll(item.photos)
            PhotoCardRow(
                photos = photos,
                onNewPhoto = { uri, context ->
                    copyToInternalStorage(uri, context)?.let {
                        photos.add(it)
                        item.photos = photos
                    }
                    modified = true
                },
                onClick = {
                    viewModel.displayPhoto = it
                    navController.navigate("DisplayPhoto")
                }
            )
            Surface(
                shape = quadrilateralShape(
                    cornerSizes(4.dp),
                    angles(top = MaterialTheme.shapes.angle)
                ),
                elevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                var descriptionEditDialog by rememberSaveable { mutableStateOf(false) }
                Column(
                    modifier = Modifier
                        .clickable { descriptionEditDialog = true }
                        .padding(16.dp)
                ) {

                    Text(
                        "Description",
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.primary,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-12).dp)
                    )
                    Text(description)

                    if (descriptionEditDialog) {
                        var editDescription by rememberSaveable(description) {
                            mutableStateOf(description)
                        }

                        fun dismiss() {
                            descriptionEditDialog = false
                            editDescription = description
                        }

                        AlertDialog(
                            onDismissRequest = { dismiss() },
                            title = { Text("Modify description") },
                            text = {
                                AutoFocusingOutlinedText(
                                    label = "Description",
                                    value = editDescription,
                                    onValueChange = { editDescription = it }
                                )
                            },
                            confirmButton = {
                                Button(onClick = {
                                    descriptionEditDialog = false
                                    description = editDescription
                                    modified = true
                                }) { Text("Confirm") }
                            },
                            dismissButton = {
                                Button(onClick = { dismiss() }) { Text("Dismiss") }
                            }
                        )
                    }
                }
            }
        }

        TopAppBar(
            title = {},
            contentPadding = WindowInsets.systemBars.asPaddingValues(),
            backgroundColor = Color.Transparent,
            elevation = 0.dp,
            contentColor = MaterialTheme.colors.onSurface,
            navigationIcon = {
                IconButton(onClick = navController::navigateUp) {
                    Icon(Icons.Filled.ArrowBack, "Back")
                }
            },
            actions = {
                var dropdownExpanded by remember { mutableStateOf(false) }
                var displayConfirmationDialog by remember { mutableStateOf(false) }
                IconButton(onClick = { dropdownExpanded = true }) {
                    Icon(Icons.Filled.MoreVert, "Menu")
                }
                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }) {
                    DropdownMenuItem(onClick = {
                        displayConfirmationDialog = true
                        dropdownExpanded = false
                    }) {
                        Text("Delete this item")
                    }
                }
                if (displayConfirmationDialog) {
                    AlertDialog(
                        onDismissRequest = { displayConfirmationDialog = false },
                        title = { Text("Delete this item") },
                        text = { Text("Are you sure ?") },
                        confirmButton = {
                            Button(onClick = {
                                displayConfirmationDialog = false
                                viewModel.deleteItem(item) { navController.navigateUp() }
                            }) {
                                Text("Delete")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { displayConfirmationDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        )

        AnimatedVisibility(
            modified,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(WindowInsets.systemBars.asPaddingValues())
                .padding(16.dp),
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            FloatingActionButton(
                onClick = {
                    // Save the item into the database
                    item.name = name
                    item.description = description
                    viewModel.saveItem(item)
                    modified = false
                }) {
                Icon(
                    Icons.Filled.Save,
                    contentDescription = stringResource(R.string.save_item)
                )
            }
        }
    }
}
