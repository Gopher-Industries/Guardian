package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.TaskListAdapter
import deakin.gopher.guardian.model.Task
class TasksListActivity : AppCompatActivity() {
    private lateinit var taskListAdapter: TaskListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks_list)

        val addTaskButton: Button = findViewById(R.id.add_task_button)
        val clearAllButton: Button = findViewById(R.id.clear_all_button)

        addTaskButton.setOnClickListener {
            startActivity(Intent(this, TaskAddActivity::class.java))
        }

        clearAllButton.setOnClickListener {
            clearAllTasks()
        }

        val recyclerView: RecyclerView = findViewById(R.id.task_list_recycleView)
        recyclerView.layoutManager = GridLayoutManager(this, 1)
        taskListAdapter = TaskListAdapter(mutableListOf())
        recyclerView.adapter = taskListAdapter

        fetchTasks()
    }

    private fun fetchTasks() {
        val taskRef = FirebaseDatabase.getInstance().reference.child("caretaker_tasks")
        taskRef.get().addOnSuccessListener { snapshot ->
            val taskList = snapshot.children.mapNotNull { it.getValue(Task::class.java) }
            taskListAdapter.updateTaskList(taskList)
        }
    }
    private fun clearAllTasks() {
        val taskRef = FirebaseDatabase.getInstance().reference.child("caretaker_tasks")
        taskRef.removeValue().addOnSuccessListener {
            taskListAdapter.updateTaskList(emptyList())
        }
    }
}
