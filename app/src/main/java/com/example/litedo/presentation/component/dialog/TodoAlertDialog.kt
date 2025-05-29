package com.example.litedo.presentation.component.dialog

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.litedo.R
import com.example.litedo.presentation.theme.LiteDoTheme
import com.example.litedo.presentation.theme.backgroundDialogButton

@Composable
fun TodoAlertDialog(
    @StringRes title: Int,
    @StringRes message: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(id = title))
        },
        text = {
            Text(text = stringResource(id = message))
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(id = R.string.no),
                    color = MaterialTheme.colorScheme.backgroundDialogButton()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(id = R.string.yes),
                    color = MaterialTheme.colorScheme.backgroundDialogButton()
                )
            }
        }
    )
}

@Preview(name = "Light Mode", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun TodoAlertDialogPreview() {
    LiteDoTheme {
        TodoAlertDialog(
            title = R.string.preview_message,
            message = R.string.cd_preview_message,
            onDismiss = {},
            onConfirm = {}
        )
    }
}