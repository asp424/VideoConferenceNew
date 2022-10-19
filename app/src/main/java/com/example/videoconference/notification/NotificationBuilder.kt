package com.example.videoconference.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.videoconference.R
import com.example.videoconference.utilities.Constants
import com.example.videoconference.utilities.Constants.REMOTE_MSG_INVITER_TOKEN
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@RequiresApi(Build.VERSION_CODES.O)
 fun createMessageChannel(context: Context) {
    val name = context.getString(R.string.not)
    val descriptionText = "null"
    val importance = NotificationManager.IMPORTANCE_HIGH
    val soundBuilder =
        AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build()
    val sound =
        Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.a);
    val channel = NotificationChannel(
        context.resources.getString(R.string.default_notification_channel_id),
        name,
        importance
    ).apply {
        description = descriptionText
        setSound(sound, soundBuilder)
        lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        vibrationPattern = longArrayOf(200, 200, 100, 100, 50, 100, 300, 500)
    }
    val notificationManager: NotificationManager =
        context.getSystemService(FirebaseMessagingService.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
 }

@SuppressLint("UnspecifiedImmutableFlag")
fun createNotification(context: Context, remoteMessage: RemoteMessage){
    val getCallIntent = Intent(context, NotificationReceiver::class.java)
        .putExtra(
            REMOTE_MSG_INVITER_TOKEN,
            remoteMessage.data[REMOTE_MSG_INVITER_TOKEN])
        .putExtra("callAnswer", "getCall")
        .putExtra("meetingType", remoteMessage.
        data[Constants.REMOTE_MSG_MEETING_TYPE]
        )
        .putExtra("meetingRoom", remoteMessage
            .data[Constants.REMOTE_MSG_MEETING_ROOM])
    val getCallPendingIntent = PendingIntent.getBroadcast(
        context, 0,
        getCallIntent, PendingIntent.FLAG_CANCEL_CURRENT
    )
    val cancelIntent = Intent(context,
        NotificationReceiver::class.java)
        .putExtra(
            Constants.REMOTE_MSG_INVITATION_RESPONSE,
            remoteMessage.data[Constants.REMOTE_MSG_INVITER_TOKEN])
        .putExtra("callAnswer", "cancel")

    val cancelPendingIntent = PendingIntent.getBroadcast(
        context, 1,
        cancelIntent, PendingIntent.FLAG_CANCEL_CURRENT
    )
    if (remoteMessage.data["avatar"] != "null" &&
        !remoteMessage.data["avatar"].isNullOrEmpty()
    ) {
        val futureTarget = Glide.with(context)
            .asBitmap()
            .load(remoteMessage.data["avatar"].toString().toUri())
            .circleCrop()
            .submit()
        val person = Person.Builder()
            .setName(
                remoteMessage.data[Constants.KEY_FIRST_NAME] +
                        " " + remoteMessage.data[Constants.KEY_LAST_NAME]
            )
            .setKey("Входящий звонок")
            .setIcon(IconCompat.createWithBitmap(futureTarget.get())).build()
        val builder = NotificationCompat
            .Builder(
                context, context.resources.getString(
                    R.string.default_notification_channel_id
                )
            )
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.anonymous_a)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .addAction(1, "Oтклонить", cancelPendingIntent)
            .addAction(1, "Ответить", getCallPendingIntent)
            .setStyle(
                NotificationCompat.MessagingStyle(person)
                    .setConversationTitle(person.key)
                    .addMessage("Вам звонок", Calendar.getInstance().time.time, person)
            )
        Glide.with(context).clear(futureTarget)
        with(NotificationManagerCompat.from(context)) {
            notify(R.string.default_notification_channel_id, builder.build())
        }
    } else {
        val builder = NotificationCompat
            .Builder(
                context, context.resources.getString(
                    R.string.default_notification_channel_id
                )
            )
            .setAutoCancel(true)
            .setContentTitle(
                remoteMessage.data[Constants.KEY_FIRST_NAME] +
                        " " + remoteMessage.data[Constants.KEY_LAST_NAME]
            )
            .setContentText("Вам звонок")
            .setSmallIcon(R.drawable.anonymous_a)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .addAction(1, "Отклонить", cancelPendingIntent)
            .addAction(1, "Ответить", getCallPendingIntent)
        with(NotificationManagerCompat.from(context)) {
            notify(R.string.default_notification_channel_id, builder.build())
        }
    }
}