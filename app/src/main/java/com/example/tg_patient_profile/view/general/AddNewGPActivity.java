package com.example.tg_patient_profile.view.general;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tg_patient_profile.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddNewGPActivity extends AppCompatActivity {
    EditText clinic_address, fax, first_name, middle_name, last_name, phone, photo, email;

    Button btnAdd, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gp_add);

        first_name=(EditText) findViewById(R.id.txtFirstName);
        middle_name=(EditText) findViewById(R.id.txtMiddleName);
        last_name=(EditText) findViewById(R.id.txtLastName);
        clinic_address=(EditText) findViewById(R.id.txtClinicAddress);
        email=(EditText) findViewById(R.id.txtEmail);
        phone=(EditText) findViewById(R.id.txtPhone);
        photo=(EditText) findViewById(R.id.urlPhoto);
        fax=(EditText) findViewById(R.id.txtFax);

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

        Button uploadPhotoButton = findViewById(R.id.uploadPhotoButton);

        uploadPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent uploadPhotoIntent = new Intent(AddNewGPActivity.this, UploadPhoto.class);
                startActivity(uploadPhotoIntent);
            }
        });
    }

    private void insertData()
    {
        Map<String, Object> map =new HashMap<>();
        map.put("first_name", first_name.getText().toString());
        map.put("middle_name", middle_name.getText().toString());
        map.put("last_name", last_name.getText().toString());
        map.put("clinic_address", clinic_address.getText().toString());
        map.put("fax", fax.getText().toString());
        map.put("phone", phone.getText().toString());
        map.put("photo", photo.getText().toString());
        map.put("email", email.getText().toString());

        FirebaseDatabase.getInstance().getReference().child("gp").push()
                .setValue(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddNewGPActivity.this,"New GP added",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(AddNewGPActivity.this,"Error adding GP",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearAll()
    {
        first_name.setText("");
        middle_name.setText("");
        last_name.setText("");
        clinic_address.setText("");
        email.setText("");
        fax.setText("");
        photo.setText("");
        phone.setText("");
    }
}