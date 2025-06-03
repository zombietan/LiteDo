package com.example.litedo.presentation.screen.todo.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.litedo.core.constant.TodoSort
import com.example.litedo.domain.model.TodoModel
import com.example.litedo.domain.repository.DataStoreRepository
import com.example.litedo.domain.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TodoListUiState(
    val searchExpanded: Boolean = false,
    val menuExpanded: Boolean = false,
    val sortExpanded: Boolean = false,
    val hideCompleted: Boolean = false,
    val sorting: TodoSort? = null,
    val deleteCompletedShown: Boolean = false,
    val deleteAllShown: Boolean = false
)

sealed interface TodoListAction {
    data class QueryChange(val query: String) : TodoListAction
    data object SearchCollapsed : TodoListAction
    data object SearchExpanded : TodoListAction
    data object SortExpanded : TodoListAction
    data object SortCollapsed : TodoListAction
    data class Sort(val todoSort: TodoSort) : TodoListAction
    data object MenuExpanded : TodoListAction
    data object MenuCollapsed : TodoListAction
    data object HideCompletedChange : TodoListAction
    data object DeleteCompletedShow : TodoListAction
    data object DeleteAllShow : TodoListAction
    data class TodoChecked(val todo: TodoModel, val checked: Boolean) : TodoListAction
    data class TodoDelete(val todo: TodoModel) : TodoListAction
    data object DeleteCompletedDismiss : TodoListAction
    data object DeleteCompleted : TodoListAction
    data object DeleteAllDismiss : TodoListAction
    data object DeleteAll : TodoListAction
    data object DeletedTodosClear : TodoListAction
    data object UndoDeletedTodo : TodoListAction
}


@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val repository: TodoRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodoListUiState())
    val uiState: StateFlow<TodoListUiState> = _uiState.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val todos: Flow<PagingData<TodoModel>> =
        combine(
            query,
            dataStoreRepository.getSorting,
            dataStoreRepository.getHideCompleted
        ) { query, sorting, hideCompleted ->
            Triple(query, sorting, hideCompleted)
        }.flatMapLatest { (query, sorting, hideCompleted) ->
            _uiState.update { it.copy(hideCompleted = hideCompleted) }
            _uiState.update { it.copy(sorting = sorting) }
            repository.getTodos(
                query = query,
                hideCompleted = hideCompleted,
                sorting = sorting
            ).cachedIn(viewModelScope)
        }

    private val _event = Channel<TodoListEvent>()
    val event: Flow<TodoListEvent> = _event.receiveAsFlow()

    private val deletedTodos: MutableList<TodoModel> = mutableListOf()

    fun onAction(action: TodoListAction) {
        when (action) {
            TodoListAction.DeleteAll -> {
                onDeleteAll()
            }

            TodoListAction.DeleteAllDismiss -> {
                onDeleteAllDismiss()
            }

            TodoListAction.DeleteAllShow -> {
                onDeleteAllShow()
            }

            TodoListAction.DeleteCompleted -> {
                onDeleteCompleted()
            }

            TodoListAction.DeleteCompletedDismiss -> {
                onDeleteCompletedDismiss()
            }

            TodoListAction.DeleteCompletedShow -> {
                onDeleteCompletedShow()
            }

            TodoListAction.HideCompletedChange -> {
                onHideCompletedChange()
            }

            TodoListAction.MenuCollapsed -> {
                onMenuCollapsed()
            }

            TodoListAction.MenuExpanded -> {
                onMenuExpanded()
            }

            is TodoListAction.QueryChange -> {
                onQueryChange(action.query)
            }

            TodoListAction.SearchExpanded -> {
                onSearchExpanded()
            }

            is TodoListAction.Sort -> {
                onSort(action.todoSort)
            }

            TodoListAction.SortCollapsed -> {
                onSortCollapsed()
            }

            TodoListAction.SortExpanded -> {
                onSortExpanded()
            }

            is TodoListAction.TodoChecked -> {
                onTodoChecked(action.todo, action.checked)
            }

            is TodoListAction.TodoDelete -> {
                onTodoDelete(action.todo)
            }

            TodoListAction.SearchCollapsed -> {
                onSearchCollapsed()
            }

            TodoListAction.DeletedTodosClear -> {
                onDeletedTodosClear()
            }

            TodoListAction.UndoDeletedTodo -> {
                onUndoDeletedTodo()
            }
        }
    }

    private fun onQueryChange(value: String) {
        _query.value = value
    }

    private fun onTodoChecked(todo: TodoModel, checked: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateTodo(
                    todo.copy(completed = checked)
                )
            } catch (e: Exception) {
                _event.send(TodoListEvent.Error("Error: ${e.message.toString()}"))
            }
        }
    }

    private fun onTodoDelete(todo: TodoModel) {
        viewModelScope.launch {
            try {
                deletedTodos.add(
                    todo.copy(id = 0)
                )
                repository.deleteTodo(todo)
                _event.send(TodoListEvent.TodoDeleted)
            } catch (e: Exception) {
                _event.send(TodoListEvent.Error("Error: ${e.message.toString()}"))
                deletedTodos.clear()
            }
        }
    }

    private fun onUndoDeletedTodo() {
        viewModelScope.launch {
            try {
                if (deletedTodos.isNotEmpty()) {
                    repository.insertTodos(deletedTodos)
                    deletedTodos.clear()
                }
            } catch (e: Exception) {
                _event.send(TodoListEvent.Error("Error: ${e.message.toString()}"))
                deletedTodos.clear()
            }
        }
    }

    private fun onDeletedTodosClear() {
        deletedTodos.clear()
    }

    private fun onSearchExpanded() {
        _uiState.update { it.copy(searchExpanded = true) }
    }

    private fun onSearchCollapsed() {
        _query.value = ""
        _uiState.update { it.copy(searchExpanded = false) }
    }

    private fun onMenuExpanded() {
        _uiState.update { it.copy(menuExpanded = true) }
    }

    private fun onMenuCollapsed() {
        _uiState.update { it.copy(menuExpanded = false) }
    }

    private fun onSortExpanded() {
        _uiState.update { it.copy(sortExpanded = true) }
    }

    private fun onSortCollapsed() {
        _uiState.update { it.copy(sortExpanded = false) }
    }

    private fun onSort(sort: TodoSort) {
        viewModelScope.launch {
            dataStoreRepository.upsertSorting(sort)
            onSortCollapsed()
        }
    }

    private fun onHideCompletedChange() {
        viewModelScope.launch {
            dataStoreRepository.upsertHideCompleted(!uiState.value.hideCompleted)
            onMenuCollapsed()
        }
    }

    private fun onDeleteCompletedShow() {
        _uiState.update { it.copy(deleteCompletedShown = true) }
        onMenuCollapsed()
    }

    private fun onDeleteCompletedDismiss() {
        _uiState.update { it.copy(deleteCompletedShown = false) }
    }

    private fun onDeleteCompleted() {
        viewModelScope.launch {
            try {
                repository.deleteAllCompletedTodo()
                onDeleteCompletedDismiss()
            } catch (e: Exception) {
                _event.send(TodoListEvent.Error("Error: ${e.message.toString()}"))
                onDeleteCompletedDismiss()
            }
        }
    }

    private fun onDeleteAllShow() {
        _uiState.update { it.copy(deleteAllShown = true) }
        onMenuCollapsed()
    }

    private fun onDeleteAllDismiss() {
        _uiState.update { it.copy(deleteAllShown = false) }
    }

    private fun onDeleteAll() {
        viewModelScope.launch {
            try {
                repository.deleteAllTodo()
                onDeleteAllDismiss()
            } catch (e: Exception) {
                _event.send(TodoListEvent.Error("Error: ${e.message.toString()}"))
                onDeleteAllDismiss()
            }
        }
    }

    sealed interface TodoListEvent {
        data object TodoDeleted : TodoListEvent
        data class Error(val errorMessage: String) : TodoListEvent
    }
}