package com.fthiery.catalog.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.fthiery.catalog.R

@Composable
fun Dialog(
    title: String = "",
    onDismiss: () -> Unit,
    dismissText: String = stringResource(id = R.string.dismiss),
    onConfirm: () -> Unit,
    confirmText: String = stringResource(id = R.string.confirm),
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(shape = RoundedCornerShape(4.dp), elevation = 16.dp) {
            Column() {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .paddingFromBaseline(top = 40.dp, bottom = 24.dp)
                        .padding(horizontal = 24.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    content()
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    verticalAlignment = Alignment.Bottom
                ) {
                    TextButton(onClick = onDismiss) { Text(dismissText.uppercase()) }
                    TextButton(onClick = onConfirm) { Text(confirmText.uppercase()) }
                }
            }
        }
    }
}