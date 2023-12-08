package deakin.gopher.guardian.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.model.Patient;
import java.util.List;

public class SimpleArchivedPatientAdapter
    extends RecyclerView.Adapter<SimpleArchivedPatientAdapter.PatientViewHolder> {

  private List<Patient> patients;

  public SimpleArchivedPatientAdapter(List<Patient> patients) {
    this.patients = patients;
  }

  @NonNull
  @Override
  public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext())
            .inflate(R.layout.activity_patient_list_item, parent, false);
    return new PatientViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
    holder.bind(patients.get(position));
  }

  @Override
  public int getItemCount() {
    return patients.size();
  }

  static class PatientViewHolder extends RecyclerView.ViewHolder {
    private TextView patient_name;
    final CardView patient_item;
    final ImageView statusIndicator;
    final ImageView actionButton;

    public PatientViewHolder(View itemView) {
      super(itemView);
      patient_name = itemView.findViewById(R.id.patient_list_name);
      patient_item = itemView.findViewById(R.id.patient_list_patient_item);
      statusIndicator = itemView.findViewById(R.id.patient_status_indicator);
      actionButton = itemView.findViewById(R.id.patient_list_arrow);
    }

    public void bind(Patient patient) {
      patient_name.setText(patient.getFirstName() + " " + patient.getLastName());
    }
  }
}
