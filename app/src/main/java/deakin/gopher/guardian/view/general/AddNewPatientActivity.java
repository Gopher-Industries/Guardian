package deakin.gopher.guardian.view.general;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.FirebaseDatabase;
import deakin.gopher.guardian.R;

import java.util.HashMap;
import java.util.Map;

public class AddNewPatientActivity extends BaseActivity {

  EditText name, address, underCare, photo, phone, dob, medicareNo;
  Button btnAdd, btnBack;

  private final String CHANNEL_ID = "patient_notifications";
  private final int NOTIFICATION_ID = 101;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_new_patient);

    // Ask for notification permission on Android 13+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
              != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
      }
    }

    // Find views
    name = findViewById(R.id.txtName);
    address = findViewById(R.id.txtAddress);
    underCare = findViewById(R.id.txtUnderCare);
    phone = findViewById(R.id.txtPhone);
    photo = findViewById(R.id.urlPhoto);
    dob = findViewById(R.id.txtDoB);
    medicareNo = findViewById(R.id.txtMedicareNumber);

    btnAdd = findViewById(R.id.btnAdd);
    btnBack = findViewById(R.id.btnBack);

    createNotificationChannel(); // For Android 8+

    btnAdd.setOnClickListener(v -> {
      insertData();
      clearAll();
    });

    btnBack.setOnClickListener(v -> onBackPressed());
  }

  private void insertData() {
    final Map<String, Object> map = new HashMap<>();
    map.put("name", name.getText().toString());
    map.put("address", address.getText().toString());
    map.put("underCare", underCare.getText().toString());
    map.put("phone", phone.getText().toString());
    map.put("photo", photo.getText().toString());
    map.put("dob", dob.getText().toString());
    map.put("medicareNo", medicareNo.getText().toString());

    FirebaseDatabase.getInstance()
            .getReference()
            .child("patient_profile")
            .push()
            .setValue(map)
            .addOnSuccessListener(unused -> {
              Toast.makeText(this, "New patient added", Toast.LENGTH_SHORT).show();
              showNotification();
            })
            .addOnFailureListener(e ->
                    Toast.makeText(this, "Error adding patient", Toast.LENGTH_SHORT).show());
  }

  private void showNotification() {
    // Skip if no permission
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
      return;
    }

    Intent intent = new Intent(this, AddNewPatientActivity.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_info_details) // ✅ built-in icon
            .setContentTitle("Patient Added")
            .setContentText("A new patient profile has been successfully added.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true);

    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
    notificationManager.notify(NOTIFICATION_ID, builder.build());
  }

  private void createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence name = "Patient Notifications";
      String description = "Notifies when new patient is added";
      int importance = NotificationManager.IMPORTANCE_HIGH;
      NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
      channel.setDescription(description);

      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      if (notificationManager != null) {
        notificationManager.createNotificationChannel(channel);
      }
    }
  }

  private void clearAll() {
    name.setText("");
    address.setText("");
    underCare.setText("");
    dob.setText("");
    photo.setText("");
    phone.setText("");
    medicareNo.setText("");
  }
}