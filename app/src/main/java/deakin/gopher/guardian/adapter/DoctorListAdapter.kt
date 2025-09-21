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
    private val onAssign: (Doctor) -> Unit,
    private val onRequest: (Doctor) -> Unit,
    private val onUnassign: (Doctor) -> Unit,
    private val onCancelRequest: (Doctor) -> Unit,
) : RecyclerView.Adapter<DoctorListAdapter.DoctorVH>() {
    private var assignedDoctorId: String? = null
    private var requestedDoctorIds: Set<String> = emptySet()

    inner class DoctorVH(view: View) : RecyclerView.ViewHolder(view) {
        val photo: ImageView = view.findViewById(R.id.ivPhoto)
        val name: TextView = view.findViewById(R.id.tvName)
        val specialty: TextView = view.findViewById(R.id.tvSpecialty)
        val btnAssign: Button = view.findViewById(R.id.btnAssign)
        val btnRequest: Button = view.findViewById(R.id.btnRequest)
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

        val isAssignedToThis = (assignedDoctorId != null && d.id == assignedDoctorId)
        val isRequested = requestedDoctorIds.contains(d.id)

        if (isAssignedToThis) {
            holder.btnAssign.text = "Unassign"
            holder.btnAssign.isEnabled = true
            holder.btnAssign.setOnClickListener { onUnassign(d) }
        } else {
            holder.btnAssign.text = "Assign"
            holder.btnAssign.isEnabled = true
            holder.btnAssign.setOnClickListener { onAssign(d) }
        }
//
//        holder.btnAssign.isEnabled = true
//        holder.btnAssign.text = holder.itemView.context.getString(R.string.assign)
//        holder.btnRequest.isEnabled = true
//        holder.btnRequest.text = holder.itemView.context.getString(R.string.request)

        if (isAssignedToThis) {
            // If already assigned, we typically disable request
            holder.btnRequest.text = "Request"
            holder.btnRequest.isEnabled = false
            holder.btnRequest.setOnClickListener(null)
        } else if (isRequested) {
            holder.btnRequest.text = "Cancel"
            holder.btnRequest.isEnabled = true
            holder.btnRequest.setOnClickListener { onCancelRequest(d) }
        } else {
            holder.btnRequest.text = "Request"
            holder.btnRequest.isEnabled = true
            holder.btnRequest.setOnClickListener { onRequest(d) }
        }
    }

//        if (assignedDoctorId != null && d.id == assignedDoctorId) {
//            holder.btnAssign.isEnabled = false
//            holder.btnAssign.text = holder.itemView.context.getString(R.string.assigned)
//
//            holder.btnRequest.isEnabled = false
//        } else if (requestedDoctorIds.contains(d.id)) {
//            holder.btnRequest.isEnabled = false
//            holder.btnRequest.text = holder.itemView.context.getString(R.string.requested)
//        }
//
//        holder.btnAssign.setOnClickListener { onAssign(d) }
//        holder.btnRequest.setOnClickListener { onRequest(d) }
//    }

    override fun getItemCount(): Int = doctors.size

    fun updateData(newDoctors: List<Doctor>) {
        doctors = newDoctors
        notifyDataSetChanged()
    }

    fun updateState(
        assignedDoctorId: String?,
        requestedDoctorIds: Set<String>,
    ) {
        this.assignedDoctorId = assignedDoctorId
        this.requestedDoctorIds = requestedDoctorIds
        notifyDataSetChanged()
    }
}
