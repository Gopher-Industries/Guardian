package com.example.tg_patient_profile.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tg_patient_profile.model.Patient;
import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.view.patient.Patient_Page;
import com.example.tg_patient_profile.view.patient.dailyreport.DailyReportActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatientListAdapter extends FirebaseRecyclerAdapter<Patient, PatientListAdapter.myViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    Context context;
    public PatientListAdapter(Context context, @NonNull FirebaseRecyclerOptions<Patient> options) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull Patient model) {

        holder.patient_name.setText(model.getFirst_name()+" "+model.getMiddle_name()+" "+model.getLast_name());
        holder.patient_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);
                int role = sharedPreferences.getInt("login_role", -1);
                if(role==0){
                    context.startActivity(new Intent(context, DailyReportActivity.class));

                }else if(role ==1){
                    context.startActivity(new Intent(context, Patient_Page.class));

                }else{
                }
            }
        });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_patieng_list_item, parent, false);

        return new myViewHolder(view);
    }


    class myViewHolder extends RecyclerView.ViewHolder{

        TextView patient_name;
        CardView patient_item;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            patient_name = itemView.findViewById(R.id.patient_list_name);
            patient_item = itemView.findViewById(R.id.patient_list_patient_item);

        }
    }


}
