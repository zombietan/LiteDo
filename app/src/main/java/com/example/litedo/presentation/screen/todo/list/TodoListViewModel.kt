package com.example.litedo.presentation.screen.todo.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val repository: TodoRepository,
    private val dataStoreRepository: DataStoreRepository
): ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    var searchExpanded: Boolean by mutableStateOf(false)
        private set

    var menuExpanded: Boolean by mutableStateOf(false)
        private set

    var sortExpanded: Boolean by mutableStateOf(false)
        private set

    var hideCompleted: Boolean by mutableStateOf(false)
        private set

    var sorting: TodoSort? by mutableStateOf<TodoSort?>(null)
        private set

    var deleteCompletedShown: Boolean by mutableStateOf(false)
        private set

    var deleteAllShown: Boolean by mutableStateOf(false)
        private set

    @OptIn(ExperimentalCoroutinesApi::class)
    val todos: Flow<PagingData<TodoModel>> =
        combine(
            query,
            dataStoreRepository.getSorting,
            dataStoreRepository.getHideCompleted
        ) { query, sorting, hideCompleted ->
            Triple(query, sorting, hideCompleted)
        }.flatMapLatest { (query, sorting, hideCompleted) ->
            this@TodoListViewModel.hideCompleted = hideCompleted
            this@TodoListViewModel.sorting = sorting
            repository.getTodos(
                query = query,
                hideCompleted = hideCompleted,
                sorting = sorting
            ).cachedIn(viewModelScope)
        }

    private val _event = Channel<TodoListEvent>()
    val event: Flow<TodoListEvent> = _event.receiveAsFlow()

    private var deletedTodo: TodoModel? = null

    fun onQueryChange(value: String) {
        viewModelScope.launch {
            _query.emit(value)
        }
    }

    fun onTodoChecked(
        todo: TodoModel,
        checked: Boolean
    ) {
        viewModelScope.launch {
            repository.updateTodo(
                todo.copy(completed = checked)
            )
        }
    }

    fun onTodoDelete(todo: TodoModel) {
        viewModelScope.launch {
            deletedTodo = todo
            repository.deleteTodo(todo)
            _event.send(TodoListEvent.TodoDeleted)
        }
    }

    fun onUndoDeletedTodo() {
        viewModelScope.launch {
            deletedTodo?.let { todo ->
                repository.insertTodo(
                    todo.copy(id = 0)
                )
            }
            deletedTodo = null
        }
    }

    fun onSearchExpanded() {
        searchExpanded = true
    }

    fun onSearchCollapsed() {
        viewModelScope.launch {
            _query.emit("")
            searchExpanded = false
        }
    }

    fun onMenuExpanded() {
        menuExpanded = true
    }

    fun onMenuCollapsed() {
        menuExpanded = false
    }

    fun onSortExpanded() {
        sortExpanded = true
    }

    fun onSortCollapsed() {
        sortExpanded = false
    }

    fun onSort(sort: TodoSort) {
        viewModelScope.launch {
            dataStoreRepository.upsertSorting(sort)
            onSortCollapsed()
        }
    }

    fun onHideCompletedChange() {
        viewModelScope.launch {
            dataStoreRepository.upsertHideCompleted(!hideCompleted)
            onMenuCollapsed()
        }
    }

    fun onDeleteCompletedShow() {
        deleteCompletedShown = true
        onMenuCollapsed()
    }

    fun onDeleteCompletedDismiss() {
        deleteCompletedShown = false
    }

    fun onDeleteCompleted() {
        viewModelScope.launch {
            repository.deleteAllCompletedTodo()
            onDeleteCompletedDismiss()
        }
    }

    fun onDeleteAllShow() {
        deleteAllShown = true
        onMenuCollapsed()
    }

    fun onDeleteAllDismiss() {
        deleteAllShown = false
    }

    fun onDeleteAll() {
        viewModelScope.launch {
            repository.deleteAllTodo()
            onDeleteAllDismiss()
        }
    }


    enum class TodoListEvent {
        TodoDeleted,
    }
}