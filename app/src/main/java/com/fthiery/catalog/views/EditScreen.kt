package com.fthiery.catalog.views

import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.fthiery.catalog.BuildConfig
import com.fthiery.catalog.R
import com.fthiery.catalog.viewmodels.MainViewModel
import com.google.accompanist.insets.ui.TopAppBar
import java.util.*

@Composable
fun EditScreen(
    viewModel: MainViewModel
) {
    var name by rememberSaveable { mutableStateOf(viewModel.currentItem.name) }
    var description by rememberSaveable { mutableStateOf(viewModel.currentItem.description) }
    var photo by rememberSaveable { mutableStateOf(viewModel.currentItem.photo) }

    var dropdownExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    // Provides a temp Content Uri for the TakePicture contract
    val tmpUri: Uri = FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.provider",
        context.filesDir.resolve("tmp_image.jpg")
    )

    // Image pickers
    val filePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            photo = copyToInternalStorage(uri, context)
        }
    val takePictureLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) photo = copyToInternalStorage(tmpUri, context)
        }

    BackHandler(enabled = true) {
        viewModel.modeEdit = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                contentPadding = WindowInsets
                    .statusBars
                    .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                    .asPaddingValues(),
                title = { Text(text = stringResource(id = R.string.title_activity_edit)) },
                backgroundColor = MaterialTheme.colors.background,
                actions = {
                    IconButton(onClick = { viewModel.modeEdit = false }) {
                        Icon(Icons.Filled.Close, "Close")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier
                    .systemBarsPadding()
                    .imePadding(),
                onClick = {
                    viewModel.saveItem(
                        viewModel.currentItem.copy(
                            name = name,
                            description = description,
                            photo = photo
                        )
                    )
                    viewModel.modeEdit = false
                }) {
                Icon(Icons.Filled.Save, contentDescription = stringResource(R.string.save_item))
            }
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { value -> name = value },
                label = { Text(stringResource(id = R.string.label_item_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { value -> description = value },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier
                        .clickable {
                            dropdownExpanded = true
                        }
                        .width(96.dp)
                        .height(96.dp)
                ) {
                    Icon(
                        Icons.Filled.AddAPhoto,
                        stringResource(R.string.add_a_photo),
                        modifier = Modifier.padding(32.dp)
                    )
                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }) {
                        DropdownMenuItem(
                            onClick = {
                                filePickerLauncher.launch("image/*")
                                dropdownExpanded = false
                            }) {
                            Text("Pick image from storage")
                        }
                        DropdownMenuItem(
                            onClick = {
                                takePictureLauncher.launch(tmpUri)
                                dropdownExpanded = false
                            }) {
                            Text("Take a photo")
                        }
                    }
                }
                AnimatedVisibility(photo != null) {
                    Card(
                        modifier = Modifier
                            .width(96.dp)
                            .height(96.dp)
                    ) {
                        AsyncImage(
                            model = photo,
                            contentDescription = "Photo",
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

private fun copyToInternalStorage(uri: Uri?, context: Context): Uri? {
    if (uri == null) return null
    // Copy the selected file to internal storage
    val input = context.contentResolver.openInputStream(uri) ?: return null
    val outputFile = context.filesDir.resolve("${UUID.randomUUID()}.jpg")
    input.copyTo(outputFile.outputStream())
    return outputFile.toUri()
}
