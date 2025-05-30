package com.example.litedo.presentation.screen.todo.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.litedo.R
import com.example.litedo.data.model.TodoModel
import com.example.litedo.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoAddViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {

    var name: String by mutableStateOf("")
        private set

    var important: Boolean by mutableStateOf(false)
        private set

    private val _event = Channel<TodoAddEvent>()
    val event: Flow<TodoAddEvent> = _event.receiveAsFlow()


    fun onNameChange(value: String) {
        name = value
    }

    fun onImportantChange(value: Boolean) {
        important = value
    }

    fun onSaveTodo() {
        viewModelScope.launch {
            if (name.isBlank()) {
                _event.send(TodoAddEvent.InvalidInput(R.string.error_name))
                return@launch
            }
            repository.insertTodo(
                TodoModel(
                    name = name,
                    important = important
                )
            )
            _event.send(TodoAddEvent.TodoAdded)
        }
    }


    sealed interface TodoAddEvent {
        data class InvalidInput(val messageResId: Int) : TodoAddEvent
        data object TodoAdded : TodoAddEvent
    }
}