package com.example.tg_patient_profile.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tg_patient_profile.model.GP;
import com.example.tg_patient_profile.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class GPListAdapter extends FirebaseRecyclerAdapter<GP, GPListAdapter.myViewHolder> {

    public GPListAdapter(@NonNull FirebaseRecyclerOptions<GP> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, final int position, @NonNull GP model) {
        holder.first_name.setText(model.getFirst_name());
        holder.middle_name.setText(model.getMiddle_name());
        holder.last_name.setText(model.getLast_name());
        holder.clinic_address.setText(model.getClinic_address());
        holder.fax.setText(model.getFax());
        holder.phone.setText(model.getPhone());
        holder.email.setText(model.getEmail());

        Glide.with(holder.photo.getContext())
                .load(model.getPhoto())
                .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                .circleCrop()
                .error(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(holder.photo);

        holder.btnEdit_gp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialogPlus=DialogPlus.newDialog(holder.photo.getContext())
                        .setContentHolder(new ViewHolder(R.layout.activity_gp_update))
                        .setExpanded(true,1830)
                        .create();

                View view =dialogPlus.getHolderView();

                EditText first_name =view.findViewById(R.id.txtFirstName);
                EditText middle_name =view.findViewById(R.id.txtMiddleName);
                EditText last_name =view.findViewById(R.id.txtLastName);
                EditText fax=view.findViewById(R.id.txtFax);
                EditText clinic_address=view.findViewById(R.id.txtClinicAddress);
                EditText phone=view.findViewById(R.id.txtPhone);
                EditText photo=view.findViewById(R.id.urlPhoto);
                EditText email=view.findViewById(R.id.txtEmail);

                Button btnUpdate=view.findViewById(R.id.btnUpdate);

                first_name.setText(model.getFirst_name());
                middle_name.setText(model.getMiddle_name());
                last_name.setText(model.getLast_name());
                fax.setText(model.getFax());
                clinic_address.setText(model.getClinic_address());
                phone.setText(model.getPhone());
                photo.setText(model.getPhoto());
                email.setText(model.getEmail());
                dialogPlus.show();

                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String,Object> map = new HashMap<>();
                        map.put("first_name",first_name.getText().toString());
                        map.put("middle_name",middle_name.getText().toString());
                        map.put("last_name",last_name.getText().toString());
                        map.put("clinic_address",clinic_address.getText().toString());
                        map.put("fax",fax.getText().toString());
                        map.put("phone",phone.getText().toString());
                        map.put("photo",photo.getText().toString());
                        map.put("email",email.getText().toString());

                        FirebaseDatabase.getInstance().getReference().child("gp")
                                .child(getRef(position).getKey()).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(holder.first_name.getContext(),"Data Updated",Toast.LENGTH_SHORT);
                                        dialogPlus.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        Toast.makeText(holder.first_name.getContext(),"Failed to Updated",Toast.LENGTH_SHORT);
                                        dialogPlus.dismiss();
                                    }
                                });
                    }
                });
            }
        });

        holder.btnDelete_gp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.first_name.getContext());
                builder.setTitle("Are you sure?");
                builder.setMessage("Data will be deleted permanently !");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        FirebaseDatabase.getInstance().getReference().child("gp")
                                .child(getRef(position).getKey()).removeValue();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        Toast.makeText(holder.first_name.getContext(),"Canceled",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_gp_profile,parent,false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        CircleImageView photo;
        TextView clinic_address, fax, first_name, middle_name, last_name, phone, email;

        Button btnEdit_gp, btnDelete_gp;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            photo=(CircleImageView) itemView.findViewById(R.id.img1);
            first_name=(TextView)itemView.findViewById(R.id.first_nametext);
            middle_name=(TextView)itemView.findViewById(R.id.middle_nametext);
            last_name=(TextView)itemView.findViewById(R.id.last_nametext);
            clinic_address=(TextView) itemView.findViewById(R.id.clinic_addresstext);
            fax=(TextView) itemView.findViewById(R.id.faxtext);
            phone=(TextView) itemView.findViewById(R.id.phonetext);
            email=(TextView) itemView.findViewById(R.id.emailtext);

            btnEdit_gp=(Button)itemView.findViewById(R.id.btnEdit_gp);
            btnDelete_gp=(Button) itemView.findViewById(R.id.btnDelete_gp);
        }
    }
}
