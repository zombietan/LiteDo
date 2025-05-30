package com.example.litedo.presentation.screen.todo.add

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.litedo.R
import com.example.litedo.presentation.component.button.TodoFloatingActionButton
import com.example.litedo.presentation.component.misc.TodoConfigure
import com.example.litedo.presentation.component.text.TextPlain
import com.example.litedo.presentation.component.topbar.TopBar
import com.example.litedo.presentation.component.topbar.TopBarIconButton
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
    val context = LocalContext.current

    ObserveEvent(
        flow = viewModel.event,
        onEvent = { event ->
            when (event) {
                is TodoAddViewModel.TodoAddEvent.InvalidInput -> {
                    snackbar.showSnackbar(context.getString(event.messageResId))
                }

                TodoAddViewModel.TodoAddEvent.TodoAdded -> {
                    navController.popBackStack()
                }
            }
        }
    )

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
                        onClick = { navController.navigateUp() },
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = R.string.cd_back_button
                    )
                }
            )
        },
        floatingActionButton = {
            TodoFloatingActionButton(
                onClick = viewModel::onSaveTodo,
                icon = Icons.Default.Done,
                contentDescription = R.string.cd_done_button
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TodoConfigure(
                name = viewModel.name,
                onNameChange = viewModel::onNameChange,
                important = viewModel.important,
                onImportantChange = viewModel::onImportantChange
            )

        }
    }
}