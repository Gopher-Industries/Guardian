package deakin.gopher.guardian.view.general;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.FirebaseDatabase;
import deakin.gopher.guardian.R;
import java.util.HashMap;
import java.util.Map;

public class AddNewPatientActivity extends BaseActivity {

  EditText name, address, underCare, photo, phone, dob, medicareNo;
  Button btnAdd, btnBack;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_new_patient);

    name = findViewById(R.id.txtName);
    address = findViewById(R.id.txtAddress);
    underCare = findViewById(R.id.txtUnderCare);
    phone = findViewById(R.id.txtPhone);
    photo = findViewById(R.id.urlPhoto);
    dob = findViewById(R.id.txtDoB);
    medicareNo = findViewById(R.id.txtMedicareNumber);

    btnAdd = findViewById(R.id.btnAdd);
    btnBack = findViewById(R.id.btnBack);

    btnAdd.setOnClickListener(
        v -> {
          insertData();
          clearAll();
        });

    btnBack.setOnClickListener(
        v -> {
          // finish();
          onBackPressed();
        });
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
        .addOnSuccessListener(
            unused ->
                Toast.makeText(AddNewPatientActivity.this, "New patient added", Toast.LENGTH_SHORT)
                    .show())
        .addOnFailureListener(
            e ->
                Toast.makeText(
                        AddNewPatientActivity.this, "Error adding patient", Toast.LENGTH_SHORT)
                    .show());
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
