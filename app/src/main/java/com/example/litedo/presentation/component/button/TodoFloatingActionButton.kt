package com.example.litedo.presentation.component.button

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.litedo.R
import com.example.litedo.presentation.theme.LiteDoTheme

@Composable
fun TodoFloatingActionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    @StringRes contentDescription: Int
) {
    val configuration = LocalConfiguration.current
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
        } else {
            Modifier
        }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(id = contentDescription)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TodoFloatingActionButtonPreview() {
    LiteDoTheme {
        TodoFloatingActionButton(
            onClick = {},
            icon = Icons.Default.Done,
            contentDescription = R.string.cd_done_button
        )
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=891dp,height=411dp,orientation=landscape"
)
@Composable
private fun TodoFloatingActionButtonLandscapePreview() {
    LiteDoTheme {
        TodoFloatingActionButton(
            onClick = {},
            icon = Icons.Default.Add,
            contentDescription = R.string.cd_done_button
        )
    }
}