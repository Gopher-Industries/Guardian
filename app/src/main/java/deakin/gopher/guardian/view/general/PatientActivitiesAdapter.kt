package deakin.gopher.guardian.view.general

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.PatientActivity

class PatientActivitiesAdapter(
    private var activities: List<PatientActivity>,
    private val onDeleteClick: (PatientActivity) -> Unit
) : RecyclerView.Adapter<PatientActivitiesAdapter.ActivityViewHolder>() {

    class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val activityName: TextView = itemView.findViewById(R.id.tvActivityName)
        val timestampLogger: TextView = itemView.findViewById(R.id.tvTimestampLogger)
        val comment: TextView = itemView.findViewById(R.id.tvComment)
        val deleteButton: Button = itemView.findViewById(R.id.deleteActivityButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_patient_activity, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = activities[position]

        holder.activityName.text = activity.activityName
        holder.timestampLogger.text = "${activity.timestamp} - Logged by ${activity.loggedBy}"
        holder.comment.text = activity.comment

        holder.deleteButton.setOnClickListener {
            onDeleteClick(activity)
        }
    }

    override fun getItemCount(): Int = activities.size

    fun updateActivities(newActivities: List<PatientActivity>) {
        activities = newActivities
        notifyDataSetChanged()
    }
}