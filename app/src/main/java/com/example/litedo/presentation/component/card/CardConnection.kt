package com.example.litedo.presentation.component.card

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.litedo.R
import com.example.litedo.presentation.theme.LiteDoTheme


@Composable
fun CardConnection(
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    @StringRes title: Int,
    @StringRes contentDescription: Int,
    fillMaxSize: Boolean,
    modifier: Modifier
) {
    ElevatedCard(
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .then(if (fillMaxSize) Modifier.fillMaxSize() else Modifier)
                .padding(16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = stringResource(id = contentDescription),
                    tint = White,
                    modifier = Modifier.size(32.dp)
                )
            }
            Text(
                text = stringResource(id = title),
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CardConnectionPreview() {
    LiteDoTheme {
        CardConnection(
            onClick = {},
            icon = R.drawable.ic_telegram,
            title = R.string.preview_message,
            contentDescription = R.string.cd_preview_message,
            fillMaxSize = false,
            modifier = Modifier
        )
    }
}