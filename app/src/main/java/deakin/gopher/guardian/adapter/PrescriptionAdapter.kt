package deakin.gopher.guardian.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.databinding.ItemPrescriptionBinding
import deakin.gopher.guardian.model.Prescription
import java.util.Locale

class PrescriptionAdapter(
    private var prescriptions: List<Prescription>,
    private val showEditButton: Boolean,
    private val onEditClick: (Prescription) -> Unit
) : RecyclerView.Adapter<PrescriptionAdapter.PrescriptionViewHolder>() {

    inner class PrescriptionViewHolder(
        private val binding: ItemPrescriptionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(prescription: Prescription) {
            val firstItem = prescription.items.firstOrNull()

            binding.tvPrescriptionTitle.text = firstItem?.name ?: "Prescription"

            binding.tvPrescriptionItems.text = if (prescription.items.isNotEmpty()) {
                prescription.items.joinToString("\n") { item ->
                    "• ${item.name} - ${item.dose}, ${item.frequency}, for ${item.durationDays} days"
                }
            } else {
                "No medicine details available"
            }

            binding.tvPrescriptionStatus.text = "Status: ${
                prescription.status?.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                } ?: "Unknown"
            }"

            binding.tvPrescriptionDate.text = "Created: ${
                prescription.createdAt?.substringBefore("T") ?: "N/A"
            }"

            binding.btnEditPrescription.visibility = if (showEditButton) {
                View.VISIBLE
            } else {
                View.GONE
            }

            binding.btnEditPrescription.setOnClickListener {
                onEditClick(prescription)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrescriptionViewHolder {
        val binding = ItemPrescriptionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PrescriptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PrescriptionViewHolder, position: Int) {
        holder.bind(prescriptions[position])
    }

    override fun getItemCount(): Int = prescriptions.size

    fun updateData(newPrescriptions: List<Prescription>) {
        prescriptions = newPrescriptions
        notifyDataSetChanged()
    }
}