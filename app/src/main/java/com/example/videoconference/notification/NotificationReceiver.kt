package com.example.videoconference.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.videoconference.activities.StartJitsiMeetActivity
import com.example.videoconference.utilities.Constants
import com.example.videoconference.utilities.closeNotification
import com.example.videoconference.utilities.log
import com.example.videoconference.utilities.sendMessageToFMS

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let { contextNotNull ->
            when (intent?.getStringExtra("callAnswer")) {
                "cancel" -> {
                    intent.getStringExtra(
                        Constants.REMOTE_MSG_INVITATION_RESPONSE
                    )?.also { invitationResponse ->
                        sendMessageToFMS(
                            invitationResponse,
                            type = Constants.REMOTE_MSG_INVITATION_RESPONSE,
                            Constants.REMOTE_MSG_INVITATION_REJECTED
                        )
                        closeNotification(contextNotNull)
                        Constants.stopPlay()
                    }
                }
                "getCall" -> {
                    contextNotNull.startActivity(
                        Intent(context, StartJitsiMeetActivity::class.java).apply {
                            putExtra(
                                Constants.REMOTE_MSG_INVITER_TOKEN,
                                intent.getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN)
                            )
                            putExtra(
                                "meetingType", intent.getStringExtra("meetingType")
                            )
                            putExtra("meetingRoom",
                                intent.getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM)
                            )
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                }
                "close" -> closeNotification(contextNotNull)
                else -> Unit
            }
        }
    }
}