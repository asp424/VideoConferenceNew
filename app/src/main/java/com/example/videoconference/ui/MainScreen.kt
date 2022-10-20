package com.example.videoconference.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.videoconference.R
import com.example.videoconference.activities.Main
import com.example.videoconference.models.MainViewModel
import com.example.videoconference.utilities.onMultipleUsersAction

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen(mainViewModel: MainViewModel, myChatId: String) {
    val visibleIcon = remember { mutableStateOf(false) }
    val activity = LocalContext.current as Main

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        Visibility(visible = visibleIcon.value) {
          Box(
                Modifier.padding(
                    end = 60.dp,
                    bottom = 60.dp
                )
            ) {
                androidx.compose.material.Icon(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.ass),
                    null, modifier = Modifier
                        .size(60.dp)
                        .clickable {
                            onMultipleUsersAction(context = activity)
                        }
                )
            }
        }
    }
    MainColumn(mainViewModel, visibleIcon, myChatId)
}
