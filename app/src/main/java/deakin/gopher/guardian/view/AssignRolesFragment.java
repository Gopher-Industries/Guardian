package deakin.gopher.guardian.view;

import android.content.ClipData;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import deakin.gopher.guardian.R;
import deakin.gopher.guardian.adapter.NurseAdapter;

public class AssignRolesFragment extends Fragment {

    private RecyclerView unassignedPatientsRecyclerView;
    private RecyclerView nurse1RecyclerView;

    private List<String> patients; // No patient list assigned
    private List<String> nursePatients; // List of patients assigned to nurses

    public AssignRolesFragment() {
        // Required empty public constructor
    }

    public static AssignRolesFragment newInstance(String param1, String param2) {
        AssignRolesFragment fragment = new AssignRolesFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_assign_roles, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initialize RecyclerView and data
        unassignedPatientsRecyclerView = view.findViewById(R.id.unassignedPatientsRecyclerView);
        nurse1RecyclerView = view.findViewById(R.id.nurse1RecyclerView);

        // Set the unassigned patient list
        patients = new ArrayList<>(Arrays.asList("Patient 1", "Patient 2", "Patient 3", "Patient 4"));
//        PatientAdapter patientAdapter = new PatientAdapter(patients);
        unassignedPatientsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        unassignedPatientsRecyclerView.setAdapter(patientAdapter);

        // Set up the assignment area for nurse 1
        nursePatients = new ArrayList<>();
        NurseAdapter nurseAdapter = new NurseAdapter(nursePatients);
        nurse1RecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        nurse1RecyclerView.setAdapter(nurseAdapter);

        // Add drag and drop functionality
        setupDragAndDrop(unassignedPatientsRecyclerView, nurse1RecyclerView, patients, nursePatients);
    }

    private void setupDragAndDrop(RecyclerView sourceRecyclerView, RecyclerView targetRecyclerView,
                                  List<String> sourceList, List<String> targetList) {
        // Source list sets drag and drop events
        sourceRecyclerView.setOnLongClickListener(v -> {
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            ClipData data = ClipData.newPlainText("patient", ((TextView) v).getText().toString());
            v.startDragAndDrop(data, shadowBuilder, v, 0);
            return true;
        });

        // Set the drag and drop listener for the target list
        targetRecyclerView.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DROP:
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    String patient = item.getText().toString();

                    // Removed from the source list and added to the destination list
                    if (sourceList.remove(patient)) {
                        targetList.add(patient);
                        sourceRecyclerView.getAdapter().notifyDataSetChanged();
                        targetRecyclerView.getAdapter().notifyDataSetChanged();
                        Toast.makeText(getContext(), "Patient assigned!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                default:
                    return true;
            }
        });
    }
}
