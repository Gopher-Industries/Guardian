package com.example.tg_patient_profile.view.general;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.adapter.PatientProfileAddAdapter;
import com.example.tg_patient_profile.model.GP;
import com.example.tg_patient_profile.model.Medical_diagnostic;
import com.example.tg_patient_profile.model.NextofKin;
import com.example.tg_patient_profile.model.Patient;
import com.example.tg_patient_profile.util.DataListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class PatientProfileAddActivity extends AppCompatActivity implements DataListener {

    private CustomHeader customHeader;
    private Patient patient;
    private NextofKin nextofKin1, nextofKin2;
    private GP gp1, gp2;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile_add);

        final ViewPager2 viewPager2 = findViewById(R.id.dataForViewViewPager);
        customHeader = findViewById(R.id.customHeader);
        final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        final PatientProfileAddAdapter viewPagerAdapter = new PatientProfileAddAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager2.setAdapter(viewPagerAdapter);

        customHeader.setHeaderHeight(450);
        customHeader.setHeaderText("Patient Add");
        customHeader.setHeaderTopImageVisibility(View.VISIBLE);
        customHeader.setHeaderTopImage(R.drawable.add_image_button);


        if (null != customHeader) {
            customHeader.menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (null != drawerLayout) {
                        drawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });
        }

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(final int position) {
                switch (position) {
                    case 0:
                        customHeader.setHeaderText("Add New Patient");
                        break;
                    case 1:
                        customHeader.setHeaderText("Add Next of Kin");
                        break;
                    case 2:
                        customHeader.setHeaderText("Add Next of Kin+");
                        break;
                    case 3:
                        customHeader.setHeaderText("Add General Practitioner");
                        break;
                    default:
                        customHeader.setHeaderText("Add General Practitioner+");
                        break;


                }
            }
        });
    }

    @Override
    public void onDataFilled(final Patient patient, final NextofKin nextofKin1, final NextofKin nextofKin2, final GP gp1, final GP gp2) {

        if (null != patient) {
            this.patient = patient;
        }

        if (null != nextofKin1) {
            this.nextofKin1 = nextofKin1;
        }


        if (null != nextofKin2) {
            this.nextofKin2 = nextofKin2;
        }


        if (null != gp1) {
            this.gp1 = gp1;
        }


        if (null != gp2) {
            this.gp2 = gp2;
        }
    }

    @Override
    public void onDataFinihsed(final Boolean isFinished) {
        // insert a dialog here

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Saving Changes?");
        // builder.setMessage("Do you really want to whatever?");
        //builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int whichButton) {
                saveInFirebase();
                //Toast.makeText(MainActivity.this, "Yaay", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("No", null);

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorGreen));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorRed));
                //dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.black));
            }
        });
        dialog.show();
        //insert


    }

    private void saveInFirebase() {
        final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference patientRef = databaseRef.child("patient_profile");
        final DatabaseReference nokRef = databaseRef.child("next_of_kins_profile");
        final DatabaseReference gpRef = databaseRef.child("gp_profile");
        final String nof1_id = nokRef.push().getKey();
        final String nof2_id = nokRef.push().getKey();
        final String gp1_id = gpRef.push().getKey();
        final String gp2_id = gpRef.push().getKey();
        patient.setNok_id1(nof1_id);
        patient.setNok_id2(nof2_id);
        patient.setGp_id1(gp1_id);
        patient.setGp_id2(gp2_id);
        nokRef.child(nof1_id).setValue(nextofKin1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(final Void unused) {
                        Log.v("nok1 success", "");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull final Exception e) {
                        Toast.makeText(PatientProfileAddActivity.this, "fail to upload first next of kin" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        nokRef.child(nof2_id).setValue(nextofKin2)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(final Void unused) {
                        Log.v("nok2success", "");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull final Exception e) {
                        Toast.makeText(PatientProfileAddActivity.this, "fail to upload second next of kin" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        gpRef.child(gp1_id).setValue(gp1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(final Void unused) {
                        Log.v("gp1success", "");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull final Exception e) {
                        Toast.makeText(PatientProfileAddActivity.this, "fail to upload first gp" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        if (null != gp2) {

            gpRef.child(gp2_id).setValue(gp2)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(final Void unused) {
                            Log.v("gp2success", "");

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull final Exception e) {
                            Toast.makeText(PatientProfileAddActivity.this, "fail to upload second gp" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        final String patient_id = patientRef.push().getKey();
        patientRef.child(patient_id).setValue(patient)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(final Void unused) {
                        Log.v("patientsuccess", "");
                        Toast.makeText(PatientProfileAddActivity.this, "Success on adding a new patient", Toast.LENGTH_SHORT).show();
                        final Intent intent = new Intent(PatientProfileAddActivity.this, PatientProfileActivity.class);
                        intent.putExtra("created_patient_id", patient_id);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull final Exception e) {
                        Toast.makeText(PatientProfileAddActivity.this, "fail to upload patient" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
        PatientRelatedCreat(patient_id);

    }

    private void PatientRelatedCreat(final String patient_id) {
        createMedicalDiagnostic(patient_id);
    }

    private void createMedicalDiagnostic(final String patient_id) {
        final Medical_diagnostic current_medical_diagnostic = new Medical_diagnostic(patient_id, true);
        final Medical_diagnostic prev_medical_diagnostic = new Medical_diagnostic(patient_id, false);
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("health_details");
        final Query query = reference
                .orderByChild("patient_id")
                .equalTo(patient_id);
        final String id = reference.push().getKey();
        reference.child(id).setValue(current_medical_diagnostic)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull final Exception e) {
                        Toast.makeText(PatientProfileAddActivity.this, "Fail to create health detail of this patient!Please try it again!Reason:" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
}