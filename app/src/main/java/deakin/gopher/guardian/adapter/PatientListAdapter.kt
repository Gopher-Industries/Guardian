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
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.PatientStatus
import deakin.gopher.guardian.view.general.PatientProfileActivity
import java.util.Objects



class PatientListAdapter(
    private val context: Context,
    options: FirebaseRecyclerOptions<Patient>,
    private val isArchivedList: Boolean,
) : FirebaseRecyclerAdapter<Patient, PatientListAdapter.MyViewHolder>(options) {

    // This method is required by FirebaseRecyclerAdapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // Inflate the item layout (the layout for each patient item)
        val view = LayoutInflater.from(context).inflate(R.layout.item_patient, parent, false)

        // Return the ViewHolder with the inflated view
        return MyViewHolder(view)
    }

    override fun onDataChanged() {
        super.onDataChanged()
        if (itemCount == 0) {
            Log.d("PatientListAdapter", "No patients found.")
        } else {
            Log.d("PatientListAdapter", "Patients loaded: $itemCount")
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int,
        model: Patient,
    ) {
        // Ensure model.firstName, model.middleName, and model.lastName are not null
        val fullName = "${model.firstName ?: ""} ${model.middleName ?: ""} ${model.lastName ?: ""}".trim()
        holder.patientName.text = fullName

        // Update status indicator based on patient status
        updateStatusIndicator(holder.statusIndicator, model.status)

        Log.d("PatientListAdapter", "Patient ID: ${model.patientId}")

        // On click action for patient item
        holder.patientItem.setOnClickListener {
            val sharedPreferences = context.getSharedPreferences("MY_PREF", Context.MODE_PRIVATE)
            val role = sharedPreferences.getInt("login_role", -1)

            val intent = Intent(context, PatientProfileActivity::class.java)
            if (role == 0 || role == -1) {
                Log.d("PatientListAdapter", "Before toggle: ${model.status}")
                model.examinePatient()
                Log.d("PatientListAdapter", "After toggle: ${model.status}")

                updateStatusIndicator(holder.statusIndicator, model.status)

                intent.putExtra("patientId", model.patientId)
                context.startActivity(intent)

                updatePatientStatus(model.patientId, model.status, model.needsAssistance)

            } else if (role == 1) {
                intent.putExtra("patientId", model.patientId)
                context.startActivity(intent)
            }
        }

        // Action button logic based on archived list
        if (isArchivedList) {
            holder.actionButton.setOnClickListener {
                restorePatient(model)
            }
        } else {
            holder.actionButton.setOnClickListener {
                viewPatientDetails(model, context)
            }
        }
    }

    // Update the status indicator color and animation based on patient status
    private fun updateStatusIndicator(
        statusIndicator: ImageView,
        status: PatientStatus?,
    ) {
        val statusColor = when (status) {
            PatientStatus.REQUIRES_ASSISTANCE -> ContextCompat.getColor(context, R.color.red)
            PatientStatus.NEEDS_CHECKUP -> ContextCompat.getColor(context, R.color.orange)
            PatientStatus.OK -> ContextCompat.getColor(context, R.color.green)
            else -> ContextCompat.getColor(context, R.color.green)
        }
        statusIndicator.setColorFilter(statusColor)

        if (status == PatientStatus.REQUIRES_ASSISTANCE || status == PatientStatus.NEEDS_CHECKUP) {
            val pulseAnimation = AnimationUtils.loadAnimation(context, R.anim.scale_animation)
            statusIndicator.startAnimation(pulseAnimation)
        } else {
            statusIndicator.clearAnimation()
        }
    }

    // Update the status and needsAssistance field in Firebase
    private fun updatePatientStatus(
        patientId: String?,
        newStatus: PatientStatus?,
        needsAssistance: Boolean?,
    ) {
        if (patientId == null) {
            Log.e("UpdateStatus", "Patient ID is null, cannot update status.")
            return
        }

        val patientsRef = FirebaseDatabase.getInstance().getReference("patients")
        val patientRef = patientsRef.child(patientId)

        newStatus?.let {
            patientRef.child("status").setValue(it.toString())
                .addOnSuccessListener {
                    Log.d("UpdateStatus", "Patient status updated successfully to $it.")
                }
                .addOnFailureListener { e: Exception ->
                    Log.e("UpdateStatus", "Failed to update patient status.", e)
                }
        }

        needsAssistance?.let {
            patientRef.child("needsAssistance").setValue(it)
                .addOnSuccessListener {
                    Log.d("UpdateStatus", "Patient needsAssistance updated successfully to $it.")
                }
                .addOnFailureListener { e: Exception ->
                    Log.e("UpdateStatus", "Failed to update patient needsAssistance.", e)
                }
        }
    }

    // Optional methods for patient restoration or viewing patient details
    private fun restorePatient(patient: Patient) {
        // Implementation for restoring a patient
    }

    private fun viewPatientDetails(patient: Patient, context: Context) {
        // Implementation for viewing patient details
    }

    // ViewHolder inner class to bind views
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val patientName: TextView = view.findViewById(R.id.patientName)
        val statusIndicator: ImageView = view.findViewById(R.id.statusIndicator)
        val patientItem: CardView = view.findViewById(R.id.patientItem)
        val actionButton: ImageView = view.findViewById(R.id.actionButton)
    }
}
