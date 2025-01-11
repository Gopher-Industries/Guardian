package deakin.gopher.guardian.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.SimpleArchivedPatientAdapter.PatientViewHolder
import deakin.gopher.guardian.model.Patient

class SimpleArchivedPatientAdapter(private val patients: List<Patient>) :
    RecyclerView.Adapter<PatientViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_patient_list_item, parent, false)
        return PatientViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        holder.bind(patients[position])
    }

    override fun getItemCount(): Int {
        return patients.size
    }

    class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val patientName: TextView
        private val patientItem: CardView
        private val statusIndicator: ImageView
        private val actionButton: ImageView

        init {
            patientName = itemView.findViewById(R.id.patient_list_name)
            patientItem = itemView.findViewById(R.id.patient_list_patient_item)
            statusIndicator = itemView.findViewById(R.id.patient_status_indicator)
            actionButton = itemView.findViewById(R.id.patient_list_arrow)
        }

        @SuppressLint("SetTextI18n")
        fun bind(patient: Patient) {
            patientName.text = "${patient.firstName} ${patient.lastName}"
        }
    }
}

