package com.example.videoconference.activities

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.NotificationManagerCompat
import com.example.videoconference.ui.Chat
import com.example.videoconference.utilities.*
import com.example.videoconference.utilities.Constants.*
import com.lm.firebasechat.FirebaseChat
import kotlinx.coroutines.delay

class ChatActivity : ComponentActivity() {

    private val preferenceManager by lazy { PreferenceManager(applicationContext) }

    private val manager by lazy { NotificationManagerCompat.from(applicationContext) }

    private var firebaseChatInstance: FirebaseChat? = null

    override fun onResume() {
        super.onResume()
        manager.cancelAll()
        intent?.apply {
            getStringExtra(CHAT_ID)?.also { him ->
                getStringExtra(REMOTE_MSG_INVITER_TOKEN)?.also { token ->
                    getStringExtra(KEY_FIRST_NAME)?.also { name ->
                        preferenceManager.apply {
                            getString(CHAT_ID)?.also { my ->
                                firebaseChatInstance = firebaseChat.invoke(Pair(my, him))
                                    .setToken { token }.setMyName { getString(KEY_FIRST_NAME) }
                                firebaseChatInstance?.apply {
                                    list.value = UIStates.Loading
                                    startListener(
                                        onMessage = { list.value = UIStates.Success(it) },
                                        onOnline = { isOnline.value = it },
                                        onWriting = { writing.value = it },
                                        onNotify = { notify.value = it }
                                    )
                                    setContent { Chat(this, name, token) }
                                }
                            }
                        }
                    }
                }
            }
        }
        firebaseChatInstance?.setOnline()
    }

    override fun onPause() {
        super.onPause()
        firebaseChatInstance?.apply { setOffline(); setNoWriting(); stopListener() }
    }
}

