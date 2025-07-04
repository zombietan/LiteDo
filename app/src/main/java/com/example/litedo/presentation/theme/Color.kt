package com.example.litedo.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val Black = Color(0xFF000000)
val White = Color(0xFFFFFFFF)
val BrownGrey = Color(0xFFB19581)

val SemiTransparentUnspecified = Color.Unspecified.copy(alpha = 0.7f)

@Composable
fun ColorScheme.backgroundDialogButton(): Color {
    return if (isSystemInDarkTheme()) BrownGrey else primary
}
