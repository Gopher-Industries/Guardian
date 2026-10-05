package deakin.gopher.guardian.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Task
import deakin.gopher.guardian.view.general.TaskDetailActivity

class TaskListAdapter(
    private var tasks: MutableList<Task>,
    private val onStatusUpdate: ((task: Task, newStatus: String, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit)? = null,
) : RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {
    init {
        Log.d("TaskListAdapter", "Task Data Size: ${tasks.size}")
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TaskViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.task_list_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TaskViewHolder,
        position: Int,
    ) {
        holder.bind(tasks[position])
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView? = itemView.findViewById(R.id.task_title_text_view)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.task_description_text_view)
        private val dueDateTextView: TextView? = itemView.findViewById(R.id.task_due_date_text_view)
        private val priorityChip: TextView? = itemView.findViewById(R.id.task_priority_chip)
        private val statusChip: TextView? = itemView.findViewById(R.id.task_status_chip)
        private val viewTaskDetailsButton: Button = itemView.findViewById(R.id.task_description_button)
        private val taskCompletedIcon: ImageView = itemView.findViewById(R.id.task_completed_icon)
        private val statusControlsLayout: LinearLayout? = itemView.findViewById(R.id.status_controls_layout)
        private val btnPending: Button? = itemView.findViewById(R.id.btn_status_pending)
        private val btnCompleted: Button? = itemView.findViewById(R.id.btn_status_completed)
        private val btnEscalated: Button? = itemView.findViewById(R.id.btn_status_escalated)
        private val itemProgressBar: ProgressBar? = itemView.findViewById(R.id.item_progress_bar)

        init {
            viewTaskDetailsButton.setOnClickListener {
                val context = itemView.context
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION && position < tasks.size) {
                    val task = tasks[position]
                    val intent = Intent(context, TaskDetailActivity::class.java)
                    intent.putExtra("taskId", task.taskId)
                    context.startActivity(intent)
                }
            }
        }

        fun bind(task: Task) {
            val context = itemView.context

            // Title and Description
            val displayTitle = task.title.takeIf { !it.isNullMonitoredBlank() } ?: task.description.take(30)
            titleTextView?.text = if (displayTitle.isNotBlank()) displayTitle else "Task #${task.taskId.takeLast(6)}"
            descriptionTextView.text = task.description

            // Priority
            val priority = task.priority.name
            priorityChip?.text = priority
            val priorityColor =
                when (priority.uppercase()) {
                    "HIGH" -> ContextCompat.getColor(context, R.color.colorRed)
                    "LOW" -> ContextCompat.getColor(context, R.color.colorGreen)
                    else -> ContextCompat.getColor(context, R.color.colorOrange)
                }
            priorityChip?.setBackgroundColor(priorityColor)

            // Due Date
            val dueDateText =
                task.dueDate?.let { "Due: $it" }
                    ?: task.patientId?.let { if (it.isNotBlank()) "Patient: $it" else null }
                    ?: "Daily Task"
            dueDateTextView?.text = dueDateText

            // Status Badge & Controls
            val currentStatus = task.status?.lowercase() ?: if (task.completed) "completed" else "pending"
            updateStatusUi(currentStatus)

            // Button Click Listeners for Status
            btnPending?.setOnClickListener { performStatusUpdate(task, "pending") }
            btnCompleted?.setOnClickListener { performStatusUpdate(task, "completed") }
            btnEscalated?.setOnClickListener { performStatusUpdate(task, "escalated") }
        }

        private fun updateStatusUi(status: String) {
            val context = itemView.context
            val formattedStatus = status.replaceFirstChar { it.uppercase() }
            statusChip?.text = "Status: $formattedStatus"

            when (status.lowercase()) {
                "completed" -> {
                    taskCompletedIcon.setImageResource(R.drawable.ic_checked_item)
                    statusChip?.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
                }
                "escalated" -> {
                    taskCompletedIcon.setImageResource(R.drawable.ic_circle_outline)
                    statusChip?.setTextColor(ContextCompat.getColor(context, R.color.colorRed))
                }
                else -> {
                    taskCompletedIcon.setImageResource(R.drawable.ic_circle_outline)
                    statusChip?.setTextColor(ContextCompat.getColor(context, R.color.colorOrange))
                }
            }
        }

        private fun performStatusUpdate(
            task: Task,
            newStatus: String,
        ) {
            if (task.status?.equals(newStatus, ignoreCase = true) == true) {
                Toast.makeText(itemView.context, "Task is already $newStatus", Toast.LENGTH_SHORT).show()
                return
            }

            if (onStatusUpdate == null) {
                task.status = newStatus
                updateStatusUi(newStatus)
                notifyItemChanged(bindingAdapterPosition)
                return
            }

            statusControlsLayout?.visibility = View.INVISIBLE
            itemProgressBar?.visibility = View.VISIBLE

            onStatusUpdate.invoke(
                task,
                newStatus,
                {
                    itemProgressBar?.visibility = View.GONE
                    statusControlsLayout?.visibility = View.VISIBLE
                    task.status = newStatus
                    updateStatusUi(newStatus)
                    notifyItemChanged(bindingAdapterPosition)
                    Toast.makeText(itemView.context, "Task status updated to $newStatus", Toast.LENGTH_SHORT).show()
                },
                { errorMessage ->
                    itemProgressBar?.visibility = View.GONE
                    statusControlsLayout?.visibility = View.VISIBLE
                    Toast.makeText(itemView.context, errorMessage, Toast.LENGTH_LONG).show()
                },
            )
        }

        private fun String?.isNullMonitoredBlank(): Boolean {
            return this == null || this.trim().isEmpty()
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun updateTaskList(newTasks: List<Task>) {
        tasks = newTasks.toMutableList()
        notifyDataSetChanged()
        Log.d("TaskListAdapter", "Updated Task Data Size: ${tasks.size}")
    }

    fun updateData(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }
}
