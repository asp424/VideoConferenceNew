package com.example.videoconference.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.videoconference.ui.Chat
import com.example.videoconference.utilities.*
import com.example.videoconference.utilities.Constants.CHAT_ID
import com.lm.firebasechat.FirebaseChat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatActivity : ComponentActivity() {

    private val preferenceManager by lazy { PreferenceManager(applicationContext) }

    var firebaseChatInstance: FirebaseChat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.getStringExtra(CHAT_ID)?.also { him ->
            preferenceManager.getString(CHAT_ID)?.also { my ->
                firebaseChatInstance = firebaseChat.invoke(Pair(my, him))
                firebaseChatInstance?.apply {
                    list.value = UIStates.Loading
                    startListener(
                        onMessage = { list.value = UIStates.Success(it); setOnline() },
                        onOnline = { isOnline.value = it != "0" },
                        onWriting = { writing.value = it != "0" },
                        onNotify = {
                            if (it == "ring") lifecycleScope.launch {
                                notify.value = true
                                delay(3000)
                                notify.value = false
                                clearNotify()
                            }
                        }
                    )
                    setContent { Chat(this) }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        firebaseChatInstance?.setOnline()
    }

    override fun onPause() {
        super.onPause()
        firebaseChatInstance?.apply { setOffline(); setNoWriting(); stopListener(); clearNotifyMe() }
    }
}