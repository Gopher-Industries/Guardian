package deakin.gopher.guardian.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Patient

class SimpleArchivedPatientAdapter(private val patients: List<Patient>) :
    RecyclerView.Adapter<SimpleArchivedPatientAdapter.PatientViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PatientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_patient_list_item, parent, false)
        return PatientViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PatientViewHolder,
        position: Int,
    ) {
        holder.bind(patients[position])
    }

    override fun getItemCount(): Int {
        return patients.size
    }

    class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val patientName: TextView = itemView.findViewById(R.id.patient_list_name)

        @SuppressLint("SetTextI18n")
        fun bind(patient: Patient) {
            // Set patient name
            patientName.text = "${patient.firstName} ${patient.lastName}"
        }
    }
}

