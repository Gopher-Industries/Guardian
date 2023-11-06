package com.example.tg_patient_profile.util;

import android.content.Context;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Util {
  public static final String DAILY_REPORT_LODGED = "daily_report_lodged";
  public static final String SHARED_PREF_DATA = "shared_pref_data";
  public static final String DAILY_REPORT_DATE = "daily_report_date";
  public static final String DAILY_REPORT_STATUS_LIST = "daily_report_status";
  public static final String DAILY_REPORT_STATUS_NOTES = "daily_report_notes";
  public static final String CHANNEL_ID = "guardians";
  public static final String CHANNEL_NAME = "guardians_app_notifications";
  public static final String CHANNEL_DESCRIPTION = "Guardians App Notifications";
  // token nodes
  public static final String TOKENS = "tokens";
  public static final String DEVICE_TOKEN = "device_token";

  public static void updateDeviceToken(
      final Context context, final String token, final String username) {

    final DatabaseReference rootRef =
        FirebaseDatabase.getInstance().getReference(); // get root node of the firebase
    final DatabaseReference databaseReference =
        rootRef.child(Util.TOKENS).child(username); // get token node associated to the user

    final Map<String, String> hashMap = new HashMap<>();
    hashMap.put(Util.DEVICE_TOKEN, token);

    databaseReference
        .setValue(hashMap)
        .addOnCompleteListener(
            task -> {
              if (!task.isSuccessful()) {
                Util.createToast(context, "Failed to update token");
              }
            });
  }

  public static void createToast(final Context context, final CharSequence message) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
  }

  public static String[] setToArray(final Collection<String> hashSet) {
    final String[] arr = new String[hashSet.size()];

    int i = 0;

    // iterating over the hashset
    for (final String ele : hashSet) {
      arr[i++] = ele;
    }
    return arr;
  }
}
