package com.example.litedo.presentation.component.card

import androidx.annotation.FloatRange
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.litedo.R
import com.example.litedo.domain.model.TodoModel
import com.example.litedo.presentation.theme.Black
import com.example.litedo.presentation.theme.LiteDoTheme
import com.example.litedo.presentation.theme.White
import kotlinx.coroutines.delay


@Composable
fun CardTodo(
    todo: TodoModel,
    onClick: (TodoModel) -> Unit,
    onCheckedChange: (TodoModel, Boolean) -> Unit,
    onDismiss: (TodoModel) -> Unit
) {
    SwipeToDelete(
        model = todo,
        onDelete = onDismiss,
        duration = 700,
        deleteIcon = Icons.Rounded.Delete,
        enableDeleteFromStartToEnd = true,
        enableDeleteFromEndToStart = true,
        positionalThreshold = 0.35F,
        inactiveBackgroundColor = Color.LightGray,
        activeBackgroundColor = MaterialTheme.colorScheme.error,
        inactiveIconTint = Black,
        activeIconTint = White,
    ) {
        Card(
            onClick = { onClick(todo) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 0.dp),
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.background
            ),
            shape = RectangleShape
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
            ) {
                Checkbox(
                    checked = todo.completed,
                    onCheckedChange = { checked ->
                        onCheckedChange(todo, checked)
                    }
                )
                Text(
                    text = todo.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    textDecoration = if (todo.completed)
                        TextDecoration.LineThrough
                    else
                        TextDecoration.None,
                    modifier = Modifier.weight(1F)
                )
                if (todo.important) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_warn),
                        contentDescription = stringResource(id = R.string.cd_task_important),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}


@Composable
fun <T> SwipeToDelete(
    model: T,
    onDelete: (T) -> Unit,
    duration: Int,
    deleteIcon: ImageVector,
    enableDeleteFromStartToEnd: Boolean,
    enableDeleteFromEndToStart: Boolean,
    @FloatRange(from = 0.0, to = 1.0) positionalThreshold: Float = 0.7F,
    inactiveBackgroundColor: Color,
    activeBackgroundColor: Color,
    inactiveIconTint: Color,
    activeIconTint: Color,
    content: @Composable (T) -> Unit
) {
    var deleted by rememberSaveable { mutableStateOf(false) }
    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (
                (enableDeleteFromStartToEnd && value == SwipeToDismissBoxValue.StartToEnd) ||
                (enableDeleteFromEndToStart && value == SwipeToDismissBoxValue.EndToStart)
            ) {
                deleted = true
                return@rememberSwipeToDismissBoxState true
            } else {
                return@rememberSwipeToDismissBoxState false
            }
        },
        positionalThreshold = { distance ->
            distance * positionalThreshold
        }
    )

    LaunchedEffect(key1 = deleted) {
        if (deleted) {
            delay(duration.toLong())
            onDelete(model)
        }
    }

    AnimatedVisibility(
        visible = !deleted,
        exit = shrinkVertically(
            animationSpec = tween(
                durationMillis = duration
            ),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        SwipeToDismissBox(
            state = state,
            enableDismissFromStartToEnd = enableDeleteFromStartToEnd,
            enableDismissFromEndToStart = enableDeleteFromEndToStart,
            backgroundContent = {
                if (
                    (enableDeleteFromStartToEnd && state.dismissDirection == SwipeToDismissBoxValue.StartToEnd) ||
                    (enableDeleteFromEndToStart && state.dismissDirection == SwipeToDismissBoxValue.EndToStart)
                ) {
                    val alignment = when (state.dismissDirection) {
                        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                        SwipeToDismissBoxValue.Settled -> return@SwipeToDismissBox
                    }
                    val backgroundColor by animateColorAsState(
                        targetValue = if (state.targetValue == SwipeToDismissBoxValue.Settled) inactiveBackgroundColor else activeBackgroundColor,
                        label = "backgroundColor"
                    )
                    val tint by animateColorAsState(
                        targetValue = if (state.targetValue == SwipeToDismissBoxValue.Settled) inactiveIconTint else activeIconTint,
                        label = "tint"
                    )
                    val scale by animateFloatAsState(
                        targetValue = if (state.targetValue == SwipeToDismissBoxValue.Settled) 0.75F else 1F,
                        label = "scale"
                    )

                    Box(
                        contentAlignment = alignment,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(backgroundColor)
                            .padding(horizontal = 20.dp)
                    ) {
                        Icon(
                            imageVector = deleteIcon,
                            contentDescription = null,
                            tint = tint,
                            modifier = Modifier.graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                        )
                    }
                }
            },
            content = {
                content(model)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CardTodoPreview() {
    LiteDoTheme {
        LazyColumn {
            item(2) {
                CardTodo(
                    todo = TodoModel(
                        name = "today task",
                        important = true,
                        completed = true
                    ),
                    onClick = {},
                    onCheckedChange = { _, _ -> },
                    onDismiss = {}
                )
                CardTodo(
                    todo = TodoModel(
                        name = "next task",
                        important = false,
                        completed = false
                    ),
                    onClick = {},
                    onCheckedChange = { _, _ -> },
                    onDismiss = {}
                )
            }
        }
    }
}