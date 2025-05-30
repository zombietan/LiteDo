package com.example.litedo.presentation.component.topbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.litedo.R
import com.example.litedo.presentation.component.text.TextPlain
import com.example.litedo.presentation.theme.SemiTransparentUnspecified

@Composable
fun TopBarMoreMenu(
    expanded: Boolean,
    onExpand: () -> Unit,
    onDismiss: () -> Unit,
    hideCompleted: Boolean,
    onHideCompletedChange: () -> Unit,
    onDeleteCompletedClick: () -> Unit,
    onDeleteAllClick: () -> Unit,
) {
    TopBarIconButton(
        onClick = onExpand,
        icon = Icons.Default.MoreVert,
        contentDescription = R.string.cd_more_vertical
    )
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            text = {
                TextPlain(
                    id = R.string.hide_completed,
                    color = SemiTransparentUnspecified
                )
            },
            onClick = onHideCompletedChange,
            trailingIcon = {
                if (hideCompleted) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = stringResource(id = R.string.cd_hide_completed)
                    )
                }
            },
            leadingIcon = {
                TopBarIcon(
                    icon = Icons.Default.VisibilityOff,
                    contentDescription = R.string.hide_completed
                )
            }
        )
        DropdownMenuItem(
            text = {
                TextPlain(
                    id = R.string.delete_all_completed,
                    color = SemiTransparentUnspecified
                )
            },
            onClick = onDeleteCompletedClick,
            leadingIcon = {
                TopBarIcon(
                    icon = Icons.Default.Delete,
                    contentDescription = R.string.delete_all_completed
                )
            }
        )
        DropdownMenuItem(
            text = {
                TextPlain(
                    id = R.string.delete_all_tasks,
                    color = SemiTransparentUnspecified
                )
            },
            onClick = onDeleteAllClick,
            leadingIcon = {
                TopBarIcon(
                    icon = Icons.Default.DeleteForever,
                    contentDescription = R.string.delete_all_tasks
                )
            }
        )
    }
}