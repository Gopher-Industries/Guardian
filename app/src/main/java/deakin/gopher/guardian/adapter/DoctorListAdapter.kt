package deakin.gopher.guardian.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Doctor

class DoctorListAdapter(
    private var doctors: List<Doctor>,
    private val onSelect: (Doctor) -> Unit,
) : RecyclerView.Adapter<DoctorListAdapter.DoctorVH>() {
    inner class DoctorVH(view: View) : RecyclerView.ViewHolder(view) {
        val photo: ImageView = view.findViewById(R.id.ivPhoto)
        val name: TextView = view.findViewById(R.id.tvName)
        val specialty: TextView = view.findViewById(R.id.tvSpecialty)
        val btnAssign: Button = view.findViewById(R.id.btnAssign)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): DoctorVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_doctor, parent, false)
        return DoctorVH(view)
    }

    override fun onBindViewHolder(
        holder: DoctorVH,
        position: Int,
    ) {
        val d = doctors[position]
        holder.name.text = d.fullname
        holder.specialty.text = d.specialty?.let { "Specialty: $it" } ?: "Specialty: —"

        Glide.with(holder.itemView.context)
            .load(d.photoUrl)
            .placeholder(R.drawable.profile)
            .circleCrop()
            .into(holder.photo)

        holder.itemView.setOnClickListener { onSelect(d) }
        holder.btnAssign.setOnClickListener { onSelect(d) }
    }

    override fun getItemCount(): Int = doctors.size

    fun updateData(newDoctors: List<Doctor>) {
        doctors = newDoctors
        notifyDataSetChanged()
    }
}
