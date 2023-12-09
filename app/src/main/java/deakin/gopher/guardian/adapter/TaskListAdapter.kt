package deakin.gopher.guardian.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Task

class TaskListAdapter(private val testData: List<Task>) :
    RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {
    init {
        Log.d("TaskListAdapter", "Test Data Size: ${testData.size}")
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
        holder.bind(testData[position])
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val descriptionTextView: TextView =
            itemView.findViewById(R.id.task_description_text_view)

        fun bind(task: Task) {
            descriptionTextView.text = task.description
        }
    }

    override fun getItemCount(): Int {
        return testData.size
    }
}
