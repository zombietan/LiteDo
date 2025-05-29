package com.example.litedo.presentation.component.textfield

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.litedo.R
import com.example.litedo.presentation.component.text.TextPlain
import com.example.litedo.presentation.theme.LiteDoTheme

@Composable
fun TodoTextField(
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            TextPlain(
                id = R.string.task_name
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun TodoTextFieldPreview() {
    LiteDoTheme {
        TodoTextField(
            value = "This is important todo",
            onValueChange = {}
        )
    }
}