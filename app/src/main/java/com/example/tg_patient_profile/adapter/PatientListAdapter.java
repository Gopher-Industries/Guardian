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
import com.example.tg_patient_profile.model.Patient;
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

public class PatientListAdapter extends FirebaseRecyclerAdapter<Patient, PatientListAdapter.myViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    private OnPatientListener onPatientClick; //Interface defining methods to override in the MainActivity

    public PatientListAdapter(@NonNull FirebaseRecyclerOptions<Patient> options, OnPatientListener onPatientClick) {
        super(options);

        this.onPatientClick = onPatientClick;
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder,final int position, @NonNull Patient model) {
        holder.name.setText(model.getName());
        holder.address.setText(model.getAddress());
        holder.dob.setText(model.getDob());
        holder.phone.setText(model.getPhone());
        holder.underCare.setText(model.getUnderCare());

        Glide.with(holder.photo.getContext())
                .load(model.getPhoto())
                .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                .circleCrop()
                .error(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(holder.photo);

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialogPlus=DialogPlus.newDialog(holder.photo.getContext())
                        .setContentHolder(new ViewHolder(R.layout.update_popup))
                        .setExpanded(true,1500)
                        .create();

                View view =dialogPlus.getHolderView();

                EditText name =view.findViewById(R.id.txtName);
                EditText dob=view.findViewById(R.id.txtDoB);
                EditText address=view.findViewById(R.id.txtAddress);
                EditText phone=view.findViewById(R.id.txtPhone);
                EditText photo=view.findViewById(R.id.urlPhoto);
                EditText underCare=view.findViewById(R.id.txtUnderCare);

                Button btnUpdate=view.findViewById(R.id.btnUpdate);

                name.setText(model.getName());
                dob.setText(model.getDob());
                address.setText(model.getAddress());
                phone.setText(model.getPhone());
                photo.setText(model.getPhoto());
                underCare.setText(model.getUnderCare());
                dialogPlus.show();

                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String,Object> map = new HashMap<>();
                        map.put("name",name.getText().toString());
                        map.put("address",address.getText().toString());
                        map.put("dob",dob.getText().toString());
                        map.put("phone",phone.getText().toString());
                        map.put("photo",photo.getText().toString());
                        map.put("underCare",underCare.getText().toString());

                        FirebaseDatabase.getInstance().getReference().child("patients")
                                .child(getRef(position).getKey()).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(holder.name.getContext(),"Data Updated",Toast.LENGTH_SHORT);
                                        dialogPlus.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        Toast.makeText(holder.name.getContext(),"Failed to Updated",Toast.LENGTH_SHORT);
                                        dialogPlus.dismiss();
                                    }
                                });
                    }
                });
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.name.getContext());
                builder.setTitle("Are you sure?");
                builder.setMessage("Data will be deleted permanently !");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        FirebaseDatabase.getInstance().getReference().child("patients")
                                .child(getRef(position).getKey()).removeValue();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        Toast.makeText(holder.name.getContext(),"Canceled",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_data_patient_list_item,parent,false);
        return new myViewHolder(item, onPatientClick);
    }

    class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CircleImageView photo;
        TextView name, address, dob, phone, underCare;

        Button btnEdit, btnDelete;

        //Interface variables
        OnPatientListener onPatientClick;

        public myViewHolder(@NonNull View itemView, OnPatientListener onPatientClick) {
            super(itemView);

            photo=(CircleImageView) itemView.findViewById(R.id.img1);
            name=(TextView)itemView.findViewById(R.id.nametext);
            address=(TextView) itemView.findViewById(R.id.addresstext);
            dob=(TextView) itemView.findViewById(R.id.dobtext);
            phone=(TextView) itemView.findViewById(R.id.phonetext);
            underCare=(TextView) itemView.findViewById(R.id.undercaretext);

            btnEdit=(Button)itemView.findViewById(R.id.btnEdit);
            btnDelete=(Button) itemView.findViewById(R.id.btnDelete);

            //Define the interface variables
            this.onPatientClick = onPatientClick;

            //Define the interface variables
            itemView.setOnClickListener((View.OnClickListener) this);
        }

        //OnClickListener for the ViewHolder
        @Override
        public void onClick(View view) {

            //Listener for the entire Order item
            if (view == itemView) {
                onPatientClick.onPatientClick(getAdapterPosition()); //Defined in HomeActivity and MyOrdersActivity
            }
        }
    }

    //Interface to be implemented by PatientListActivity
    public interface OnPatientListener {
        void onPatientClick(int position); //Listener method to open up the PatientDashboardActivity - for PatientDashboardActivity
    }
}
