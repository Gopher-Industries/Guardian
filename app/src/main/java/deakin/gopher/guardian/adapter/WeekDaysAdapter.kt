package deakin.gopher.guardian.adapter



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.util.CalendarUtils
import java.text.SimpleDateFormat
import java.util.*

class WeekDaysAdapter(
    private var weekDays: List<Date>,
    private val onDateClick: (Date) -> Unit
) : RecyclerView.Adapter<WeekDaysAdapter.WeekDayViewHolder>() {

    private var selectedPosition = -1

    init {
        // Find today's position if it's in the current week
        weekDays.forEachIndexed { index, date ->
            if (CalendarUtils.isToday(date)) {
                selectedPosition = index
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekDayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_week_day, parent, false)
        return WeekDayViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeekDayViewHolder, position: Int) {
        holder.bind(weekDays[position], position == selectedPosition) {
            val oldPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(oldPosition)
            notifyItemChanged(selectedPosition)
            onDateClick(weekDays[position])
        }
    }

    override fun getItemCount() = weekDays.size

    fun updateDays(newDays: List<Date>) {
        weekDays = newDays
        // Reset selection to today if present
        selectedPosition = -1
        newDays.forEachIndexed { index, date ->
            if (CalendarUtils.isToday(date)) {
                selectedPosition = index
            }
        }
        notifyDataSetChanged()
    }

    class WeekDayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayText: TextView = itemView.findViewById(R.id.day_text)
        private val dateText: TextView = itemView.findViewById(R.id.date_text)
        private val container: View = itemView.findViewById(R.id.day_container)

        fun bind(date: Date, isSelected: Boolean, onClick: () -> Unit) {
            val calendar = Calendar.getInstance().apply { time = date }

            // Set day name (M, Tu, W, etc.)
            val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
            val dayName = dayFormat.format(date)
            dayText.text = when (dayName) {
                "Mon" -> "M"
                "Tue" -> "Tu"
                "Wed" -> "W"
                "Thu" -> "Th"
                "Fri" -> "F"
                "Sat" -> "Sa"
                "Sun" -> "Su"
                else -> dayName.take(2)
            }

            // Set date number
            dateText.text = calendar.get(Calendar.DAY_OF_MONTH).toString()

            // Handle selection state
            if (isSelected) {
                container.setBackgroundResource(R.drawable.selected_day_background)
                dayText.setTextColor(itemView.context.getColor(android.R.color.white))
                dateText.setTextColor(itemView.context.getColor(android.R.color.white))
            } else {
                container.setBackgroundResource(R.drawable.unselected_day_background)
                dayText.setTextColor(itemView.context.getColor(R.color.text_secondary))
                dateText.setTextColor(itemView.context.getColor(R.color.text_primary))
            }

            container.setOnClickListener { onClick() }
        }
    }
}