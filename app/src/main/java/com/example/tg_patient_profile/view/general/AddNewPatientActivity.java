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

    EditText name, id, caretaker, photo, dob, medicare;
    Button btnAdd, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_patient);

        name = (EditText) findViewById(R.id.patientNameEditText);
        id = (EditText) findViewById(R.id.patientIDEditText);
        dob = (EditText) findViewById(R.id.dateOfBirthEditText);
        photo =(EditText) findViewById(R.id.patientPhotoURLEditText);
        dob = (EditText) findViewById(R.id.dateOfBirthEditText);
        caretaker = (EditText) findViewById(R.id.careTakerEditText);
        medicare = (EditText) findViewById(R.id.medicareEditText);

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
        map.put("id", id.getText().toString());
        map.put("medicare", medicare.getText().toString());
        map.put("caretaker", caretaker.getText().toString());
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
        id.setText("");
        caretaker.setText("");
        dob.setText("");
        photo.setText("");
        medicare.setText("");
    }
}