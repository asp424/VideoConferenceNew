package com.example.videoconference.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.videoconference.utilities.Constants
import com.example.videoconference.utilities.closeNotification
import com.example.videoconference.utilities.log
import com.example.videoconference.utilities.sendInvitationResponse

class StartJitsiMeetActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sendInvitationResponse(
            Constants.REMOTE_MSG_INVITATION_ACCEPTED, intent, this
        )
        closeNotification(this)
    }
}