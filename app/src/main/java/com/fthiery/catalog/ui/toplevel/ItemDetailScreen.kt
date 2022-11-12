package com.fthiery.catalog.ui.toplevel

import android.util.Log
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
import com.fthiery.catalog.*
import com.fthiery.catalog.R
import com.fthiery.catalog.datasources.wikiparser.WikiTextParser
import com.fthiery.catalog.models.Item
import com.fthiery.catalog.models.Search
import com.fthiery.catalog.ui.baselevel.*
import com.fthiery.catalog.ui.dialogs.Dialog
import com.fthiery.catalog.ui.midlevel.PhotoCardRow
import com.fthiery.catalog.ui.midlevel.SlantedTopAppBar
import com.fthiery.catalog.ui.midlevel.TransparentScaffold
import com.fthiery.catalog.ui.theme.GLOBAL_ANGLE
import com.fthiery.catalog.viewmodels.ItemDetailViewModel
import java.util.*

@Composable
fun ItemDetailScreen(
    item: Item,
    viewModel: ItemDetailViewModel,
    navController: NavController
) {
    var modified by rememberSaveable { mutableStateOf(false) }

    var name by rememberSaveable(item.name) { mutableStateOf(item.name) }
    var description by rememberSaveable(item.description) { mutableStateOf(item.description) }
    var properties by rememberSaveable(item.properties) { mutableStateOf(item.properties) }
    var photos by rememberSaveable(item.photos) { mutableStateOf(item.photos) }

    var deleteConfirmationDialog by remember { mutableStateOf(false) }
    var nameEdit by remember { mutableStateOf(false) }
    var descriptionEditDialog by rememberSaveable { mutableStateOf(false) }
    var propertiesEditDialog by rememberSaveable { mutableStateOf(false) }
    var wikipediaSuggestionsDialog by remember { mutableStateOf(false) }

    val buttonColors = ButtonDefaults.textButtonColors(contentColor = item.backgroundColor())

    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        textColor = MaterialTheme.colors.onSurface,
        cursorColor = item.backgroundColor(),
        focusedBorderColor = item.backgroundColor(),
        focusedLabelColor = item.backgroundColor()
    )

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
                        Icon(Icons.Filled.ArrowBack, stringResource(R.string.back))
                    }
                },
                actions = {
                    var dropdownExpanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { dropdownExpanded = true }) {
                        Icon(Icons.Filled.MoreVert, stringResource(R.string.menu))
                    }
                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }) {
                        DropdownMenuItem(onClick = {
                            wikipediaSuggestionsDialog = true
                            dropdownExpanded = false
                        }) {
                            Text(stringResource(R.string.fetch_from_wikipedia))
                        }
                        DropdownMenuItem(onClick = {
                            deleteConfirmationDialog = true
                            dropdownExpanded = false
                        }) {
                            Text(stringResource(R.string.delete_this_item))
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
                        // TODO: Si description et détails vides, proposer de les récupérer sur Wikipedia
                        val photosToDelete = item.photos - photos.toSet()
                        Log.i("debug", "Photos to delete:\n" + photosToDelete.joinToString("\n"))
                        photosToDelete.forEach { deleteFromInternalStorage(it, context) }

                        val itemToSave = item.copy(
                            name = name,
                            description = description,
                            photos = photos,
                            properties = properties,
                            lastUpdated = Calendar.getInstance()
                        )
                        viewModel.save(itemToSave, context) {
                            navController.popBackStack()
                            navController.navigate("ReloadItem/$it")
                        }
                        modified = false
                    },
                    backgroundColor = item.backgroundColor(),
                    contentColor = item.backgroundColor().contentColor(),
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
                backgroundImage = photos.getOrNull(0),
                backgroundColor = item.backgroundColor(),
                onTitleClick = { nameEdit = true }
            ) {
                Crossfade(nameEdit) { editing ->
                    when (editing) {
                        false -> {
                            Crossfade(name.isEmpty()) { empty ->
                                when (empty) {
                                    true -> Text(
                                        text = stringResource(R.string.name_of_this_item),
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

                            BackHandler { nameEdit = false }

                            AutoFocusingBasicText(
                                value = textFieldValue,
                                onValueChange = { textFieldValue = it },
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
                        }
                    }
                }
            }

            // PHOTOS
            PhotoCardRow(
                photos = photos,
                color = item.backgroundColor(),
                angleDegrees = GLOBAL_ANGLE,
                onNewPhoto = { uri, context ->
                    copyToInternalStorage(uri, context)?.let {
                        val newPhotos = photos.toMutableList()
                        newPhotos.add(it)
                        photos = newPhotos
                    }
                    modified = true
                },
                onDeletePhoto = { uri ->
                    val newPhotos = photos.toMutableList()
                    newPhotos.remove(uri)
                    photos = newPhotos
                    modified = true
                },
                onClick = { navController.navigate("DisplayPhoto?uri=$it") }
            )

            // DESCRIPTION
            SlantedSurface(
                title = stringResource(R.string.description),
                titleColor = item.backgroundColor(),
                onClick = { descriptionEditDialog = true }
            ) {
                Text(description)
            }

            // PROPERTIES
            val propertiesList = properties.map { it.key to it.value }
            SlantedSurface(
                title = stringResource(R.string.details),
                titleColor = item.backgroundColor(),
                onClick = { propertiesEditDialog = true }
            ) {
                propertiesList.forEachIndexed { index, property ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = property.first.uppercase(),
                            color = item.backgroundColor(),
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
            title = stringResource(id = R.string.about_to_delete_item, item.name),
            onDismiss = { deleteConfirmationDialog = false },
            dismissText = stringResource(R.string.cancel),
            onConfirm = {
                deleteConfirmationDialog = false
                viewModel.delete(item) { navController.navigateUp() }
            },
            confirmText = stringResource(R.string.delete),
            buttonColors = buttonColors
        ) { Text(stringResource(R.string.are_you_sure)) }
    }

    if (descriptionEditDialog) {
        var editDescription by rememberSaveable(description) {
            mutableStateOf(description)
        }
        Dialog(
            title = stringResource(R.string.edit_description),
            onDismiss = {
                descriptionEditDialog = false
                editDescription = description
            },
            onConfirm = {
                descriptionEditDialog = false
                description = editDescription
                modified = true
            },
            buttonColors = buttonColors
        ) {
            AutoFocusingOutlinedText(
                label = stringResource(id = R.string.description),
                value = editDescription,
                onValueChange = { editDescription = it },
                colors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
            )
        }
    }

    if (wikipediaSuggestionsDialog) {
        var wikiArticleTitle by remember { mutableStateOf<String?>(null) }
        Dialog(
            title = stringResource(R.string.wikipedia_suggestions_title),
            onDismiss = { wikipediaSuggestionsDialog = false },
            onConfirm = {
                // If a suggestion has been selected, get the details
                wikiArticleTitle?.let {
                    viewModel.fetchDetailsFromWikipedia(it) { newDescription, newProperties ->
                        description = newDescription
                        properties = newProperties
                    }
                }
                modified = true
                wikipediaSuggestionsDialog = false
            },
            buttonColors = buttonColors
        ) {
            var suggestions by remember { mutableStateOf<List<Search>>(listOf()) }
            viewModel.getSuggestions(name) { suggestions = it }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                suggestions.forEach { suggestion ->
                    Card(
                        elevation = 0.dp,
                        backgroundColor = if (wikiArticleTitle == suggestion.title) item.backgroundColor() else Color.Transparent,
                        contentColor = if (wikiArticleTitle == suggestion.title) item.backgroundColor()
                            .contentColor() else MaterialTheme.colors.onSurface,
                        modifier = Modifier
                            .clickable { wikiArticleTitle = suggestion.title }
                    ) {
                        Column(modifier = Modifier.padding(4.dp)) {
                            Text(text = suggestion.title ?: "")
                            Text(
                                text = WikiTextParser(suggestion.snippet ?: "").parse().toString(),
                                style = MaterialTheme.typography.caption
                            )
                        }
                    }
                }
            }
        }
    }

    if (propertiesEditDialog) {
        val propertyList = remember { mutableStateListOf<Pair<String, String>>() }
        propertyList.clear()
        propertyList.addAll(properties.map { it.key to it.value })
        Dialog(
            title = stringResource(R.string.edit_details),
            onDismiss = { propertiesEditDialog = false },
            onConfirm = {
                propertiesEditDialog = false
                modified = true
                val propertyMap = mutableMapOf<String, String>()
                propertyList.forEach { propertyMap[it.first] = it.second }
                properties = propertyMap
            },
            buttonColors = buttonColors
        ) {
            var newProperty by remember { mutableStateOf(false) }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                propertyList.forEachIndexed { index, item ->
                    var value by rememberSaveable { mutableStateOf(item.second) }
                    OutlinedTextField(
                        value = value,
                        onValueChange = {
                            value = it
                            propertyList[index] = item.first to value
                        },
                        label = { Text(item.first) },
                        colors = textFieldColors,
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
                            label = stringResource(R.string.custom_field_name),
                            colors = textFieldColors,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = {
                                propertyList.add(newPropertyName to "")
                                /* TODO: mettre le focus sur le nouveau champ */
                                newProperty = false
                            },
                            colors = buttonColors
                        ) {
                            Text(stringResource(R.string.add_field).uppercase())
                        }
                    } else TextButton(
                        onClick = { newProperty = true },
                        colors = buttonColors
                    ) {
                        Text(stringResource(R.string.new_custom_field).uppercase())
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