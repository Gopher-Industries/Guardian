package deakin.gopher.guardian.model

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import deakin.gopher.guardian.R
import deakin.gopher.guardian.util.Util
import deakin.gopher.guardian.view.patient.associateradar.ActivitySuggestionActivity

class FirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(s: String) {
        val user = "user"
        Util.updateDeviceToken(this, s, user)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // TODO Test integration with Firebase and replace test data with data from remoteMessage
        val title = "Guardians Alert"
        val message =
            "Behaviour Anomalies on patient X - Radar device detected anomalies on patient X"
        createNotification(title, message)
    }

    private fun createNotification(
        title: String,
        message: String,
    ) {
        val notification = buildNotification(title, message)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(
        title: String,
        message: String,
    ): Notification {
        val intent = Intent(applicationContext, ActivitySuggestionActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE,
            )
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val color: Int =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                resources.getColor(R.color.black, theme)
            } else {
                ContextCompat.getColor(this, R.color.black)
            }

        return NotificationCompat.Builder(this, Util.CHANNEL_ID)
            .setSmallIcon(R.drawable.notifcations).setColor(color).setContentTitle(title)
            .setAutoCancel(true).setSound(defaultSoundUri).setContentIntent(pendingIntent)
            .setContentText(message).build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel =
            NotificationChannel(
                Util.CHANNEL_ID,
                Util.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = Util.CHANNEL_DESCRIPTION
            }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val NOTIFICATION_ID = 999
    }
}
