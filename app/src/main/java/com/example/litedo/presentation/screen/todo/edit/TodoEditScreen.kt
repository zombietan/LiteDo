package com.example.litedo.presentation.screen.todo.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.litedo.R
import com.example.litedo.domain.model.TodoModel
import com.example.litedo.presentation.component.button.TodoFloatingActionButton
import com.example.litedo.presentation.component.misc.TodoConfigure
import com.example.litedo.presentation.component.text.TextPlain
import com.example.litedo.presentation.component.topbar.TopBar
import com.example.litedo.presentation.component.topbar.TopBarIconButton
import com.example.litedo.presentation.navigation.CustomNavType
import com.example.litedo.presentation.theme.LiteDoTheme
import com.example.litedo.presentation.util.ObserveEvent
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.reflect.typeOf
import kotlin.time.Clock

@Serializable
data class TodoEditRoute(
    val todo: TodoModel
) {
    companion object {
        val typeMap = mapOf(
            typeOf<TodoModel>() to CustomNavType.TodoModelType
        )
    }
}

@Composable
fun TodoEditScreen(
    snackbar: SnackbarHostState = remember { SnackbarHostState() },
    navController: NavHostController,
    viewmodel: TodoEditViewModel = hiltViewModel()
) {
    val resources = LocalResources.current
    val uiState by viewmodel.uiState.collectAsStateWithLifecycle()

    ObserveEvent(
        flow = viewmodel.event,
        onEvent = { event ->
            when (event) {
                is TodoEditViewModel.TodoEditEvent.InvalidInput -> {
                    snackbar.showSnackbar(resources.getString(event.messageResId))
                }

                TodoEditViewModel.TodoEditEvent.TodoUpdated -> {
                    navController.popBackStack()
                }

                is TodoEditViewModel.TodoEditEvent.Error -> {
                    snackbar.showSnackbar(event.errorMessage)
                }
            }
        }
    )

    TodoEditContent(
        snackbar = snackbar,
        onAction = viewmodel::onAction,
        onNavigateBack = {
            navController.navigateUp()
        },
        uiState = uiState
    )

}

@Composable
private fun TodoEditContent(
    snackbar: SnackbarHostState,
    onAction: (TodoEditAction) -> Unit,
    onNavigateBack: () -> Unit,
    uiState: TodoEditUiState
) {
    Scaffold(
        topBar = {
            TopBar(
                title = {
                    TextPlain(
                        id = R.string.edit_task
                    )
                },
                navigationIcon = {
                    TopBarIconButton(
                        onClick = onNavigateBack,
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = R.string.cd_back_button
                    )
                }
            )
        },
        floatingActionButton = {
            TodoFloatingActionButton(
                onClick = {
                    onAction(TodoEditAction.UpdateTodo)
                },
                icon = Icons.Default.Done,
                contentDescription = R.string.cd_done_button
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TodoConfigure(
                name = uiState.name,
                onNameChange = {
                    onAction(TodoEditAction.NameChange(name = it))
                },
                important = uiState.important,
                onImportantChange = {
                    onAction(TodoEditAction.ImportantChange(important = it))
                }
            )
            Spacer(
                modifier = Modifier.height(10.dp)
            )
            Text(
                text = stringResource(id = R.string.your_created_timestamp, uiState.timestamp),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}

@Preview
@Composable
private fun TodoEditContentPreview() {
    LiteDoTheme {
        val formatter = DateTimeFormatter.ofPattern("yyyy, MMMM d, HH:mm", Locale.getDefault())
        TodoEditContent(
            snackbar = remember { SnackbarHostState() },
            onAction = { _ -> },
            onNavigateBack = {},
            uiState = TodoEditUiState(
                timestamp = Clock.System.now()
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .toJavaLocalDateTime().format(formatter)
            )
        )
    }
}