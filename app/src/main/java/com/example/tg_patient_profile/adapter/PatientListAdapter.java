package com.example.tg_patient_profile.adapter;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.model.Patient;
import com.example.tg_patient_profile.view.general.PatientProfileActivity;
import com.example.tg_patient_profile.view.patient.dailyreport.DailyReportActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;


public class PatientListAdapter extends FirebaseRecyclerAdapter<Patient, PatientListAdapter.myViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     */
    Context context;

    public PatientListAdapter(final Context context, @NonNull final FirebaseRecyclerOptions<Patient> options) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull final myViewHolder holder, final int position, @NonNull final Patient model) {

        holder.patient_name.setText(model.getFirst_name() + " " + model.getMiddle_name() + " " + model.getLast_name());
        holder.patient_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final SharedPreferences sharedPreferences = context.getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);
                final int role = sharedPreferences.getInt("login_role", -1);
                if (0 == role) {
                    //caretaker
                    //context.startActivity(new Intent(context, DailyReportActivity.class));
                    final Intent intent = new Intent(context, DailyReportActivity.class);
                    intent.putExtra("patientName", holder.patient_name.getText());
                    context.startActivity(intent);

                } else if (1 == role) {
                    //admin
                    final Intent intent = new Intent(context, PatientProfileActivity.class);
                    intent.putExtra("id", model.getPatient_id());
                    context.startActivity(intent);

                }
            }
        });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_patieng_list_item, parent, false);

        return new myViewHolder(view);
    }


    static class myViewHolder extends RecyclerView.ViewHolder {

        TextView patient_name;
        CardView patient_item;


        public myViewHolder(@NonNull final View itemView) {
            super(itemView);
            patient_name = itemView.findViewById(R.id.patient_list_name);
            patient_item = itemView.findViewById(R.id.patient_list_patient_item);

        }
    }


}