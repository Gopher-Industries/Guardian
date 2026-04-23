package deakin.gopher.guardian.view.dialog


import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.calendar.TaskResponse
import deakin.gopher.guardian.util.CalendarUtils
import java.text.SimpleDateFormat
import java.util.*

class TaskDetailDialog {

    companion object {
        fun show(
            context: Context,
            task: TaskResponse,
            onEdit: ((TaskResponse) -> Unit)? = null,
            onMarkComplete: ((String) -> Unit)? = null
        ) {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)

            val view = LayoutInflater.from(context).inflate(R.layout.dialog_task_detail, null)
            dialog.setContentView(view)

            // Bind data to views
            bindTaskData(view, task)

            // Setup click listeners
            setupClickListeners(view, task, dialog, onEdit, onMarkComplete)

            // Show dialog
            dialog.show()

            // Set dialog size
            val window = dialog.window
            window?.setLayout(
                (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        private fun bindTaskData(view: android.view.View, task: TaskResponse) {
            val taskTitle = view.findViewById<TextView>(R.id.task_title)
            val taskDescription = view.findViewById<TextView>(R.id.task_description)
            val taskDateTime = view.findViewById<TextView>(R.id.task_datetime)
            val taskStatus = view.findViewById<TextView>(R.id.task_status)
            val taskCaretaker = view.findViewById<TextView>(R.id.task_caretaker)
            val priorityBadge = view.findViewById<TextView>(R.id.priority_badge)
            val markCompleteButton = view.findViewById<Button>(R.id.mark_complete_button)

            // Set basic info
            taskTitle.text = task.title
            taskDescription.text = task.description

            // Format date and time
            val formattedDateTime = task.dueDate?.let {
                formatDateTime(it)
            } ?: "No date set"
            taskDateTime.text = formattedDateTime

            // Status with color coding
            taskStatus.text = task.status.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
            }

            // Set status color
            val statusColor = when (task.status.lowercase()) {
                "completed" -> view.context.getColor(R.color.success_color)
                "in_progress" -> view.context.getColor(R.color.warning_color)
                "pending" -> view.context.getColor(R.color.text_secondary)
                else -> view.context.getColor(R.color.text_secondary)
            }
            taskStatus.setTextColor(statusColor)

            // Caretaker info (you can resolve this to a name if needed)
            taskCaretaker.text = "Assigned Caretaker"

            // Priority badge
            priorityBadge.text = task.priority.uppercase()
            val priorityColor = when (task.priority.lowercase()) {
                "urgent", "high" -> view.context.getColor(R.color.priority_high)
                "medium" -> view.context.getColor(R.color.priority_medium)
                "low" -> view.context.getColor(R.color.priority_low)
                else -> view.context.getColor(R.color.priority_medium)
            }
            priorityBadge.setBackgroundColor(priorityColor)

            // Hide complete button if already completed
            if (task.status.lowercase() == "completed") {
                markCompleteButton.visibility = android.view.View.GONE
            }
        }

        private fun setupClickListeners(
            view: android.view.View,
            task: TaskResponse,
            dialog: Dialog,
            onEdit: ((TaskResponse) -> Unit)?,
            onMarkComplete: ((String) -> Unit)?
        ) {
            val editButton = view.findViewById<Button>(R.id.edit_task_button)
            val markCompleteButton = view.findViewById<Button>(R.id.mark_complete_button)

            editButton.setOnClickListener {
                dialog.dismiss()
                onEdit?.invoke(task)
            }

            markCompleteButton.setOnClickListener {
                showCompleteConfirmation(view.context, task.id) { taskId ->
                    dialog.dismiss()
                    onMarkComplete?.invoke(taskId)
                }
            }
        }

        private fun showCompleteConfirmation(
            context: Context,
            taskId: String,
            onConfirm: (String) -> Unit
        ) {
            AlertDialog.Builder(context)
                .setTitle("Mark Task Complete")
                .setMessage("Are you sure you want to mark this task as completed?")
                .setPositiveButton("Complete") { _, _ -> onConfirm(taskId) }
                .setNegativeButton("Cancel", null)
                .show()
        }

        private fun formatDateTime(dateString: String): String {
            return try {
                val date = CalendarUtils.parseDateString(dateString)
                val outputFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy 'at' h:mm a", Locale.getDefault())
                outputFormat.format(date)
            } catch (e: Exception) {
                "Invalid date format"
            }
        }
    }
}