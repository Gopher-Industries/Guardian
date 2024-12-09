package deakin.gopher.guardian.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R

class CarePlanProgressAdapter(private val progressList: List<String>) : RecyclerView.Adapter<CarePlanProgressAdapter.CarePlanProgressViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarePlanProgressViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_care_plan_progress, parent, false)
        return CarePlanProgressViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarePlanProgressViewHolder, position: Int) {
        holder.bind(progressList[position])
    }

    override fun getItemCount(): Int = progressList.size

    inner class CarePlanProgressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val progressText: TextView = itemView.findViewById(R.id.progress_text)

        fun bind(progress: String) {
            progressText.text = progress
        }
    }
}
