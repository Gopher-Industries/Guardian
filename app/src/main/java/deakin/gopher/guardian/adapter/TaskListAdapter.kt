package deakin.gopher.guardian.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Task
import deakin.gopher.guardian.view.general.TaskDetailActivity

class TaskListAdapter(private var tasks: MutableList<Task>) :
    RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {
    init {
        Log.d("TaskListAdapter", "Task Data Size: ${tasks.size}")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.task_list_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val descriptionTextView: TextView =
            itemView.findViewById(R.id.task_description_text_view)

        private val viewTaskDetailsButton: Button =
            itemView.findViewById(R.id.task_description_button)
        private val taskCompletedIcon: ImageView = itemView.findViewById(R.id.task_completed_icon)

        init {
            viewTaskDetailsButton.setOnClickListener {
                val context = itemView.context
                val task = tasks[adapterPosition]
                val intent = Intent(context, TaskDetailActivity::class.java)
                intent.putExtra("taskId", task.taskId)
                context.startActivity(intent)
            }
        }

        fun bind(task: Task) {
            descriptionTextView.text = task.description
            if (task.completed) {
                taskCompletedIcon.setImageResource(R.drawable.green_circle)
            } else {
                taskCompletedIcon.setImageResource(R.drawable.red_circle)
            }
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
