package deakin.gopher.guardian.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.CarePlan

class CarePlanAdapter(private val carePlans: List<CarePlan>) :
    RecyclerView.Adapter<CarePlanAdapter.CarePlanViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarePlanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.care_plan_item, parent, false)
        return CarePlanViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarePlanViewHolder, position: Int) {
        val carePlan = carePlans[position]
        holder.bind(carePlan)
    }

    override fun getItemCount(): Int = carePlans.size

    inner class CarePlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val patientName: TextView = itemView.findViewById(R.id.patientName)
        private val title: TextView = itemView.findViewById(R.id.carePlanTitle)
        private val assignedNurse: TextView = itemView.findViewById(R.id.assignedNurse)
        private val status: TextView = itemView.findViewById(R.id.carePlanStatus)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.carePlanProgressBar)

        fun bind(carePlan: CarePlan) {
            patientName.text = carePlan.patientId ?: "Unknown Patient"
            title.text = carePlan.title ?: "Untitled"
            assignedNurse.text = carePlan.assignedNurse ?: "Unassigned"
            status.text = "Status: ${carePlan.status ?: "Pending"}"
            progressBar.progress = carePlan.completionRate
        }

    }
}