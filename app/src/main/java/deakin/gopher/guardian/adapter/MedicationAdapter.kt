package deakin.gopher.guardian.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Medication

class MedicationAdapter(private val medications: List<Medication>) : RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MedicationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_medicine, parent, false)
        return MedicationViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MedicationViewHolder,
        position: Int,
    ) {
        val medication = medications[position]
        holder.bind(medication)
    }

    override fun getItemCount(): Int = medications.size

    inner class MedicationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val medicationNameTextView: TextView = itemView.findViewById(R.id.medicineNameTextView)

        fun bind(medication: Medication) {
            medicationNameTextView.text = medication.name
        }
    }
}
