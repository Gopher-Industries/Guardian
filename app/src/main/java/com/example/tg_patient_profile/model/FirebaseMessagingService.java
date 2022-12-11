package com.example.tg_patient_profile.model;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.util.Util;
import com.example.tg_patient_profile.view.patient.associateradar.ActivityProfilingActivity;
import com.example.tg_patient_profile.view.patient.associateradar.ActivitySuggestionActivity;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    // used for different logged in user
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        String username = "user";
        Util.updateDeviceToken(this, s,username);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // fetch data from the notification received
        // only applicable for when radar device customizes messages
//        String title = remoteMessage.getData().get(Util.NOTIFICATION_TITLE);
//        String message = remoteMessage.getData().get(Util.NOTIFICATION_MESSAGE);

        String title = "Guardians Alert";
        String message = "Behaviour Anomalies on patient X - Radar device detected anomalies on patient X";

        Intent intent = new Intent(getApplicationContext(), ActivitySuggestionActivity.class); // create a new intent object

        // wrap chatIntent in pendingIntent as it can only be used once
        PendingIntent pendingIntent  = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // create Notification Manager to show the notification
        final NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        // create default ringtone
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // default ringtone

        // create new Notification Builder
        final NotificationCompat.Builder notificationBuilder;

        // create Notification Channel to group notifications separately
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(Util.CHANNEL_ID, Util.CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(Util.CHANNEL_DESCRIPTION); // set description of channel
            notificationManager.createNotificationChannel(channel); // bind channel to notification manager
            Log.i("masuk", "masuk sinik");

            notificationBuilder = new NotificationCompat.Builder(this,Util.CHANNEL_ID); // build a new notification with the channel id
            // binding data to notification builder
            notificationBuilder.setSmallIcon(R.drawable.notifcations); // to show small icon
            notificationBuilder.setColor(getResources().getColor(R.color.black)); // set text of notification
            notificationBuilder.setContentTitle(title); // set title of notification
            notificationBuilder.setAutoCancel(true); // allow auto cancel
            notificationBuilder.setSound(defaultSoundUri); // play a sound when notification is received
            notificationBuilder.setContentIntent(pendingIntent); // set intent when notification is clicked
            notificationBuilder.setContentText(message); // set text of the notification
            notificationManager.notify(999, notificationBuilder.build()); // build notification
        }
    }
}
