package com.example.litedo.presentation.screen.todo.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.litedo.R
import com.example.litedo.domain.repository.TodoRepository
import com.example.litedo.injection.annotation.TimeFormatter
import com.example.litedo.presentation.navigation.autoTypeMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

sealed interface EditSaveState {
    data object Idle: EditSaveState
    data object Saving: EditSaveState
}

data class TodoEditUiState(
    val name: String = "",
    val important: Boolean = false,
    val timestamp: String = "",
    val saveState: EditSaveState = EditSaveState.Idle
)

sealed interface TodoEditAction {
    data class NameChange(val name: String) : TodoEditAction
    data class ImportantChange(val important: Boolean) : TodoEditAction
    data object UpdateTodo : TodoEditAction
    data object SetIdle : TodoEditAction
}

@HiltViewModel
class TodoEditViewModel @Inject constructor(
    private val repository: TodoRepository,
    @param:TimeFormatter private val formatter: DateTimeFormatter,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val navArg = savedStateHandle.toRoute<TodoEditRoute>(
        typeMap = autoTypeMap<TodoEditRoute>()
    )

    private val _uiState = MutableStateFlow(TodoEditUiState())
    val uiState: StateFlow<TodoEditUiState> = _uiState.asStateFlow()

    private var isSaving = false

    init {
        _uiState.update {
            it.copy(
                name = navArg.todo.name,
                important = navArg.todo.important,
                timestamp = navArg.todo.timestamp.toJavaLocalDateTime().format(formatter)
            )
        }
    }

    private val _event = Channel<TodoEditEvent>()
    val event: Flow<TodoEditEvent> = _event.receiveAsFlow()

    fun onAction(action: TodoEditAction) {
        when (action) {
            is TodoEditAction.ImportantChange -> {
                onImportantChange(action.important)
            }

            is TodoEditAction.NameChange -> {
                onNameChange(action.name)
            }

            TodoEditAction.UpdateTodo -> {
                onUpdateTodo()
            }

            TodoEditAction.SetIdle -> {
                onSetIdle()
            }
        }
    }

    private fun onSetIdle() {
        _uiState.update { it.copy(saveState = EditSaveState.Idle) }
    }

    private fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    private fun onImportantChange(value: Boolean) {
        _uiState.update { it.copy(important = value) }
    }

    private fun onUpdateTodo() {
        if (isSaving) return
        _uiState.update { it.copy(saveState = EditSaveState.Saving) }

        viewModelScope.launch {
            if (uiState.value.name.isBlank()) {
                _event.send(TodoEditEvent.InvalidInput(R.string.error_name))
                return@launch
            }

            isSaving = true

            try {
                repository.updateTodo(
                    navArg.todo.copy(
                        name = uiState.value.name,
                        important = uiState.value.important
                    )
                )

                _event.send(TodoEditEvent.TodoUpdated)
            } catch (e: Exception) {
                _event.send(TodoEditEvent.Error("Error: ${e.message.toString()}"))
            } finally {
                isSaving = false
            }
        }
    }


    sealed interface TodoEditEvent {
        data class InvalidInput(val messageResId: Int) : TodoEditEvent
        data class Error(val errorMessage: String) : TodoEditEvent
        data object TodoUpdated : TodoEditEvent
    }
}