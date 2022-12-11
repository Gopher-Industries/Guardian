package com.example.tg_patient_profile.util;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Set;

public class Util {

    //variables for shared preferences
    public static final String DAILY_REPORT_LODGED = "daily_report_lodged";
    public static final String SHARED_PREF_DATA = "shared_pref_data";
    public static final String DAILY_REPORT_DATE = "daily_report_date";
    public static final String DAILY_REPORT_STATUS_LIST = "daily_report_status";
    public static final String DAILY_REPORT_STATUS_NOTES = "daily_report_notes";


    // care plan
    public static final String CARE_PLAN_TYPE = "care_plan_type";
    public static final String NUTRITION_HYDRATION_TYPE = "nutrition_hydration_type";
    public static final String SUPPORT_REQUIREMENTS = "support_requirements";
    public static final String DIET_TIMING = "diet_timing";
    public static final String DRINK_LIKES = "drink_likes";
    public static final String SLEEP_PATTERN = "sleep_pattern";
    public static final String PAIN = "pain";
    public static final String PAIN_SCORE = "pain_score";
    public static final String BEHAVIOUR_MANAGEMENT = "behaviour_management";

    // variables for notification
    public static final String NOTIFICATION_TITLE = "title";
    public static final String NOTIFICATION_RECEIVER_TOKEN = "to";
    public static final String NOTIFICATION_MESSAGE = "message";
    public static final String NOTIFICATION_DATA = "data";
    public static final String CHANNEL_ID = "guardians";
    public static final String CHANNEL_NAME = "guardians_app_notifications";
    public static final String CHANNEL_DESCRIPTION = "Guardians App Notifications";

    // token nodes
    public static final String TOKENS = "tokens";
    public static final String DEVICE_TOKEN = "device_token";

    public static void updateDeviceToken(final Context context, String token, String username) {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(); // get root node of the firebase
        DatabaseReference databaseReference = rootRef.child(Util.TOKENS).child(username); // get token node associated to the user

        // create new hashmap, used to specify nodes in the firebase
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Util.DEVICE_TOKEN, token);

        // set values of nodes in the firebase
        databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {

            // on click listener for if the node update is completed
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) // if token is failed to be updated
                {
                    Util.createToast(context, "Failed to update token");
                }
            }
        });
    }

    public static void createToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }










    public static final String[] setToArray(Set<String> hashSet) {
        String arr[] = new String[hashSet.size()];

        int i = 0;

        // iterating over the hashset
        for (String ele : hashSet) {
            arr[i++] = ele;
        }
        return arr;
    }
}
