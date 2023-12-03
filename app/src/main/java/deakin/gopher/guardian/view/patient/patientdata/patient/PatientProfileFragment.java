package deakin.gopher.guardian.view.patient.patientdata.patient;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import deakin.gopher.guardian.R;
import deakin.gopher.guardian.model.Patient;

//public class PatientProfileFragment extends Fragment {
//  public PatientProfileFragment() {}
//
//  @Override
//  public void onCreate(final Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//  }
//
//  @Override
//  public View onCreateView(
//      final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
//    return inflater.inflate(R.layout.fragment_patient_profile, container, false);
//  }
//}


public class PatientProfileFragment extends Fragment {

    public PatientProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_profile, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String patientId = bundle.getString("patientId");
            if (patientId != null) {
                loadPatientData(patientId, view);
            }
        }
        return view;
    }

    private void loadPatientData(String patientId, View view) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("patient_profile").child(patientId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String firstName = dataSnapshot.hasChild("name") ? dataSnapshot.child("name").getValue(String.class) : "N/A";
                String middleName = dataSnapshot.hasChild("middle_name") ? dataSnapshot.child("middle_name").getValue(String.class) : "";
                String lastName = dataSnapshot.hasChild("last_name") ? dataSnapshot.child("last_name").getValue(String.class) : "N/A";
                String dob = dataSnapshot.hasChild("dob") ? dataSnapshot.child("dob").getValue(String.class) : "N/A";

                // Update TextViews with the fetched data
                ((TextView) view.findViewById(R.id.txtFirstName)).setText(firstName);
                ((TextView) view.findViewById(R.id.txtMiddleName)).setText(middleName);
                ((TextView) view.findViewById(R.id.txtLastName)).setText(lastName);
                ((TextView) view.findViewById(R.id.txtDateOfBirth)).setText(dob);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}


