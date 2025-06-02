package com.example.litedo.presentation.screen.todo.list

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.example.litedo.R
import com.example.litedo.domain.model.TodoModel
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
import com.example.litedo.presentation.theme.LiteDoTheme
import com.example.litedo.presentation.util.ObserveEvent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveEvent(
        flow = viewModel.event,
        onEvent = { event ->
            when (event) {
                TodoListViewModel.TodoListEvent.TodoDeleted -> {
                    if (snackbar.currentSnackbarData == null) {
                        val result = snackbar.showSnackbar(
                            message = context.getString(R.string.deleted_successfully),
                            actionLabel = context.getString(R.string.undo),
                            duration = SnackbarDuration.Short
                        )
                        if (result == SnackbarResult.ActionPerformed) viewModel.onUndoDeletedTodo()
                    }
                }

                is TodoListViewModel.TodoListEvent.Error -> {
                    snackbar.showSnackbar(event.errorMessage)
                }
            }
        }
    )


    LifecycleEventEffect(
        event = Lifecycle.Event.ON_RESUME,
        onEvent = {}
    )

    TodoListContent(
        snackbar = snackbar,
        query = query,
        todos = todos,
        onAction = viewModel::onAction,
        onNavigateTodoEdit = {
            navController.navigate(TodoEditRoute(it))
        },
        onNavigateTodoAdd = {
            navController.navigate(TodoAddRoute)
        },
        uiState = uiState
    )

}

@Composable
private fun TodoListContent(
    snackbar: SnackbarHostState,
    query: String,
    todos: LazyPagingItems<TodoModel>,
    onAction: (TodoListAction) -> Unit,
    onNavigateTodoEdit: (TodoModel) -> Unit,
    onNavigateTodoAdd: () -> Unit,
    uiState: TodoListUiState
) {
    LaunchedEffect(snackbar) {
        snapshotFlow { snackbar.currentSnackbarData }
            .distinctUntilChanged()
            .filter { it == null }
            .collectLatest {
                onAction(TodoListAction.DeletedTodosClear)
            }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = {
                    if (uiState.searchExpanded) {
                        TopBarSearch(
                            query = query,
                            onQueryChange = {
                                onAction(TodoListAction.QueryChange(it))
                            },
                            onCancel = {
                                onAction(TodoListAction.SearchCollapsed)
                            }
                        )
                    } else {
                        TextPlain(id = R.string.tasks)
                    }
                },
                actions = {
                    if (!uiState.searchExpanded) {
                        TopBarIconButton(
                            onClick = {
                                onAction(TodoListAction.SearchExpanded)
                            },
                            icon = Icons.Default.Search,
                            contentDescription = R.string.cd_search_button
                        )
                    }
                    TopBarSortMenu(
                        expanded = uiState.sortExpanded,
                        sorting = uiState.sorting,
                        onExpand = {
                            onAction(TodoListAction.SortExpanded)
                        },
                        onDismiss = {
                            onAction(TodoListAction.SortCollapsed)
                        },
                        onSort = {
                            onAction(TodoListAction.Sort(it))
                        }
                    )
                    TopBarMoreMenu(
                        expanded = uiState.menuExpanded,
                        onExpand = {
                            onAction(TodoListAction.MenuExpanded)
                        },
                        onDismiss = {
                            onAction(TodoListAction.MenuCollapsed)
                        },
                        hideCompleted = uiState.hideCompleted,
                        onHideCompletedChange = {
                            onAction(TodoListAction.HideCompletedChange)
                        },
                        onDeleteCompletedClick = {
                            onAction(TodoListAction.DeleteCompletedShow)
                        },
                        onDeleteAllClick = {
                            onAction(TodoListAction.DeleteAllShow)
                        }
                    )
                },
            )
        },
        floatingActionButton = {
            TodoFloatingActionButton(
                onClick = onNavigateTodoAdd,
                icon = Icons.Default.Add,
                contentDescription = R.string.cd_task_add
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbar
            ) { data ->
                val isError = data.visuals.message.startsWith("Error:")
                Snackbar(
                    snackbarData = data,
                    containerColor = if (isError) MaterialTheme.colorScheme.error
                    else SnackbarDefaults.color
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
                        onClick = onNavigateTodoEdit,
                        onCheckedChange = { todo: TodoModel, checked: Boolean ->
                            onAction(TodoListAction.TodoChecked(todo = todo, checked = checked))
                        },
                        onDismiss = {
                            onAction(TodoListAction.TodoDelete(todo = it))
                        }
                    )
                }
            }
        }
        if (uiState.deleteCompletedShown) {
            TodoAlertDialog(
                title = R.string.confirm_deletion,
                message = R.string.wanna_delete_completed,
                onDismiss = {
                    onAction(TodoListAction.DeleteCompletedDismiss)
                },
                onConfirm = {
                    onAction(TodoListAction.DeleteCompleted)
                }

            )
        }
        if (uiState.deleteAllShown) {
            TodoAlertDialog(
                title = R.string.confirm_deletion,
                message = R.string.wanna_delete_all,
                onDismiss = {
                    onAction(TodoListAction.DeleteAllDismiss)
                },
                onConfirm = {
                    onAction(TodoListAction.DeleteAll)
                }
            )
        }
    }
}

@Preview
@Composable
private fun TodoListContentPreview() {
    LiteDoTheme {
        val dummyPagingData = PagingData.from(
            listOf(
                TodoModel(
                    id = 1,
                    name = "first todo",
                    important = true,
                    completed = false
                ),
                TodoModel(
                    id = 2,
                    name = "second todo",
                    important = false,
                    completed = true
                ),
            )
        )
        // Flow<PagingData<TodoModel>> を作る
        val dummyFlow = flowOf(dummyPagingData)
        val todos = dummyFlow.collectAsLazyPagingItems()
        TodoListContent(
            snackbar = remember { SnackbarHostState() },
            query = "",
            todos = todos,
            onAction = { _ -> },
            onNavigateTodoEdit = {},
            onNavigateTodoAdd = {},
            uiState = TodoListUiState()
        )
    }
}