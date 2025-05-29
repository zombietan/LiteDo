package com.example.litedo.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.litedo.presentation.screen.todo.add.TodoAddRoute
import com.example.litedo.presentation.screen.todo.add.TodoAddScreen
import com.example.litedo.presentation.screen.todo.edit.TodoEditRoute
import com.example.litedo.presentation.screen.todo.edit.TodoEditScreen
import com.example.litedo.presentation.screen.todo.list.TodoListRoute
import com.example.litedo.presentation.screen.todo.list.TodoListScreen
import kotlin.reflect.KType

@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {

    NavHost(
        navController = navController,
        startDestination = TodoListRoute,
        modifier = modifier
    ) {
        composable<TodoListRoute> {
            TodoListScreen(
                navController = navController
            )
        }
        composable<TodoEditRoute>(
            typeMap = autoTypeMap<TodoEditRoute>()
        ) {
            TodoEditScreen(
                navController = navController
            )
        }
        composable<TodoAddRoute> {
            TodoAddScreen(
                navController = navController
            )
        }
    }

}

inline fun <reified T : Any> autoTypeMap(): Map<KType, NavType<*>> =
    when (T::class) {
        TodoEditRoute::class -> TodoEditRoute.typeMap
        else -> emptyMap()
    }