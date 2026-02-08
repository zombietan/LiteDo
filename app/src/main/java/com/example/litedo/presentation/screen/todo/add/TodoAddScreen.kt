package com.example.litedo.presentation.screen.todo.add

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.litedo.R
import com.example.litedo.presentation.component.button.TodoFloatingActionButton
import com.example.litedo.presentation.component.misc.TodoConfigure
import com.example.litedo.presentation.component.text.TextPlain
import com.example.litedo.presentation.component.topbar.TopBar
import com.example.litedo.presentation.component.topbar.TopBarIconButton
import com.example.litedo.presentation.theme.LiteDoTheme
import com.example.litedo.presentation.util.ObserveEvent
import kotlinx.serialization.Serializable

@Serializable
data object TodoAddRoute

@Composable
fun TodoAddScreen(
    snackbar: SnackbarHostState = remember { SnackbarHostState() },
    navController: NavHostController,
    viewModel: TodoAddViewModel = hiltViewModel()
) {
    val resources = LocalResources.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveEvent(
        flow = viewModel.event,
        onEvent = { event ->
            when (event) {
                is TodoAddViewModel.TodoAddEvent.InvalidInput -> {
                    snackbar.showSnackbar(resources.getString(event.messageResId))
                }

                TodoAddViewModel.TodoAddEvent.TodoAdded -> {
                    navController.popBackStack()
                }

                is TodoAddViewModel.TodoAddEvent.Error -> {
                    snackbar.showSnackbar(event.errorMessage)
                }
            }
        }
    )

    TodoAddContent(
        snackbar = snackbar,
        onAction = viewModel::onAction,
        onNavigateBack = {
            navController.navigateUp()
        },
        uiState = uiState
    )
}

@Composable
private fun TodoAddContent(
    snackbar: SnackbarHostState,
    onAction: (TodoAddAction) -> Unit,
    onNavigateBack: () -> Unit,
    uiState: TodoAddUiState
) {
    Scaffold(
        topBar = {
            TopBar(
                title = {
                    TextPlain(
                        id = R.string.new_task
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
                    onAction(TodoAddAction.SaveTodo)
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TodoConfigure(
                name = uiState.name,
                onNameChange = {
                    onAction(TodoAddAction.NameChange(name = it))
                },
                important = uiState.important,
                onImportantChange = {
                    onAction(TodoAddAction.ImportantChange(important = it))
                }
            )

        }
    }
}

@Preview
@Composable
private fun TodoAddContentPreview() {
    LiteDoTheme {
        TodoAddContent(
            snackbar = remember { SnackbarHostState() },
            onAction = { _ -> },
            onNavigateBack = {},
            uiState = TodoAddUiState()
        )
    }
}