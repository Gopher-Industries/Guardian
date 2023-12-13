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
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.PatientListAdapter.MyViewHolder
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.PatientStatus
import deakin.gopher.guardian.view.general.PatientProfileActivity
import java.util.Objects

class PatientListAdapter(
    /**
     * Initialize a [RecyclerView.Adapter] that listens to a Firebase query. See [ ] for configuration options.
     */
    private val context: Context,
    options: FirebaseRecyclerOptions<Patient?>,
    private val isArchivedList: Boolean,
) : FirebaseRecyclerAdapter<Patient?, MyViewHolder?>(options) {
    override fun onDataChanged() {
        super.onDataChanged()
        if (itemCount == 0) {
            Log.d("PatientListAdapter", "No archived patients found.")
        } else {
            Log.d("PatientListAdapter", "Archived patients loaded: $itemCount")
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int,
        model: Patient,
    ) {
        holder.patientName.text = "${model.firstName} ${model.middleName} ${model.lastName}"
        updateStatusIndicator(holder.statusIndicator, model.status)
        holder.bind(model, isArchivedList)
        Log.d("PatientListAdapter", "Patient ID: " + model.patientId)
        holder.patientItem.setOnClickListener { view: View? ->
            val sharedPreferences = context.getSharedPreferences("MY_PREF", Context.MODE_PRIVATE)
            val role = sharedPreferences.getInt("login_role", -1)
            if (0 == role || -1 == role) {
                Log.d("PatientListAdapter", "Before toggle: " + model.status)
                model.examinePatient()
                Log.d("PatientListAdapter", "After toggle: " + model.status)
                updateStatusIndicator(holder.statusIndicator, model.status)
                notifyItemChanged(position)
                val intent = Intent(context, PatientProfileActivity::class.java)
                intent.putExtra("patientId", model.patientId)
                context.startActivity(intent)
                updatePatientStatus(model.patientId, model.status, model.needsAssistance)
            } else if (1 == role) {
                // admin
                val intent = Intent(context, PatientProfileActivity::class.java)
                intent.putExtra("id", model.patientId)
                context.startActivity(intent)
            }
        }
    }

    private fun updateStatusIndicator(
        statusIndicator: ImageView,
        status: PatientStatus,
    ) {
        val statusColor =
            if (PatientStatus.REQUIRES_ASSISTANCE === status) {
                ContextCompat.getColor(
                    context,
                    R.color.red,
                )
            } else {
                ContextCompat.getColor(context, R.color.green)
            }
        statusIndicator.setColorFilter(statusColor)
        val pulseAnimation =
            AnimationUtils.loadAnimation(
                context,
                R.anim.scale_animation,
            )
        statusIndicator.startAnimation(pulseAnimation)
    }

    private fun updatePatientStatus(
        patientId: String?,
        newStatus: PatientStatus,
        needsAssistance: Boolean,
    ) {
        val patientsRef = FirebaseDatabase.getInstance().getReference("patients")
        patientsRef
            .child(patientId!!)
            .child("status")
            .setValue(newStatus.toString())
            .addOnSuccessListener {
                Log.d(
                    "UpdateStatus",
                    "Patient status updated successfully.",
                )
            }
            .addOnFailureListener { e: Exception? ->
                Log.e(
                    "UpdateStatus",
                    "Failed to update patient status.",
                    e,
                )
            }
        patientsRef.child(patientId).child("needsAssistance").setValue(needsAssistance)
    }

    private fun getStatusColor(patient: Patient): Int {
        val oneHourMillis: Long = 60000
        val currentTime = System.currentTimeMillis()
        return if (currentTime - patient.lastExaminedTimestamp > oneHourMillis) {
            ContextCompat.getColor(context, R.color.orange)
        } else {
            if (PatientStatus.REQUIRES_ASSISTANCE ===
                Objects.requireNonNull(
                    patient.status,
                )
            ) {
                ContextCompat.getColor(context, R.color.red)
            } else {
                ContextCompat.getColor(context, R.color.green)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_patient_list_item, parent, false)
        return MyViewHolder(view, context)
    }

    fun deleteItem(position: Int) {
        val itemRef = getRef(position)
        itemRef
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    "Deleted successfully, click on archive to restore",
                    Toast.LENGTH_LONG,
                )
                    .show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    context,
                    "Error during deletion",
                    Toast.LENGTH_LONG,
                ).show()
            }
    }

    class MyViewHolder(itemView: View, private val context: Context) :
        RecyclerView.ViewHolder(itemView) {
        val patientName: TextView
        val patientItem: CardView
        val statusIndicator: ImageView
        private val actionButton: ImageView

        init {
            patientName = itemView.findViewById(R.id.patient_list_name)
            patientItem = itemView.findViewById(R.id.patient_list_patient_item)
            statusIndicator = itemView.findViewById(R.id.patient_status_indicator)
            actionButton = itemView.findViewById(R.id.patient_list_arrow)
            Log.d("PatientListAdapter", "Indicator loaded $statusIndicator")
        }

        @SuppressLint("SetTextI18n")
        fun bind(
            patient: Patient,
            isArchivedList: Boolean,
        ) {
            patientName.text = "${patient.getFirstName()} ${patient.getLastName()}"
            if (isArchivedList) {
                actionButton.setOnClickListener { v: View? -> restorePatient(patient) }
            } else {
                actionButton.setOnClickListener { v: View? -> viewPatientDetails(patient, context) }
            }
        }

        private fun restorePatient(patient: Patient) {
            val patientRef =
                patient.getPatientId()?.let {
                    FirebaseDatabase.getInstance()
                        .reference
                        .child("patients")
                        .child(it)
                }
            patientRef?.child("isArchived")?.setValue(false)
        }

        private fun viewPatientDetails(
            patient: Patient,
            context: Context,
        ) {
            val intent = Intent(context, PatientProfileActivity::class.java)
            intent.putExtra("PATIENT_ID", patient.getPatientId())
            context.startActivity(intent)
        }
    }
}
