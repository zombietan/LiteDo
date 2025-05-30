package com.example.litedo.presentation.component.text

import androidx.annotation.StringRes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource

@Composable
fun TextPlain(@StringRes id: Int, color: Color = Color.Unspecified) {
    Text(text = stringResource(id = id), color = color)
}