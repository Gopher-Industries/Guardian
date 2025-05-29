package deakin.gopher.guardian.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.PatientActivity
import deakin.gopher.guardian.util.DateUtils

class PatientActivityAdapter(
    private var reports: List<PatientActivity>,
) : RecyclerView.Adapter<PatientActivityAdapter.PatientActivityViewHolder>() {
    inner class PatientActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvActivityName: TextView = itemView.findViewById(R.id.tvActivityName)
        val tvTimestampLogger: TextView = itemView.findViewById(R.id.tvTimestampLogger)
        val tvComment: TextView = itemView.findViewById(R.id.tvComment)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PatientActivityViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_patient_activity, parent, false)
        return PatientActivityViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PatientActivityViewHolder,
        position: Int,
    ) {
        val activity = reports[position]
        holder.tvActivityName.text =
            activity.activityName.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
            }
        holder.tvTimestampLogger.text =
            "${DateUtils.getRelativeDayWithTime(activity.timestamp)} - Logged by ${activity.loggedBy}"
        holder.tvComment.text = activity.comment
    }

    override fun getItemCount(): Int = reports.size

    fun updateData(newReports: List<PatientActivity>) {
        reports = newReports
        notifyDataSetChanged()
    }
}
