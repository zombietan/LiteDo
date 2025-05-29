package com.example.litedo.presentation.screen.todo.list

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.example.litedo.R
import com.example.litedo.presentation.component.button.TodoFloatingActionButton
import com.example.litedo.presentation.component.card.CardTodo
import com.example.litedo.presentation.component.dialog.TodoAlertDialog
import com.example.litedo.presentation.component.text.TextPlain
import com.example.litedo.presentation.component.topbar.TopBar
import com.example.litedo.presentation.component.topbar.TopBarIconButton
import com.example.litedo.presentation.component.topbar.TopBarMoreMenu
import com.example.litedo.presentation.component.topbar.TopBarSearch
import com.example.litedo.presentation.component.topbar.TopBarSortMenu
import com.example.litedo.presentation.screen.todo.add.TodoAddRoute
import com.example.litedo.presentation.screen.todo.edit.TodoEditRoute
import com.example.litedo.presentation.util.ObserveEvent
import kotlinx.serialization.Serializable

@Serializable
data object TodoListRoute

@Composable
fun TodoListScreen(
    snackbar: SnackbarHostState = remember { SnackbarHostState() },
    navController: NavHostController,
    viewModel: TodoListViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val todos = viewModel.todos.collectAsLazyPagingItems()
    val query by viewModel.query.collectAsStateWithLifecycle()

    ObserveEvent(
        flow = viewModel.event,
        onEvent = { event ->
            when (event) {
                TodoListViewModel.TodoListEvent.TodoDeleted -> {
                    val result = snackbar.showSnackbar(
                        message = context.getString(R.string.deleted_successfully),
                        actionLabel = context.getString(R.string.undo),
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) viewModel.onUndoDeletedTodo()
                }

            }
        }
    )

    LifecycleEventEffect(
        event = Lifecycle.Event.ON_RESUME,
        onEvent = {}
    )

    Scaffold(
        topBar = {
            TopBar(
                title = {
                    if (viewModel.searchExpanded) {
                        TopBarSearch(
                            query = query,
                            onQueryChange = viewModel::onQueryChange,
                            onCancel = viewModel::onSearchCollapsed
                        )
                    } else {
                        TextPlain(id = R.string.tasks)
                    }
                },
                actions = {
                    if (!viewModel.searchExpanded) {
                        TopBarIconButton(
                            onClick = viewModel::onSearchExpanded,
                            icon = Icons.Default.Search,
                            contentDescription = R.string.cd_search_button
                        )
                    }
                    TopBarSortMenu(
                        expanded = viewModel.sortExpanded,
                        sorting = viewModel.sorting,
                        onExpand = viewModel::onSortExpanded,
                        onDismiss = viewModel::onSortCollapsed,
                        onSort = viewModel::onSort
                    )
                    TopBarMoreMenu(
                        expanded = viewModel.menuExpanded,
                        onExpand = viewModel::onMenuExpanded,
                        onDismiss = viewModel::onMenuCollapsed,
                        hideCompleted = viewModel.hideCompleted,
                        onHideCompletedChange = viewModel::onHideCompletedChange,
                        onDeleteCompletedClick = viewModel::onDeleteCompletedShow,
                        onDeleteAllClick = viewModel::onDeleteAllShow,
                    )
                },
            )
        },
        floatingActionButton = {
            TodoFloatingActionButton(
                onClick = {
                    navController.navigate(TodoAddRoute)
                },
                icon = Icons.Default.Add,
                contentDescription = R.string.cd_task_add
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbar
            ) { data ->
                Snackbar(
                    snackbarData = data
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                count = todos.itemCount,
                key = todos.itemKey(
                    key = { todo ->
                        todo.id
                    }
                ),
                contentType = todos.itemContentType()
            ) { index ->
                val todo = todos[index]
                if (todo != null) {
                    CardTodo(
                        todo = todo,
                        onClick = {
                            navController.navigate(TodoEditRoute(it))
                        },
                        onCheckedChange = viewModel::onTodoChecked,
                        onDismiss = viewModel::onTodoDelete
                    )
                }
            }
        }
        if (viewModel.deleteCompletedShown) {
            TodoAlertDialog(
                title = R.string.confirm_deletion,
                message = R.string.wanna_delete_completed,
                onDismiss = viewModel::onDeleteCompletedDismiss,
                onConfirm = viewModel::onDeleteCompleted
            )
        }
        if (viewModel.deleteAllShown) {
            TodoAlertDialog(
                title = R.string.confirm_deletion,
                message = R.string.wanna_delete_all,
                onDismiss = viewModel::onDeleteAllDismiss,
                onConfirm = viewModel::onDeleteAll
            )
        }
    }
}