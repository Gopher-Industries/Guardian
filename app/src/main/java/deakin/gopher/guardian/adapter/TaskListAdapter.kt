package deakin.gopher.guardian.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Task

class TaskListAdapter : RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {
    private val tasks: MutableList<Task> = mutableListOf()

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
        Log.d("TaskListAdapter", tasks[position].toString())
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val descriptionTextView: TextView =
            itemView.findViewById(R.id.task_description_text_view)

        fun bind(task: Task) {
            descriptionTextView.text = task.description
            Log.d("TaskListAdapter", task.description)
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun updateData(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }
}

