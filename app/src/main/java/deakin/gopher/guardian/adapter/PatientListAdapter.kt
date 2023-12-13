package deakin.gopher.guardian.adapter;

import android.annotation.SuppressLint;
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
import android.widget.Toast;
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
  private final Context context;

  private final boolean isArchivedList;

  public PatientListAdapter(
      final Context context,
      @NonNull final FirebaseRecyclerOptions<Patient> options,
      final boolean isArchivedList) {
    super(options);
    this.context = context;
    this.isArchivedList = isArchivedList;
  }

  @Override
  public void onDataChanged() {
    super.onDataChanged();
    if (getItemCount() == 0) {
      Log.d("PatientListAdapter", "No archived patients found.");
    } else {
      Log.d("PatientListAdapter", "Archived patients loaded: " + getItemCount());
    }
  }

  @Override
  protected void onBindViewHolder(
      @NonNull final myViewHolder holder, final int position, @NonNull final Patient model) {
    holder.patient_name.setText(model.firstName + " " + model.middleName + " " + model.lastName);
    updateStatusIndicator(holder.statusIndicator, model.status);
    holder.bind(model, isArchivedList);

    Log.d("PatientListAdapter", "Patient ID: " + model.patientId);
    holder.patient_item.setOnClickListener(
        view -> {
          final SharedPreferences sharedPreferences =
              context.getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);
          final int role = sharedPreferences.getInt("login_role", -1);
          if (0 == role || -1 == role) {

            Log.d("PatientListAdapter", "Before toggle: " + model.status);
            model.examinePatient();
            Log.d("PatientListAdapter", "After toggle: " + model.status);
            updateStatusIndicator(holder.statusIndicator, model.status);
            notifyItemChanged(position);

            final Intent intent = new Intent(context, PatientProfileActivity.class);

            intent.putExtra("patientId", model.patientId);
            context.startActivity(intent);
            updatePatientStatus(model.patientId, model.status, model.needsAssistance);

          } else if (1 == role) {
            // admin
            final Intent intent = new Intent(context, PatientProfileActivity.class);
            intent.putExtra("id", model.patientId);
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

    if (currentTime - patient.lastExaminedTimestamp > oneHourMillis) {
      return ContextCompat.getColor(context, R.color.orange);
    } else {
      if (PatientStatus.REQUIRES_ASSISTANCE == Objects.requireNonNull(patient.status)) {
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

    return new myViewHolder(view, context);
  }

  public void deleteItem(final int position) {
    final DatabaseReference itemRef = getRef(position);
    itemRef
        .removeValue()
        .addOnSuccessListener(
            aVoid -> {
              Toast.makeText(
                      context,
                      "Deleted successfully, click on archive to restore",
                      Toast.LENGTH_LONG)
                  .show();
            })
        .addOnFailureListener(
            e -> {
              Toast.makeText(context, "Error during deletion", Toast.LENGTH_LONG).show();
            });
  }

  static class myViewHolder extends RecyclerView.ViewHolder {
    final TextView patient_name;
    final CardView patient_item;
    final ImageView statusIndicator;

    private final Context context;

    private TextView patientNameTextView;
    private final ImageView actionButton;

    public myViewHolder(@NonNull final View itemView, Context context) {
      super(itemView);
      this.context = context;
      patient_name = itemView.findViewById(R.id.patient_list_name);
      patient_item = itemView.findViewById(R.id.patient_list_patient_item);
      statusIndicator = itemView.findViewById(R.id.patient_status_indicator);
      actionButton = itemView.findViewById(R.id.patient_list_arrow);
      Log.d("PatientListAdapter", "Indicator loaded " + statusIndicator);
    }

    @SuppressLint("SetTextI18n")
    public void bind(final Patient patient, final boolean isArchivedList) {
      patient_name.setText(patient.getFirstName() + " " + patient.getLastName());

      if (isArchivedList) {
        actionButton.setOnClickListener(v -> restorePatient(patient));
      } else {
        actionButton.setOnClickListener(v -> viewPatientDetails(patient, context));
      }
    }

    private void restorePatient(Patient patient) {
      final DatabaseReference patientRef =
          FirebaseDatabase.getInstance()
              .getReference()
              .child("patients")
              .child(Objects.requireNonNull(patient.getPatientId()));
      patientRef.child("isArchived").setValue(false);
    }

    private void viewPatientDetails(Patient patient, Context context) {
      final Intent intent = new Intent(context, PatientProfileActivity.class);
      intent.putExtra("PATIENT_ID", patient.getPatientId());
      context.startActivity(intent);
    }
  }
}
