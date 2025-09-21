package deakin.gopher.guardian.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import java.util.*

class CalendarGridAdapter(
    private var weekDays: List<Date>,
    private val timeSlots: List<String>,
    private val onSlotClick: (Date, String) -> Unit
) : RecyclerView.Adapter<CalendarGridAdapter.CalendarSlotViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarSlotViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_slot, parent, false)
        return CalendarSlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarSlotViewHolder, position: Int) {
        val dayIndex = position % 7
        val timeIndex = position / 7

        if (dayIndex < weekDays.size && timeIndex < timeSlots.size) {
            val date = weekDays[dayIndex]
            val time = timeSlots[timeIndex]
            holder.bind(date, time, onSlotClick)
        }
    }

    override fun getItemCount() = weekDays.size * timeSlots.size

    fun updateWeekDays(newWeekDays: List<Date>) {
        weekDays = newWeekDays
        notifyDataSetChanged()
    }

    class CalendarSlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(date: Date, time: String, onSlotClick: (Date, String) -> Unit) {
            itemView.setOnClickListener {
                onSlotClick(date, time)
            }
        }
    }
}