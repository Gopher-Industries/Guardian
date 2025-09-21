package deakin.gopher.guardian.view.general

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Prescription

class PrescriptionAdapter(private var prescriptions: MutableList<Prescription>) :
    RecyclerView.Adapter<PrescriptionAdapter.PrescriptionViewHolder>() {
    inner class PrescriptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPatientName: TextView = itemView.findViewById(R.id.tvPatientName)
        val tvMedicine: TextView = itemView.findViewById(R.id.tvMedicine)
        val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PrescriptionViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_prescription, parent, false)
        return PrescriptionViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PrescriptionViewHolder,
        position: Int,
    ) {
        val prescription = prescriptions[position]
        holder.tvPatientName.text = "Patient ID: ${prescription.patientId}"
        if (prescription.items.isNotEmpty()) {
            val item = prescription.items[0]
            holder.tvMedicine.text = "${item.name} - ${item.dose}"
            holder.tvDuration.text = "Duration: ${item.durationDays} days"
        }
    }

    override fun getItemCount() = prescriptions.size

    // Renamed method to match activity
    fun setData(newData: List<Prescription>) {
        prescriptions.clear()
        prescriptions.addAll(newData)
        notifyDataSetChanged()
    }
}
