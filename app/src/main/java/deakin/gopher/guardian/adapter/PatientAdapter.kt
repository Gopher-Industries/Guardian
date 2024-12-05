package deakin.gopher.guardian.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R

class PatientAdapter(private val patients: List<String>) :
    RecyclerView.Adapter<PatientAdapter.ViewHolder>() {

    // ViewHolder class to hold item views
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the item_patient layout for each item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_patient, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind data to the TextView in each item
        holder.textView.text = patients[position]
    }

    override fun getItemCount(): Int {
        // Return the total number of items
        return patients.size
    }
}
