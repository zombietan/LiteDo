package com.example.litedo.presentation.component.topbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.litedo.R
import com.example.litedo.core.constant.TodoSort
import com.example.litedo.presentation.component.text.TextPlain
import com.example.litedo.presentation.theme.LiteDoTheme
import com.example.litedo.presentation.theme.SemiTransparentUnspecified
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun TopBarSortMenu(
    expanded: Boolean,
    sorting: TodoSort?,
    onExpand: () -> Unit,
    onDismiss: () -> Unit,
    onSort: (TodoSort) -> Unit,
    menus: ImmutableList<SortMenu> = SORT_MENUS
) {
    TopBarIconButton(
        onClick = onExpand,
        icon = Icons.AutoMirrored.Filled.Sort,
        contentDescription = R.string.cd_sort
    )
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        menus.forEach { menu: SortMenu ->
            DropdownMenuItem(
                onClick = { onSort(menu.sort) },
                text = {
                    TextPlain(
                        id = menu.titleResId,
                        color = SemiTransparentUnspecified
                    )
                },
                trailingIcon = {
                    if (sorting == menu.sort) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = stringResource(
                                id = R.string.cd_selected_sort,
                                stringResource(id = menu.titleResId)
                            ),
                            tint = LocalContentColor.current.copy(alpha = 0.7f)
                        )
                    }
                }
            )
        }
    }
}

data class SortMenu(
    val titleResId: Int,
    val sort: TodoSort
)

private val SORT_MENUS: ImmutableList<SortMenu> = persistentListOf(
    SortMenu(
        titleResId = R.string.sort_by_name,
        sort = TodoSort.Name
    ),
    SortMenu(
        titleResId = R.string.sort_by_date,
        sort = TodoSort.Date
    )
)

@Preview(showBackground = true)
@Composable
private fun TopBarSortMenuPreview() {
    LiteDoTheme {
        TopBarSortMenu(
            expanded = true,
            sorting = TodoSort.Name,
            onExpand = {},
            onDismiss = {},
            onSort = {},
        )
    }
}