package com.example.litedo.presentation.screen.todo.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.litedo.R
import com.example.litedo.domain.model.TodoModel
import com.example.litedo.domain.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TodoAddUiState(
    val name: String = "",
    val important: Boolean = false
)

sealed interface TodoAddAction {
    data class NameChange(val name: String) : TodoAddAction
    data class ImportantChange(val important: Boolean) : TodoAddAction
    data object SaveTodo : TodoAddAction
}

@HiltViewModel
class TodoAddViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodoAddUiState())
    val uiState: StateFlow<TodoAddUiState> = _uiState.asStateFlow()

    private val _event = Channel<TodoAddEvent>()
    val event: Flow<TodoAddEvent> = _event.receiveAsFlow()

    fun onAction(action: TodoAddAction) {
        when (action) {
            is TodoAddAction.ImportantChange -> {
                onImportantChange(action.important)
            }

            is TodoAddAction.NameChange -> {
                onNameChange(action.name)
            }

            TodoAddAction.SaveTodo -> {
                onSaveTodo()
            }
        }
    }


    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    fun onImportantChange(value: Boolean) {
        _uiState.update { it.copy(important = value) }
    }

    fun onSaveTodo() {
        viewModelScope.launch {
            if (uiState.value.name.isBlank()) {
                _event.send(TodoAddEvent.InvalidInput(R.string.error_name))
                return@launch
            }
            try {
                repository.insertTodo(
                    TodoModel(
                        name = uiState.value.name,
                        important = uiState.value.important
                    )
                )
                _event.send(TodoAddEvent.TodoAdded)
            } catch (e: Exception) {
                _event.send(TodoAddEvent.Error("Error: ${e.message.toString()}"))
            }
        }
    }


    sealed interface TodoAddEvent {
        data class InvalidInput(val messageResId: Int) : TodoAddEvent
        data class Error(val errorMessage: String) : TodoAddEvent
        data object TodoAdded : TodoAddEvent
    }
}