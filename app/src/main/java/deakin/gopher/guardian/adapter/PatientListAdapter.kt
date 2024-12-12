package deakin.gopher.guardian.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.PatientStatus
import deakin.gopher.guardian.view.general.PatientProfileActivity
import deakin.gopher.guardian.view.patient.PatientOverviewActivity
import java.util.Objects

class PatientListAdapter(
    private val context: Context,
    private val patientList: MutableList<Patient>,  // Change this to MutableList
    private val isArchivedList: Boolean
) : RecyclerView.Adapter<PatientListAdapter.MyViewHolder>() {

    // Call this to update the list with new data
    fun updateData(newList: List<Patient>) {
        patientList.clear()  // This works now because the list is mutable
        patientList.addAll(newList)  // This also works with a mutable list
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        val model = patientList[position] // Use the correct position from the mutable list
        holder.patientName.text = "${model.firstName} ${model.middleName} ${model.lastName}"
        updateStatusIndicator(holder.statusIndicator, model.status)
        holder.bind(model, isArchivedList)
    }

    override fun getItemCount(): Int {
        return patientList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_patient_list_item, parent, false)
        return MyViewHolder(view, context)
    }

    // Method to update patient status when clicking
    private fun updatePatientStatus(patientId: String?, newStatus: PatientStatus, needsAssistance: Boolean) {
        // API call logic to update patient status (replace Firebase call)
    }

    private fun updateStatusIndicator(statusIndicator: ImageView, status: PatientStatus) {
        val statusColor = if (status == PatientStatus.REQUIRES_ASSISTANCE) {
            ContextCompat.getColor(context, R.color.red)
        } else {
            ContextCompat.getColor(context, R.color.green)
        }
        statusIndicator.setColorFilter(statusColor)
    }

    class MyViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {
        val patientName: TextView = itemView.findViewById(R.id.patient_list_name)
        val patientItem: CardView = itemView.findViewById(R.id.patient_list_patient_item)
        val statusIndicator: ImageView = itemView.findViewById(R.id.patient_status_indicator)
        private val actionButton: ImageView = itemView.findViewById(R.id.patient_list_arrow)

        init {
            Log.d("PatientListAdapter", "Indicator loaded $statusIndicator")
        }

        @SuppressLint("SetTextI18n")
        fun bind(patient: Patient, isArchivedList: Boolean) {
            patientName.text = "${patient.firstName} ${patient.lastName}"
            if (isArchivedList) {
                actionButton.setOnClickListener { restorePatient(patient) }
            } else {
                actionButton.setOnClickListener { viewPatientDetails(patient, context) }
            }
        }

        private fun restorePatient(patient: Patient) {
            // API logic to restore patient (replace Firebase code)
        }

        private fun viewPatientDetails(patient: Patient, context: Context) {
            val intent = Intent(context, PatientProfileActivity::class.java)
            intent.putExtra("PATIENT_ID", patient.patientId) // Directly access the property
            context.startActivity(intent)
        }
    }
}
