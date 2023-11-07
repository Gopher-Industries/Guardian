package com.example.tg_patient_profile.model;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.util.Util;
import com.example.tg_patient_profile.view.patient.associateradar.ActivitySuggestionActivity;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService
    extends com.google.firebase.messaging.FirebaseMessagingService {
  @Override
  public void onNewToken(@NonNull final String s) {
    super.onNewToken(s);
    final String username = "user";
    Util.updateDeviceToken(this, s, username);
  }

  @Override
  public void onMessageReceived(@NonNull final RemoteMessage remoteMessage) {
    super.onMessageReceived(remoteMessage);

    final String title = "Guardians Alert";
    final String message =
        "Behaviour Anomalies on patient X - Radar device detected anomalies on patient X";
    final Intent intent = new Intent(getApplicationContext(), ActivitySuggestionActivity.class);
    final PendingIntent pendingIntent =
        PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
    final NotificationManager notificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    final NotificationCompat.Builder notificationBuilder;
    final NotificationChannel channel;

    if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
      channel =
          new NotificationChannel(
              Util.CHANNEL_ID, Util.CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
      channel.setDescription(Util.CHANNEL_DESCRIPTION);
      notificationManager.createNotificationChannel(channel);

      notificationBuilder =
          new NotificationCompat.Builder(this, Util.CHANNEL_ID)
              .setSmallIcon(R.drawable.notifcations)
              .setColor(getResources().getColor(R.color.black, getTheme()))
              .setContentTitle(title)
              .setAutoCancel(true)
              .setSound(defaultSoundUri)
              .setContentIntent(pendingIntent)
              .setContentText(message);

      notificationManager.notify(999, notificationBuilder.build()); // build notification
    }
  }
}
