package com.example.videoconference.ui



import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CellCard(
    onLongClick: () -> Unit, height: (Dp) -> Unit, inCard: @Composable () -> Unit) {
    val haptic = LocalHapticFeedback.current
    val localDens = LocalDensity.current
    Card(shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongClick()
                    },
                    onDoubleTap = {
                    })
            }
            .onGloballyPositioned {
                height(it.size.height.dp / localDens.density)
            }
            .padding(start = 16.dp, end = 16.dp, top = 6.dp),
        elevation = 10.dp,
        border = BorderStroke(
            width = 1.dp,
            color = Color.Black
        )) {
        inCard()
    }
}

@Composable
fun Dropdown(settings: () -> Unit, signOut: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopEnd)
            .padding(top = 3.dp, end = 8.dp)
    ) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                Icons.Default.MoreVert,
                tint = Color.White,
                contentDescription = null
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(onClick = { settings() }) {
                Text("Настройки")
            }
            Divider()
            DropdownMenuItem(onClick = { signOut() }) {
                Text("SignOut")
            }
        }
    }

}

