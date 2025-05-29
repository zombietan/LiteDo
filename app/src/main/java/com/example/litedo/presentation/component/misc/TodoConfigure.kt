package com.example.litedo.presentation.component.misc

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.litedo.presentation.component.checkbox.TodoImportantCheckbox
import com.example.litedo.presentation.component.textfield.TodoTextField
import com.example.litedo.presentation.theme.LiteDoTheme

@Composable
fun TodoConfigure(
    name: String,
    onNameChange: (String) -> Unit,
    important: Boolean,
    onImportantChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TodoTextField(
            value = name,
            onValueChange = onNameChange
        )
        Spacer(modifier = Modifier.height(8.dp))
        TodoImportantCheckbox(
            important = important,
            onImportantChange = onImportantChange
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TodoConfigurePreview() {
    LiteDoTheme {
        TodoConfigure(
            name = "todo",
            onNameChange = {},
            important = true,
            onImportantChange = {}
        )
    }
}