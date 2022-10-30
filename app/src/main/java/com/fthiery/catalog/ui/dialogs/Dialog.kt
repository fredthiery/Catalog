package com.fthiery.catalog.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.fthiery.catalog.R

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Dialog(
    title: String = "",
    onDismiss: () -> Unit,
    dismissText: String = stringResource(id = R.string.dismiss),
    onConfirm: () -> Unit,
    confirmText: String = stringResource(id = R.string.confirm),
    buttonColors: ButtonColors = ButtonDefaults.textButtonColors(),
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            elevation = 16.dp,
            modifier = Modifier
                .padding(16.dp)
                .widthIn(0.dp, 400.dp)
        ) {
            Column() {
                LazyColumn(Modifier.weight(1f, false)) {
                    item {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.h6,
                            modifier = Modifier
                                .paddingFromBaseline(top = 40.dp, bottom = 24.dp)
                                .padding(horizontal = 24.dp)
                        )
                    }
                    item {
                        ProvideTextStyle(value = MaterialTheme.typography.body1) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                            ) {
                                content()
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    verticalAlignment = Alignment.Bottom
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = buttonColors
                    ) { Text(dismissText.uppercase()) }
                    TextButton(
                        onClick = onConfirm,
                        colors = buttonColors
                    ) { Text(confirmText.uppercase()) }
                }
            }
        }
    }
}

@Composable
fun Dialog2(
    title: String = "",
    onDismiss: () -> Unit,
    dismissText: String = stringResource(id = R.string.dismiss),
    onConfirm: () -> Unit,
    confirmText: String = stringResource(id = R.string.confirm),
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onConfirm) { Text(confirmText.uppercase()) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(dismissText.uppercase()) } },
        title = { Text(title) },
        text = {
            Column(Modifier.background(Color.Green)) {
                Text("", fontSize = 0.sp)
                content()
            }
        },
    )
}