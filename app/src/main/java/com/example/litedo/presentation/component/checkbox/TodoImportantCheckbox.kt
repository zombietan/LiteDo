package com.example.litedo.presentation.component.checkbox

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.litedo.R
import com.example.litedo.presentation.theme.LiteDoTheme

@Composable
fun TodoImportantCheckbox(
    important: Boolean,
    onImportantChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.toggleable(
            value = important,
            role = Role.Checkbox,
            onValueChange = onImportantChange
        )
    ) {
        Checkbox(
            checked = important,
            onCheckedChange = onImportantChange
        )
        Text(text = stringResource(id = R.string.important_task))
        Spacer(modifier = Modifier.width(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun TodoImportantCheckboxPreview() {
    LiteDoTheme {
        TodoImportantCheckbox(
            important = true,
            onImportantChange = {}
        )
    }
}