package com.example.videoconference.firebase

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.videoconference.R
import com.example.videoconference.activities.IncomingInvitationActivity
import com.example.videoconference.activities.Main
import com.example.videoconference.notification.NotificationReceiver
import com.example.videoconference.notification.createMessageChannel
import com.example.videoconference.notification.createNotification
import com.example.videoconference.utilities.Constants
import com.example.videoconference.utilities.Constants.MESSAGE
import com.example.videoconference.utilities.sendMessageToFMS
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lm.firebasechat.FirebaseMessageServiceChatCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FBMessageService : FirebaseMessagingService() {

    private val notificationManager
            by lazy { NotificationManagerCompat.from(this) }

    private val activityManager
            by lazy { getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager }

    private val notificationBuilder
            by lazy { NotificationCompat.Builder(this, resources.getString(R.string.id)) }

    private val pendingIntent by lazy {
        PendingIntent.getActivity(
            this, 0, Intent(this, Main::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK },
                    PendingIntent.FLAG_IMMUTABLE
        )
    }

    private val firebaseMessageServiceChatCallback by lazy {
        FirebaseMessageServiceChatCallback()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        registerReceiver(NotificationReceiver(),
            IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE))
        val type = remoteMessage.data[Constants.REMOTE_MSG_TYPE]
        if (type != null) {
            when(type){
                "answer" -> {
                    LocalBroadcastManager.getInstance(applicationContext)
                        .sendBroadcast(Intent(Constants.REMOTE_MSG_INVITATION_RESPONSE)
                           .putExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE, "тыква"))
                }
                Constants.REMOTE_MSG_INVITATION -> {
                    if (Constants.stateApp == 1) {
                        startActivity(Intent(applicationContext, IncomingInvitationActivity::class.java)
                            .putExtra(
                                Constants.REMOTE_MSG_MEETING_TYPE,
                                remoteMessage.data[Constants.REMOTE_MSG_MEETING_TYPE])
                            .putExtra(
                                Constants.KEY_FIRST_NAME,
                                remoteMessage.data[Constants.KEY_FIRST_NAME])
                            .putExtra(
                                Constants.KEY_LAST_NAME,
                                remoteMessage.data[Constants.KEY_LAST_NAME])
                            .putExtra(
                                Constants.KEY_EMAIL,
                                remoteMessage.data[Constants.KEY_EMAIL])
                            .putExtra(
                                Constants.REMOTE_MSG_INVITER_TOKEN,
                                remoteMessage.data[Constants.REMOTE_MSG_INVITER_TOKEN])
                            .putExtra(
                                Constants.REMOTE_MSG_MEETING_ROOM,
                                remoteMessage.data[Constants.REMOTE_MSG_MEETING_ROOM])
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    } else {
                        showNotification(remoteMessage)
                    }
                    Constants.startPlay(this, "in")
                }
                Constants.REMOTE_MSG_INVITATION_RESPONSE -> {
                    Constants.stopPlay()
                    if ( remoteMessage.data[Constants.REMOTE_MSG_INVITATION_RESPONSE]
                        == Constants.REMOTE_MSG_INVITATION_CANCELLED)
                            sendBroadcast(Intent(this,
                                NotificationReceiver::class.java)
                            .putExtra("callAnswer", "close"))
                    LocalBroadcastManager.getInstance(applicationContext)
                        .sendBroadcast(Intent(Constants.REMOTE_MSG_INVITATION_RESPONSE)
                        .putExtra(
                            Constants.REMOTE_MSG_INVITATION_RESPONSE,
                            remoteMessage.data[Constants.REMOTE_MSG_INVITATION_RESPONSE]
                        ))
                }
            }
        }
        val textMessage = remoteMessage.data[MESSAGE]?:""
        val name = remoteMessage.data["name"]?:""
        if (!isRun() && textMessage.isNotEmpty()) {
            showNotificationFromMessenger(textMessage, name)
           firebaseMessageServiceChatCallback.sendCallBack(remoteMessage)
        }
    }

    private fun isRun(): Boolean {
        val runningProcesses = activityManager.runningAppProcesses ?: return false
        for (i in runningProcesses) {
            if (i.importance ==
                ActivityManager.RunningAppProcessInfo
                    .IMPORTANCE_FOREGROUND && i.processName == packageName) return true
        }
        return false
    }
    private fun showNotificationFromMessenger(message: String, name: String) {
        notificationManager.createNotificationChannel(
            NotificationChannel("1", "ass", NotificationManager.IMPORTANCE_DEFAULT)
        )

        notificationManager.notify(
            1, notificationBuilder
                .setContentTitle(name)
                .setContentText(message)
                .setSmallIcon(R.drawable.anonymous_a)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .build()
        )
    }

    @SuppressLint("UnspecifiedImmutableFlag", "RemoteViewLayout")
    private fun showNotification(remoteMessage: RemoteMessage) =
        CoroutineScope(Dispatchers.IO).launch {
            sendMessageToFMS(
                type = "answer",
                token = remoteMessage.data[Constants.REMOTE_MSG_INVITER_TOKEN]!!,
                callAnswer = "тыква"
            )
            createMessageChannel(this@FBMessageService)
            createNotification(this@FBMessageService, remoteMessage)
        }
}

