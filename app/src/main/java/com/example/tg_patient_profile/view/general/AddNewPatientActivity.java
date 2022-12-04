package com.example.tg_patient_profile.view.general;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tg_patient_profile.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddNewPatientActivity extends AppCompatActivity {

    EditText name, address, underCare, photo, phone, dob;
    Button btnAdd, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_patient);

        name=(EditText) findViewById(R.id.txtName);
        address=(EditText) findViewById(R.id.txtAddress);
        underCare=(EditText) findViewById(R.id.txtUnderCare);
        phone=(EditText) findViewById(R.id.txtPhone);
        photo=(EditText) findViewById(R.id.urlPhoto);
        dob=(EditText) findViewById(R.id.txtDoB);

        btnAdd=(Button) findViewById(R.id.btnAdd);
        btnBack=(Button) findViewById(R.id.btnBack);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               insertData();
                clearAll();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                onBackPressed();
            }
        });
    }

    private void insertData()
    {
        Map<String, Object> map =new HashMap<>();
        map.put("name", name.getText().toString());
        map.put("address", address.getText().toString());
        map.put("underCare", underCare.getText().toString());
        map.put("phone", phone.getText().toString());
        map.put("photo", photo.getText().toString());
        map.put("dob", dob.getText().toString());

        FirebaseDatabase.getInstance().getReference().child("patients").push()
                .setValue(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddNewPatientActivity.this,"New patient added",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(AddNewPatientActivity.this,"Error adding patient",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearAll()
    {
        name.setText("");
        address.setText("");
        underCare.setText("");
        dob.setText("");
        photo.setText("");
        phone.setText("");
    }
}