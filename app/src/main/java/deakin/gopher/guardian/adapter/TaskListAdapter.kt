package deakin.gopher.guardian.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Task

class TaskListAdapter(private var tasks: List<Task>) :
    RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {

    init {
        Log.d("TaskListAdapter", "Task Data Size: ${tasks.size}")
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
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
        private val descriptionTextView: TextView =
            itemView.findViewById(R.id.task_description_text_view)
//        private val subDescriptionTextView: TextView =
//            itemView.findViewById(R.id.tasksubDescEditText)
//        private val patientIdTextView: TextView =
//            itemView.findViewById(R.id.taskPatientIdEditText)

        fun bind(task: Task) {
            descriptionTextView.text = task.description
//            subDescriptionTextView.text = task.subDescription
//            patientIdTextView.text = task.patientId
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun updateTaskList(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
        Log.d("TaskListAdapter", "Updated Task Data Size: ${tasks.size}")
    }
}
