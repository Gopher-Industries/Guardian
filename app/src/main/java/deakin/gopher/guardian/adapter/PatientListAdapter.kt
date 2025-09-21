package deakin.gopher.guardian.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Patient

class PatientListAdapter(
    private var patients: List<Patient>,
    private val onPatientClick: ((Patient) -> Unit)? = null,
    private val onAssignNurseClick: ((Patient) -> Unit)? = null,
    private val onAssignDoctorClick: ((Patient) -> Unit)? = null,
    private val onDeleteClick: ((Patient) -> Unit)? = null,
) : RecyclerView.Adapter<PatientListAdapter.PatientViewHolder>() {
    inner class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.tvName)
        val ageText: TextView = itemView.findViewById(R.id.tvAge)
        val genderText: TextView = itemView.findViewById(R.id.tvGender)
        val image: ImageView = itemView.findViewById(R.id.imagePatient)
        val card: CardView = itemView.findViewById(R.id.card)
        val moreIcon: ImageView = itemView.findViewById(R.id.ivMore)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PatientViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_patient, parent, false)
        return PatientViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PatientViewHolder,
        position: Int,
    ) {
        val patient = patients[position]
        holder.nameText.text = patient.fullname
        holder.ageText.text = "Age: ${patient.age}"
        holder.genderText.text = "Gender: ${
            patient.gender.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
            }
        }"

        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(patient.photoUrl)
            .placeholder(R.drawable.profile)
            .circleCrop()
            .into(holder.image)

        holder.card.setOnClickListener {
            onPatientClick?.invoke(patient)
        }

        holder.moreIcon.setOnClickListener {
            val popupMenu = PopupMenu(holder.itemView.context, it)
            popupMenu.inflate(R.menu.menu_patient_item)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.assign_nurse -> {
                        onAssignNurseClick?.invoke(patient)
                        true
                    }
                    R.id.assign_doctor -> {
                        onAssignDoctorClick?.invoke(patient)
                        true
                    }
                    R.id.action_delete -> { // Handle delete click
                        onDeleteClick?.invoke(patient)
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }
    }

    override fun getItemCount(): Int = patients.size

    fun updateData(newPatients: List<Patient>) {
        patients = newPatients
        notifyDataSetChanged()
    }
}
