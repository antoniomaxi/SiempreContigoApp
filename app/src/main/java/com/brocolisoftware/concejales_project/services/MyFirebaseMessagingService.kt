package com.brocolisoftware.concejales_project.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.brocolisoftware.concejales_project.R
import com.brocolisoftware.concejales_project.activities.LatestMessageActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.karn.notify.Notify
import org.json.JSONObject
import java.util.*
import android.content.Context

class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)

            // Check if message contains a notification payload.
            remoteMessage.notification?.let {
                Log.d(TAG, "Message Notification Body: ${it.body}")
                val json = JSONObject(it.body.toString())
                sendNotification(json, it.title.toString())
            }
        }
    }


    private fun sendNotification(messageBody: JSONObject, title: String) {

/*

        var user = messageBody.getJSONObject("user")
        var message = messageBody.getJSONObject("message")

        Notify
            .with(this)
            .meta {
                icon|
            }
            .asMessage {
                userDisplayName = "Usuario"
                conversationTitle = title
                messages = Arrays.asList(
                    NotificationCompat.MessagingStyle.Message(messageBody.getJSONObject("message").getString("text"),
                        System.currentTimeMillis() - message.getLong("timestamp"), // 6 Mins ago
                        user.getString("nombre") + " " + user.getString("apellido"))
                )
                conversationTitle = "Siempre Contigo"
            }
            .show()
*/

        val user = messageBody.getJSONObject("user")
        val message = messageBody.getJSONObject("message")

        val intent = Intent(this, LatestMessageActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)
            .setSound(defaultSoundUri)

            .setStyle(
                NotificationCompat.MessagingStyle("ME")
                    .addMessage(
                        NotificationCompat.MessagingStyle.Message(
                            messageBody.getJSONObject("message").getString("text"),
                            System.currentTimeMillis() - message.getLong("timestamp"), // 6 Mins ago
                            user.getString("nombre") + " " + user.getString("apellido")
                        )
                    ).setConversationTitle("Emergencias")
            )
            .setContentIntent(pendingIntent)
            .setContentTitle(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Messages",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Messages"
            channel.name = "Notificacion Mensaje"
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, builder.build())
    }

    companion object {

        private const val TAG = "MyFirebaseMsgService"
    }
}