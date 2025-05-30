package com.example.litedo.presentation.screen.todo.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.litedo.R
import com.example.litedo.data.repository.TodoRepository
import com.example.litedo.injection.annotation.TimeFormatter
import com.example.litedo.presentation.navigation.autoTypeMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class TodoEditViewModel @Inject constructor(
    private val repository: TodoRepository,
    @TimeFormatter private val formatter: DateTimeFormatter,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val navArg = savedStateHandle.toRoute<TodoEditRoute>(
        typeMap = autoTypeMap<TodoEditRoute>()
    )

    var name: String by mutableStateOf(navArg.todo.name)
        private set

    var important: Boolean by mutableStateOf(navArg.todo.important)
        private set

    val timestamp: String by mutableStateOf(formatter.format(navArg.todo.timestamp))

    private val _event = Channel<TodoEditEvent>()
    val event: Flow<TodoEditEvent> = _event.receiveAsFlow()


    fun onNameChange(value: String) {
        name = value
    }

    fun onImportantChange(value: Boolean) {
        important = value
    }

    fun onUpdateTodo() {
        viewModelScope.launch {
            if (name.isBlank()) {
                _event.send(TodoEditEvent.InvalidInput(R.string.error_name))
                return@launch
            }

            repository.updateTodo(
                navArg.todo.copy(
                    name = name,
                    important = important
                )
            )

            _event.send(TodoEditEvent.TodoUpdated)
        }
    }


    sealed interface TodoEditEvent {
        data class InvalidInput(val messageResId: Int) : TodoEditEvent
        data object TodoUpdated : TodoEditEvent
    }
}