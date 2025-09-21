package deakin.gopher.guardian.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import java.text.SimpleDateFormat
import java.util.*

class WeekCalendarAdapter(
    private var weekDays: List<Date>,
    private val onDateClick: (Date) -> Unit
) : RecyclerView.Adapter<WeekCalendarAdapter.WeekDayViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekDayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_week_day, parent, false)
        return WeekDayViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeekDayViewHolder, position: Int) {
        holder.bind(weekDays[position], onDateClick)
    }

    override fun getItemCount() = weekDays.size

    fun updateDays(newDays: List<Date>) {
        weekDays = newDays
        notifyDataSetChanged()
    }

    class WeekDayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayText: TextView = itemView.findViewById(R.id.day_text)
        private val dateText: TextView = itemView.findViewById(R.id.date_text)

        fun bind(date: Date, onDateClick: (Date) -> Unit) {
            val calendar = Calendar.getInstance().apply { time = date }

            // Set day name
            val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
            dayText.text = dayFormat.format(date).take(2)

            // Set date number
            dateText.text = calendar.get(Calendar.DAY_OF_MONTH).toString()

            itemView.setOnClickListener { onDateClick(date) }
        }
    }
}