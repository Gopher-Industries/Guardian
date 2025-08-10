package deakin.gopher.guardian.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.ExerciseItem

class ExerciseAdapter(
    private var items: List<ExerciseItem>,
    private val onClick: (ExerciseItem) -> Unit,
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {
    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: CardView = itemView.findViewById(R.id.exerciseCard)
        val title: TextView = itemView.findViewById(R.id.exerciseTitle)
        val icon: ImageView = itemView.findViewById(R.id.exerciseIcon)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise_card, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ExerciseViewHolder,
        position: Int,
    ) {
        val item = items[position]
        holder.title.text = item.title
        holder.icon.setImageResource(item.iconRes)
        holder.card.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<ExerciseItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
