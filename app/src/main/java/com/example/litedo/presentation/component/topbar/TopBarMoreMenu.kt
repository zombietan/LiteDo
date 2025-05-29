package com.example.litedo.presentation.component.topbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.litedo.R
import com.example.litedo.presentation.component.text.TextPlain

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
                    id = R.string.hide_completed
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
                    icon = R.drawable.ic_hide,
                    contentDescription = R.string.hide_completed
                )
            }
        )
        DropdownMenuItem(
            text = {
                TextPlain(
                    id = R.string.delete_all_completed
                )
            },
            onClick = onDeleteCompletedClick,
            leadingIcon = {
                TopBarIcon(
                    icon = R.drawable.ic_delete,
                    contentDescription = R.string.delete_all_completed
                )
            }
        )
        DropdownMenuItem(
            text = {
                TextPlain(
                    id = R.string.delete_all_tasks
                )
            },
            onClick = onDeleteAllClick,
            leadingIcon = {
                TopBarIcon(
                    icon = R.drawable.ic_delete,
                    contentDescription = R.string.delete_all_tasks
                )
            }
        )
    }
}