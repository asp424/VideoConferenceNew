package com.example.videoconference.activities

import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.videoconference.models.MainViewModel
import com.example.videoconference.models.UserModel
import com.example.videoconference.ui.*
import com.example.videoconference.utilities.*
import com.example.videoconference.utilities.Constants.stateApp

var listChecked = mutableListOf<UserModel>()

class Main : AppCompatActivity() {

    private val preferenceManager by lazy { PreferenceManager(applicationContext) }

    private val mainViewModel: MainViewModel by viewModels()
    override fun onResume() {
        super.onResume()
        stateApp = 1
        sendFCMTokenToDatabase(applicationContext)
        preferenceManager.getString(Constants.CHAT_ID)?.also { myChatId ->
            setContent { MainScreen(mainViewModel, myChatId) }
        }
    }

    override fun onPause() {
        super.onPause()
        stateApp = 0
    }
}