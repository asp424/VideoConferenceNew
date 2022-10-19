package com.example.videoconference.ui

import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.videoconference.activities.ChatActivity
import com.example.videoconference.activities.Main
import com.example.videoconference.activities.SettingsActivity
import com.example.videoconference.activities.listChecked
import com.example.videoconference.models.MainViewModel
import com.example.videoconference.utilities.Constants.CHAT_ID
import com.example.videoconference.utilities.getUser
import com.example.videoconference.utilities.getValuePref
import com.example.videoconference.utilities.initiateMeeting
import com.example.videoconference.utilities.signOut
import com.google.firebase.firestore.DocumentSnapshot

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainColumn(mainViewModel: MainViewModel, visibleIcon: MutableState<Boolean>) {
    val list by mainViewModel.listUsers.observeAsState()
    var heightCard by remember { mutableStateOf(0.dp) }
    var visibleCheck by remember { mutableStateOf(false) }
    val mapChecked by remember {
        mutableStateOf(hashMapOf<String, Boolean>())
    }
    val ass by animateDpAsState(if (visibleCheck) 50.dp else 0.dp)
    val activity = LocalContext.current as Main

    Column(
        Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        TopAppBar(
            backgroundColor = Color.Black,
            contentColor = Color.White,
            contentPadding = PaddingValues(start = 16.dp)
        ) { Text(text = "Абоненты", fontSize = 20.sp) }
        LazyColumn(
            content = {
                items(list as List<DocumentSnapshot>) { item ->
                    var check by remember { mutableStateOf(false) }
                    val user = remember { mutableStateOf(getUser(item = item)) }
                    if (mapChecked[item.id] == null) {
                        mapChecked[item.id] = false
                        check = mapChecked[item.id]!!
                    }
                    if (check) {
                        if (!listChecked.contains(user.value))
                            listChecked.add(user.value)
                    } else listChecked.remove(user.value)
                    visibleCheck = listChecked.size != 0
                    visibleIcon.value = listChecked.size > 1
                    user.value.apply {
                        if (user_id != getValuePref(activity.applicationContext, "user_id"))
                            CellCard(onLongClick = {
                                mapChecked.clear()
                                listChecked.clear()
                                visibleCheck = !visibleCheck
                                mapChecked[item.id] = !visibleIcon.value
                                check = !check
                            }, height = { heightCard = it }) {
                                ChangePhoto(
                                    user = user.value,
                                    photoUrl = avatar,
                                    onClick = {}, visible = visibleCheck,
                                    inRow = {
                                        Visibility(visible = visibleCheck) {
                                            Checkbox(
                                                checked = check,
                                                onCheckedChange = { _ ->
                                                    check = !check
                                                    mapChecked[item.id] = !mapChecked[item.id]!!

                                                }, modifier = Modifier.size(ass)
                                            )
                                        }

                                    }, scaleVisible = !visibleCheck
                                )
                            }
                    }
                }
            }, modifier = Modifier.padding(top = 15.dp)
        )
    }
    Dropdown(settings = {
        val intent = Intent(activity, SettingsActivity::class.java)
        activity.startActivity(intent)
        activity.finish()
    }, signOut = {
        signOut(activity.applicationContext, activity)
    })
}