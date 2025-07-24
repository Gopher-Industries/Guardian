package deakin.gopher.guardian.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.register.User

class NurseListAdapter(
    private var nurses: List<User>,
    private val onNurseClick: ((User) -> Unit)? = null,
) : RecyclerView.Adapter<NurseListAdapter.NurseViewHolder>() {
    inner class NurseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.tvName)
        val emailText: TextView = itemView.findViewById(R.id.tvEmail)
        val image: ImageView = itemView.findViewById(R.id.image)
        val card: CardView = itemView.findViewById(R.id.card)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): NurseViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_nurse, parent, false)
        return NurseViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: NurseViewHolder,
        position: Int,
    ) {
        val nurse = nurses[position]
        holder.nameText.text = nurse.name
        holder.emailText.text = nurse.email

        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(nurse.photoUrl)
            .placeholder(R.drawable.profile_user)
            .into(holder.image)

        holder.card.setOnClickListener {
            onNurseClick?.invoke(nurse)
        }
    }

    override fun getItemCount(): Int = nurses.size

    fun updateData(newNurses: List<User>) {
        nurses = newNurses
        notifyDataSetChanged()
    }
}
