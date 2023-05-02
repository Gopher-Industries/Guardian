package com.example.tg_patient_profile.view.caretaker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;

import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.view.general.PatientListActivity;

public class CaretakerForPatientList extends RecyclerView.ViewHolder {
    public ImageView arrowImage65;
    public ImageView arrowImage66;
    public ImageView arrowImage67;
    public ImageView arrowImage68;
    public ImageView arrowImage69;

    public CaretakerForPatientList(View itemView) {
        super(itemView);
        arrowImage65 = itemView.findViewById(R.id.imageView65);
        arrowImage65.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Because I don't have a Patient Profile page here yet, I'll use activity_caretakerprofile.xml temporarily for now
                Intent intent = new Intent(v.getContext(), CaretakerProfileActivity.class);

                v.getContext().startActivity(intent);
            }
        });

        arrowImage66 = itemView.findViewById(R.id.imageView66);
        arrowImage66.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Because I don't have a Patient Profile page here yet, I'll use activity_caretakerprofile.xml temporarily for now
                Intent intent = new Intent(v.getContext(), CaretakerProfileActivity.class);

                v.getContext().startActivity(intent);
            }
        });

        arrowImage67 = itemView.findViewById(R.id.imageView67);
        arrowImage67.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Because I don't have a Patient Profile page here yet, I'll use activity_caretakerprofile.xml temporarily for now
                Intent intent = new Intent(v.getContext(), CaretakerProfileActivity.class);

                v.getContext().startActivity(intent);
            }
        });

        arrowImage68 = itemView.findViewById(R.id.imageView68);
        arrowImage68.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Because I don't have a Patient Profile page here yet, I'll use activity_caretakerprofile.xml temporarily for now
                Intent intent = new Intent(v.getContext(), CaretakerProfileActivity.class);

                v.getContext().startActivity(intent);
            }
        });

        arrowImage69 = itemView.findViewById(R.id.imageView69);
        arrowImage69.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Because I don't have a Patient Profile page here yet, I'll use activity_caretakerprofile.xml temporarily for now
                Intent intent = new Intent(v.getContext(), CaretakerProfileActivity.class);

                v.getContext().startActivity(intent);
            }
        });
    }


}



