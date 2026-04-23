package deakin.gopher.guardian.adapter

import deakin.gopher.guardian.util.CalendarUtils


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.calendar.TaskResponse

import java.text.SimpleDateFormat
import java.util.*

class TaskEventsAdapter(
    private val onTaskClick: (TaskResponse) -> Unit,
    private val onTaskComplete: (String) -> Unit,
    private val onTaskEdit: (TaskResponse) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var displayItems = mutableListOf<DisplayItem>()

    companion object {
        const val TYPE_DATE_HEADER = 0
        const val TYPE_TASK_ITEM = 1
        const val TYPE_NO_EVENTS = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (displayItems[position]) {
            is DisplayItem.DateHeader -> TYPE_DATE_HEADER
            is DisplayItem.TaskItem -> TYPE_TASK_ITEM
            is DisplayItem.NoEvents -> TYPE_NO_EVENTS
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_DATE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_date_header, parent, false)
                DateHeaderViewHolder(view)
            }
            TYPE_TASK_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_task_event, parent, false)
                TaskEventViewHolder(view)
            }
            TYPE_NO_EVENTS -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_no_events, parent, false)
                NoEventsViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = displayItems[position]) {
            is DisplayItem.DateHeader -> {
                (holder as DateHeaderViewHolder).bind(item.date)
            }
            is DisplayItem.TaskItem -> {
                (holder as TaskEventViewHolder).bind(
                    item.task,
                    onTaskClick,
                    onTaskComplete,
                    onTaskEdit
                )
            }
            is DisplayItem.NoEvents -> {
                // No binding needed
            }
        }
    }

    override fun getItemCount() = displayItems.size

    fun updateTasks(tasks: List<TaskResponse>) {
        displayItems.clear()

        if (tasks.isEmpty()) {
            displayItems.add(DisplayItem.NoEvents)
            notifyDataSetChanged()
            return
        }

        // Group tasks by date and sort
        val tasksByDate = tasks.groupBy { task ->
            task.dueDate?.let {
                val date = CalendarUtils.parseDateString(it)
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
            } ?: "unknown"
        }.toSortedMap()

        tasksByDate.forEach { (dateString, tasksForDate) ->
            if (dateString != "unknown") {
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString) ?: Date()
                displayItems.add(DisplayItem.DateHeader(date))

                // Sort tasks by time for the day
                val sortedTasks = tasksForDate.sortedBy { task ->
                    task.dueDate?.let { CalendarUtils.parseDateString(it) } ?: Date(0)
                }

                sortedTasks.forEach { task ->
                    displayItems.add(DisplayItem.TaskItem(task))
                }
            }
        }

        notifyDataSetChanged()
    }

    sealed class DisplayItem {
        data class DateHeader(val date: Date) : DisplayItem()
        data class TaskItem(val task: TaskResponse) : DisplayItem()
        object NoEvents : DisplayItem()
    }

    class DateHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateText: TextView = itemView.findViewById(R.id.date_text)

        fun bind(date: Date) {
            dateText.text = CalendarUtils.formatDateHeader(date)
        }
    }

    class TaskEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timeText: TextView = itemView.findViewById(R.id.time_text)
        private val durationText: TextView = itemView.findViewById(R.id.duration_text)
        private val titleText: TextView = itemView.findViewById(R.id.title_text)
        private val descriptionText: TextView = itemView.findViewById(R.id.description_text)
        private val priorityIndicator: View = itemView.findViewById(R.id.priority_indicator)
        private val statusIcon: ImageView = itemView.findViewById(R.id.status_icon)
        private val patientBadge: TextView = itemView.findViewById(R.id.patient_badge)
        private val completeButton: ImageButton = itemView.findViewById(R.id.complete_button)
        private val editButton: ImageButton = itemView.findViewById(R.id.edit_button)

        fun bind(
            task: TaskResponse,
            onTaskClick: (TaskResponse) -> Unit,
            onTaskComplete: (String) -> Unit,
            onTaskEdit: (TaskResponse) -> Unit
        ) {
            // Time formatting
            val time = task.dueDate?.let {
                val date = CalendarUtils.parseDateString(it)
                SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
            } ?: "No time"

            timeText.text = time
            durationText.text = "1h" // You can calculate or store duration
            titleText.text = task.title

            // Show description if available and different from title
            if (task.description.isNotBlank() && task.description != task.title) {
                descriptionText.text = task.description
                descriptionText.visibility = View.VISIBLE
            } else {
                descriptionText.visibility = View.GONE
            }

            // Priority indicator
            val priorityColor = when (task.priority.lowercase()) {
                "urgent", "high" -> itemView.context.getColor(R.color.priority_high)
                "medium" -> itemView.context.getColor(R.color.priority_medium)
                "low" -> itemView.context.getColor(R.color.priority_low)
                else -> itemView.context.getColor(R.color.priority_medium)
            }
            priorityIndicator.setBackgroundColor(priorityColor)

            // Status icon and styling
            val isCompleted = task.status.lowercase() == "completed"
            when (task.status.lowercase()) {
                "completed" -> {
                    statusIcon.setImageResource(R.drawable.ic_check_circle)
                    statusIcon.setColorFilter(itemView.context.getColor(R.color.success_color))
                    itemView.alpha = 0.7f
                }
                "in_progress" -> {
                    statusIcon.setImageResource(R.drawable.ic_in_progress)
                    statusIcon.setColorFilter(itemView.context.getColor(R.color.warning_color))
                    itemView.alpha = 1.0f
                }
                else -> {
                    statusIcon.setImageResource(R.drawable.ic_pending)
                    statusIcon.setColorFilter(itemView.context.getColor(R.color.text_secondary))
                    itemView.alpha = 1.0f
                }
            }

            // Patient badge
            if (task.patientId != null) {
                patientBadge.visibility = View.VISIBLE
                patientBadge.text = "Patient"
            } else {
                patientBadge.visibility = View.GONE
            }

            // Action buttons
            completeButton.visibility = if (isCompleted) View.GONE else View.VISIBLE
            completeButton.setOnClickListener { onTaskComplete(task.id) }

            editButton.setOnClickListener { onTaskEdit(task) }

            // Click listeners
            itemView.setOnClickListener { onTaskClick(task) }
        }
    }

    class NoEventsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}