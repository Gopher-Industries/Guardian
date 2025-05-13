package deakin.gopher.guardian.model

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService // Import the correct base class
import com.google.firebase.messaging.RemoteMessage // Import Firebase's RemoteMessage class
import deakin.gopher.guardian.R
import deakin.gopher.guardian.util.Util
import deakin.gopher.guardian.view.patient.associateradar.ActivitySuggestionActivity

// Corrected inheritance: Extend Firebase's FirebaseMessagingService
class FirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {

    // Override the onNewToken function
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        // You might want to send this token to your app server.
        // Currently, it's using a hardcoded user. Consider how you want to handle this
        // for different users or scenarios.
        val user = "user" // Replace with dynamic user identification if applicable
        Util.updateDeviceToken(this, token, user)
    }

    // Override the onMessageReceived function
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            // Extract title and message from the data payload.
            // Adjust these keys based on how your server sends the data.
            val title = remoteMessage.data["title"] ?: "Guardians Alert" // Default title
            val message = remoteMessage.data["message"] ?: "You have a new message." // Default message

            // Process the message data.
            // You might need to parse the data to get patient information, anomaly details, etc.
            // Example: val patientId = remoteMessage.data["patientId"]
            // Example: val anomalyType = remoteMessage.data["anomalyType"]

            // Create and display the notification.
            createNotification(title, message)
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            // If the app is in the foreground, onMessageReceived is called,
            // and you handle the notification here.
            // If the app is in the background or killed, the system handles the notification,
            // and it appears in the notification tray.
            // If you want to handle background notifications within your app,
            // you might need to use a custom handler or worker.

            // You might choose to display the notification even if it has a notification payload
            // when the app is in the foreground.
            createNotification(it.title ?: "Notification", it.body ?: "You have a new notification.")
        }

        // Also if you intend to send messages to this application instance
        // from your server side, here is where you would handle that.
    }

    private fun createNotification(
        title: String,
        message: String,
    ) {
        // Make sure you have a notification icon (`notifcations` in R.drawable)
        // and the channel ID, name, and description are defined in your Util class.
        val notification = buildNotification(title, message)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(
        title: String,
        message: String,
    ): Notification {
        // Define the activity to open when the notification is tapped.
        // Ensure ActivitySuggestionActivity is the correct activity.
        val intent = Intent(applicationContext, ActivitySuggestionActivity::class.java).apply {
            // Add any data from the notification to the intent if needed
            // For example: putExtra("patientId", patientId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0, // Use a unique request code if you have multiple pending intents
                intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE,
            )
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val color: Int =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Ensure R.color.black exists in your resources
                resources.getColor(R.color.black, theme)
            } else {
                ContextCompat.getColor(this, R.color.black)
            }

        return NotificationCompat.Builder(this, Util.CHANNEL_ID)
            .setSmallIcon(R.drawable.notifcations) // Ensure this drawable exists
            .setColor(color)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        // Ensure Util.CHANNEL_ID, Util.CHANNEL_NAME, and Util.CHANNEL_DESCRIPTION are defined
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
        private const val NOTIFICATION_ID = 999 // Consider using a unique ID for each notification if you send multiple
        private const val TAG = "MyFirebaseMsgService" // Added a TAG for logging
    }
}

// Removed the annotation class RemoteMessage as you should be using Firebase's RemoteMessage class
// annotation class RemoteMessage