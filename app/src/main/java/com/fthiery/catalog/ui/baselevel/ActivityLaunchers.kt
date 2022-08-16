package com.fthiery.catalog.ui.baselevel

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

@Composable
fun filePickerLauncher(onComplete: (Uri?) -> Unit): ManagedActivityResultLauncher<String, Uri?> {
    return rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        onComplete(it)
    }
}

@Composable
fun takePictureLauncher(onComplete: () -> Unit): ManagedActivityResultLauncher<Uri, Boolean> {
    return rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) onComplete()
    }
}