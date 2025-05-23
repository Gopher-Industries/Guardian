package deakin.gopher.guardian.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Task

class TaskListAdapter(
    private var taskList: MutableList<Task>
) : RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {

    private val expandedPositions = mutableSetOf<Int>()
    private var deleteListener: OnTaskDeleteListener? = null

    // --- ViewHolder ---
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.task_title)
        val descriptionText: TextView = itemView.findViewById(R.id.task_description)
        val dueDateText: TextView = itemView.findViewById(R.id.task_due_date)
        val priorityText: TextView = itemView.findViewById(R.id.task_priority)
        val assignedNurseText: TextView = itemView.findViewById(R.id.task_assigned_nurse)
        val patientIdText: TextView = itemView.findViewById(R.id.task_patient_id)
        val completedText: TextView = itemView.findViewById(R.id.task_completed)
        val expandIcon: ImageView = itemView.findViewById(R.id.task_expand_icon)
        val detailsLayout: View = itemView.findViewById(R.id.task_details_layout)
        val deleteIcon: ImageView = itemView.findViewById(R.id.delete_icon) // Ensure this ID exists in your layout
    }

    // --- Interface for delete callback ---
    interface OnTaskDeleteListener {
        fun onDeleteTask(task: Task, position: Int)
    }

    fun setOnTaskDeleteListener(listener: OnTaskDeleteListener) {
        deleteListener = listener
    }

    // --- ViewHolder creation ---
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task_dropdown, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int = taskList.size

    // --- View binding ---
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        val isExpanded = expandedPositions.contains(position)

        holder.titleText.text = task.title
        holder.descriptionText.text = "Description: ${task.description}"
        holder.dueDateText.text = "Due: ${task.dueDate}"
        holder.priorityText.text = "Priority: ${task.priority}"
        holder.assignedNurseText.text = "Assigned Nurse: ${task.assignedNurse}"
        holder.patientIdText.text = "Patient ID: ${task.patientId}"
        holder.completedText.text = "Completed: ${if (task.completed) "Yes" else "No"}"

        holder.detailsLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.expandIcon.setImageResource(
            if (isExpanded) R.drawable.ic_expand_less else R.drawable.ic_expand_more
        )

        holder.itemView.setOnClickListener {
            if (isExpanded) expandedPositions.remove(position) else expandedPositions.add(position)
            notifyItemChanged(position)
        }

        holder.deleteIcon.setOnClickListener {
            deleteListener?.onDeleteTask(task, position)
        }
    }

    // --- Remove task from list and notify adapter ---
    fun removeTaskAt(position: Int) {
        if (position in taskList.indices) {
            taskList.removeAt(position)
            expandedPositions.remove(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, taskList.size)
        }
    }

    // --- Update all tasks ---
    fun updateTaskList(newList: List<Task>) {
        taskList = newList.toMutableList()
        expandedPositions.clear()
        notifyDataSetChanged()
    }
}
