package com.fthiery.catalog.ui.toplevel

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fthiery.catalog.R
import com.fthiery.catalog.contentColor
import com.fthiery.catalog.copyToInternalStorage
import com.fthiery.catalog.noRippleClickable
import com.fthiery.catalog.ui.baselevel.*
import com.fthiery.catalog.ui.dialogs.Dialog
import com.fthiery.catalog.ui.midlevel.PhotoCardRow
import com.fthiery.catalog.ui.midlevel.SlantedTopAppBar
import com.fthiery.catalog.ui.midlevel.TransparentScaffold
import com.fthiery.catalog.ui.theme.GLOBAL_ANGLE
import com.fthiery.catalog.viewmodels.ItemDetailViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ItemDetailScreen(
    viewModel: ItemDetailViewModel,
    navController: NavController
) {
    val item by mutableStateOf(viewModel.item)
    var modified by rememberSaveable { mutableStateOf(false) }

    var name by rememberSaveable(item.name) { mutableStateOf(item.name) }
    var description by rememberSaveable(item.description) { mutableStateOf(item.description) }

    var deleteConfirmationDialog by remember { mutableStateOf(false) }
    var nameEdit by remember { mutableStateOf(false) }
    var descriptionEditDialog by rememberSaveable { mutableStateOf(false) }
    var propertiesEditDialog by rememberSaveable { mutableStateOf(false) }

    /* TODO: Ajouter un dialog de confirmation si des changements ne sont pas sauvegardés */
    BackHandler { navController.navigateUp() }

    TransparentScaffold(
        modifier = Modifier.noRippleClickable { nameEdit = false },
        topBar = {
            TopAppBar(
                title = {},
                backgroundColor = Color.Transparent,
                elevation = 0.dp,
                contentColor = item.backgroundColor().contentColor(),
                navigationIcon = {
                    IconButton(onClick = navController::navigateUp) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    var dropdownExpanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { dropdownExpanded = true }) {
                        Icon(Icons.Filled.MoreVert, "Menu")
                    }
                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }) {
                        DropdownMenuItem(onClick = {
                            deleteConfirmationDialog = true
                            dropdownExpanded = false
                        }) {
                            Text("Delete this item")
                        }
                    }
                }
            )
        },
        drawerGesturesEnabled = false,
        floatingActionButton = {
            AnimatedVisibility(
                modified,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                val context = LocalContext.current
                FloatingActionButton(
                    onClick = {
                        // Save the item into the database
                        item.name = name
                        item.description = description
                        viewModel.save(item, context)
                        modified = false
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        Icons.Filled.Save,
                        contentDescription = stringResource(R.string.save_item)
                    )
                }
            }
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(WindowInsets.navigationBars.asPaddingValues())
                .padding(bottom = 8.dp)
        ) {
            SlantedTopAppBar(
                angleDegrees = GLOBAL_ANGLE,
                backgroundImage = item.photos.getOrNull(0),
                backgroundColor = item.backgroundColor(),
                onTitleClick = { nameEdit = true }
            ) {
                Crossfade(nameEdit) { editing ->
                    when (editing) {
                        false -> {
                            Crossfade(name.isEmpty()) { empty ->
                                when (empty) {
                                    true -> Text(
                                        text = "Name of this item",
                                        style = MaterialTheme.typography.subtitle1,
                                        fontStyle = FontStyle.Italic,
                                        color = item.backgroundColor()
                                            .contentColor()
                                            .copy(alpha = 0.8f)
                                    )
                                    false -> Text(name)
                                }
                            }
                        }
                        true  -> {
                            var textFieldValue by rememberSaveable(name) { mutableStateOf(name) }
                            var suggestionsExpanded by remember { mutableStateOf(false) }
                            val suggestions by mutableStateOf(viewModel.suggestions)

                            BackHandler { nameEdit = false }

                            ExposedDropdownMenuBox(
                                expanded = suggestionsExpanded,
                                onExpandedChange = { suggestionsExpanded = !suggestionsExpanded }
                            ) {
                                AutoFocusingBasicText(
                                    value = textFieldValue,
                                    onValueChange = {
                                        textFieldValue = it
                                        viewModel.getSuggestions(textFieldValue) {
                                            suggestionsExpanded = suggestions.isNotEmpty()
                                        }
                                    },
                                    textStyle = LocalTextStyle.current
                                        .copy(color = item.backgroundColor().contentColor()),
                                    cursorBrush = SolidColor(item.backgroundColor().contentColor()),
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Sentences,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(onDone = {
                                        nameEdit = false
                                        name = textFieldValue
                                        modified = true
                                    })
                                )
                                ExposedDropdownMenu(
                                    expanded = suggestionsExpanded,
                                    onDismissRequest = { suggestionsExpanded = false }
                                ) {
                                    suggestions.forEach { search ->
                                        DropdownMenuItem(onClick = {
                                            textFieldValue = search.title ?: textFieldValue
                                            suggestionsExpanded = false
                                        }) {
                                            Text(search.title ?: "")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // PHOTOS
            val photos = mutableStateListOf<Uri>()
            photos.addAll(item.photos)
            PhotoCardRow(
                photos = photos,
                color = item.lightColor(),
                angleDegrees = GLOBAL_ANGLE,
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

            // DESCRIPTION
            SlantedSurface(
                title = "Description",
                titleColor = item.lightColor(),
                onClick = { descriptionEditDialog = true }
            ) {
                Text(description)
            }

            // PROPERTIES
            val propertiesList = item.properties.map { it.key to it.value }
            SlantedSurface(
                title = "Details",
                titleColor = item.lightColor(),
                onClick = { propertiesEditDialog = true }
            ) {
                propertiesList.forEachIndexed { index, property ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = property.first.uppercase(),
                            color = item.lightColor(),
                            style = MaterialTheme.typography.overline,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .weight(0.3f)
                                .alignByBaseline()
                        )
                        Text(
                            text = property.second,
                            modifier = Modifier
                                .weight(0.7f)
                                .alignByBaseline()
                        )
                    }
                    if (index < propertiesList.lastIndex)
                        Divider(Modifier.padding(4.dp))
                }
            }
        }
    }

    if (deleteConfirmationDialog) {
        Dialog(
            title = "You're about to delete the item ${item.name} !",
            onDismiss = { deleteConfirmationDialog = false },
            dismissText = "Cancel",
            onConfirm = {
                deleteConfirmationDialog = false
                viewModel.delete(item) { navController.navigateUp() }
            },
            confirmText = "Delete",
            content = { Text("Are you sure ?") }
        )
    }

    if (descriptionEditDialog) {
        var editDescription by rememberSaveable(description) {
            mutableStateOf(description)
        }
        Dialog(
            title = "Edit description",
            onDismiss = {
                descriptionEditDialog = false
                editDescription = description
            },
            onConfirm = {
                descriptionEditDialog = false
                description = editDescription
                modified = true
            }
        ) {
            AutoFocusingOutlinedText(
                label = "Description",
                value = editDescription,
                onValueChange = { editDescription = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
            )
        }
    }

    if (propertiesEditDialog) {
        val properties = remember { mutableStateListOf<Pair<String, String>>() }
        properties.addAll(item.properties.map { it.key to it.value })
        Dialog(
            title = "Edit details",
            onDismiss = { propertiesEditDialog = false },
            onConfirm = {
                propertiesEditDialog = false
                modified = true
                properties.forEach {
                    item.properties[it.first] = it.second
                }
            }
        ) {
            var newProperty by remember { mutableStateOf(false) }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                properties.forEachIndexed { index, item ->
                    var value by rememberSaveable { mutableStateOf(item.second) }
                    OutlinedTextField(
                        value = value,
                        onValueChange = {
                            value = it
                            properties[index] = item.first to value
                        },
                        label = { Text(item.first) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Row(
                    Modifier.fillMaxWidth(),
                    Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    Alignment.CenterVertically
                ) {
                    if (newProperty) {
                        var newPropertyName by remember { mutableStateOf("") }
                        AutoFocusingOutlinedText(
                            value = newPropertyName,
                            onValueChange = { newPropertyName = it },
                            label = "Custom field name",
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = {
                            properties.add(newPropertyName to "")
                            /* TODO: mettre le focus sur le nouveau champ */
                            newProperty = false
                        }) {
                            Text("Add field".uppercase())
                        }
                    } else TextButton(onClick = { newProperty = true }) {
                        Text("New Custom field".uppercase())
                    }
                }
            }
        }
    }
}

@Composable
fun SlantedSurface(
    title: String = "",
    titleColor: Color = MaterialTheme.colors.primary,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        shape = quadrilateralShape(
            cornerSizes(4.dp),
            angles(horizontal = GLOBAL_ANGLE)
        ),
        elevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable { onClick() }
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.h5,
                color = titleColor,
                textAlign = TextAlign.End,
                fontStyle = FontStyle.Italic,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-4).dp)
                    .rotate(GLOBAL_ANGLE)
            )
            content()
        }
    }
}