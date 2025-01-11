package deakin.gopher.guardian.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.PatientStatus
import deakin.gopher.guardian.view.general.PatientProfileActivity
import deakin.gopher.guardian.view.patient.PatientOverviewActivity

class PatientListAdapter(
    private val context: Context,
    private var patientList: MutableList<Patient>,
    private val isArchivedList: Boolean,
) : RecyclerView.Adapter<PatientListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_patient_list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val patient = patientList[position]
        holder.patientName.text = "${patient.firstName.orEmpty()} ${patient.middleName.orEmpty()} ${patient.lastName.orEmpty()}"
        updateStatusIndicator(holder.statusIndicator, patient.status)

        // Handle item click (existing functionality)
        holder.patientItem.setOnClickListener {
            val patientId = patient.patientId ?: return@setOnClickListener
            val intent = Intent(context, PatientProfileActivity::class.java).apply {
                putExtra("patientId", patientId)
                putExtra("firstName", patient.firstName)
                putExtra("middleName", patient.middleName)
                putExtra("lastName", patient.lastName)
                putExtra("dob", patient.dob)
                putExtra("medicareNo", patient.medicareNo)
                putExtra("westernAffairsNo", patient.westernAffairsNo)
            }
            context.startActivity(intent)
        }

        // Handle "View" button click
        holder.viewButton.setOnClickListener {
            val patientId = patient.patientId ?: return@setOnClickListener
            val intent = Intent(context, PatientOverviewActivity::class.java).apply {
                putExtra("patientId", patientId)
                putExtra("firstName", patient.firstName)
                putExtra("middleName", patient.middleName)
                putExtra("lastName", patient.lastName)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = patientList.size

    fun updateData(newData: List<Patient>) {
        patientList.clear()
        patientList.addAll(newData)
        notifyDataSetChanged()
    }

    private fun updateStatusIndicator(statusIndicator: ImageView, status: PatientStatus?) {
        val statusColor = if (status == PatientStatus.REQUIRES_ASSISTANCE) {
            ContextCompat.getColor(context, R.color.red)
        } else {
            ContextCompat.getColor(context, R.color.green)
        }
        statusIndicator.setColorFilter(statusColor)
        val pulseAnimation = AnimationUtils.loadAnimation(context, R.anim.scale_animation)
        statusIndicator.startAnimation(pulseAnimation)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val patientName: TextView = itemView.findViewById(R.id.patient_list_name)
        val patientItem: CardView = itemView.findViewById(R.id.patient_list_patient_item)
        val statusIndicator: ImageView = itemView.findViewById(R.id.patient_status_indicator)
        val viewButton: Button = itemView.findViewById(R.id.button_view_patient) // Add the "View" button
    }
}
