package com.example.videoconference.ui

import android.content.Intent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.videoconference.activities.ChatActivity
import com.example.videoconference.activities.Main
import com.example.videoconference.models.UserModel
import com.example.videoconference.utilities.Constants
import com.example.videoconference.utilities.initiateMeeting

@Composable
fun ChangePhoto(
    visible: Boolean,
    photoUrl: String,
    onClick: () -> Unit,
    user: UserModel,
    scaleVisible: Boolean,
    inRow: @Composable (RowScope) -> Unit
) {
    val activity = LocalContext.current as Main
val scale = animateFloatAsState(
    if (scaleVisible) 1f else 0f
)
    Row(
        Modifier.padding(10.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (photoUrl.isEmpty() && user.first_name.isNotEmpty()) {
            DrawCircle(user.first_name) { onClick() }
        } else SetImage(photoUrl) { onClick() }
        inRow(this)
        NameBlockMainList(
            name = user.first_name,
            lastName = user.last_name
        )
    }
    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxSize().scale(scale.value),
    verticalAlignment = Alignment.CenterVertically
        ) {
        Box(Modifier.padding(10.dp)) {
            Icon(
                Icons.Default.Message,
                contentDescription = null,
                Modifier
                    .size(25.dp)
                    .clickable {
                        if (user.chatId.isNotEmpty()) {
                            activity.startActivity(
                                Intent(
                                    activity,
                                    ChatActivity::class.java
                                ).apply { putExtra(Constants.CHAT_ID, user.chatId) })
                        }
                    }
            )
        }
        Box(Modifier.padding(10.dp)) {
            Icon(
                Icons.Default.Call, contentDescription = null,
                Modifier
                    .size(25.dp)
                    .clickable {
                        initiateMeeting(
                            user,
                            "audio",
                            activity.applicationContext
                        )
                    }
            )
        }
        Box(Modifier.padding(10.dp)) {
            Icon(
                Icons.Default.VideoCall, contentDescription = null,
                Modifier
                    .size(25.dp)
                    .clickable {
                        initiateMeeting(
                            user,
                            "video",
                            activity.applicationContext
                        )
                    }
            )
        }
    }
}