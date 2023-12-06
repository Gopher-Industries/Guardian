package deakin.gopher.guardian.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.model.Patient;
import deakin.gopher.guardian.model.PatientStatus;
import deakin.gopher.guardian.view.general.PatientProfileActivity;
import java.util.Objects;

public class PatientListAdapter
    extends FirebaseRecyclerAdapter<Patient, PatientListAdapter.myViewHolder> {
  /**
   * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See {@link
   * FirebaseRecyclerOptions} for configuration options.
   */
  final Context context;

  public PatientListAdapter(
      final Context context, @NonNull final FirebaseRecyclerOptions<Patient> options) {
    super(options);
    this.context = context;
  }

  @Override
  protected void onBindViewHolder(
      @NonNull final myViewHolder holder, final int position, @NonNull final Patient model) {
    holder.patient_name.setText(
        model.getFirstName() + " " + model.getMiddleName() + " " + model.getLastName());
    updateStatusIndicator(holder.statusIndicator, model.getStatus());

    Log.d("PatientListAdapter", "Patient ID: " + model.getPatientId());
    holder.patient_item.setOnClickListener(
        view -> {
          final SharedPreferences sharedPreferences =
              context.getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);
          final int role = sharedPreferences.getInt("login_role", -1);
          if (0 == role || -1 == role) {

            Log.d("PatientListAdapter", "Before toggle: " + model.getStatus());
            model.examinePatient();
            Log.d("PatientListAdapter", "After toggle: " + model.getStatus());
            updateStatusIndicator(holder.statusIndicator, model.getStatus());
            notifyItemChanged(position);

            final Intent intent = new Intent(context, PatientProfileActivity.class);

            intent.putExtra("patientId", model.getPatientId());
            context.startActivity(intent);
            updatePatientStatus(
                model.getPatientId(), model.getStatus(), model.getNeedsAssistance());

          } else if (1 == role) {
            // admin
            final Intent intent = new Intent(context, PatientProfileActivity.class);
            intent.putExtra("id", model.getPatientId());
            context.startActivity(intent);
          }
        });
  }

  private void updateStatusIndicator(final ImageView statusIndicator, final PatientStatus status) {
    final int statusColor =
        (PatientStatus.REQUIRES_ASSISTANCE == status)
            ? ContextCompat.getColor(context, R.color.red)
            : ContextCompat.getColor(context, R.color.green);

    statusIndicator.setColorFilter(statusColor);
    final Animation pulseAnimation = AnimationUtils.loadAnimation(context, R.anim.scale_animation);
    statusIndicator.startAnimation(pulseAnimation);
  }

  public void updatePatientStatus(
      String patientId, PatientStatus newStatus, boolean needsAssistance) {
    final DatabaseReference patientsRef = FirebaseDatabase.getInstance().getReference("patients");

    patientsRef
        .child(patientId)
        .child("status")
        .setValue(newStatus.toString())
        .addOnSuccessListener(
            aVoid -> Log.d("UpdateStatus", "Patient status updated successfully."))
        .addOnFailureListener(e -> Log.e("UpdateStatus", "Failed to update patient status.", e));
    patientsRef.child(patientId).child("needsAssistance").setValue(needsAssistance);
  }

  private int getStatusColor(final Patient patient) {
    long oneHourMillis = 60000;
    long currentTime = System.currentTimeMillis();

    if (currentTime - patient.getLastExaminedTimestamp() > oneHourMillis) {
      return ContextCompat.getColor(context, R.color.orange);
    } else {
      if (PatientStatus.REQUIRES_ASSISTANCE == Objects.requireNonNull(patient.getStatus())) {
        return ContextCompat.getColor(context, R.color.red);
      }
      return ContextCompat.getColor(context, R.color.green);
    }
  }

  @NonNull
  @Override
  public myViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
    final View view =
        LayoutInflater.from(parent.getContext())
            .inflate(R.layout.activity_patient_list_item, parent, false);

    return new myViewHolder(view);
  }

  public void deleteItem(final int position) {
    final DatabaseReference itemRef = getRef(position);
    itemRef.removeValue();
  }

  static class myViewHolder extends RecyclerView.ViewHolder {
    final TextView patient_name;
    final CardView patient_item;
    final ImageView statusIndicator;

    public myViewHolder(@NonNull final View itemView) {
      super(itemView);
      patient_name = itemView.findViewById(R.id.patient_list_name);
      patient_item = itemView.findViewById(R.id.patient_list_patient_item);
      statusIndicator = itemView.findViewById(R.id.patient_status_indicator);
      Log.d("PatientListAdapter", "Indicator loaded " + statusIndicator);
    }
  }
}
