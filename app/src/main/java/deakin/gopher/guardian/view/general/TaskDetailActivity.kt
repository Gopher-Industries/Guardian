package deakin.gopher.guardian.view.general


import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Priority
import deakin.gopher.guardian.model.Task

class TaskDetailActivity : AppCompatActivity() {

    private lateinit var completeButton: ImageButton
    private lateinit var incompleteButton: ImageButton
    private lateinit var deleteButton: ImageButton

    private lateinit var taskTitle: TextView
    private lateinit var taskDetails: TextView

    private var task: Task? = null  // Using your Task data class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        // Initialize views
        completeButton = findViewById(R.id.completeButton)
        incompleteButton = findViewById(R.id.incompleteButton)
        deleteButton = findViewById(R.id.deleteButton)

        taskTitle = findViewById(R.id.taskTitle)
        taskDetails = findViewById(R.id.taskDetails)

        // Load your task from intent or ViewModel or however you do it
//        task = getTaskFromIntent() // Implement this function as needed

        // Populate UI with task data
        task?.let {
            taskTitle.text = it.title
            taskDetails.text = it.description
        }

//        description// Set up icon button click listeners
//        completeButton.setOnClickListener {
//            task?.let {
//                markTaskCompletion(it.taskId, true)
//            }
//        }
//
//        incompleteButton.setOnClickListener {
//            task?.let {
//                markTaskCompletion(it.taskId, false)
//            }
//        }
        deleteButton.setOnClickListener {
            task?.taskId?.let { id ->
                deleteTask(id)
            } ?: Toast.makeText(this, "Task ID is null", Toast.LENGTH_SHORT).show()
        }

    }






    private fun fetchTaskFromDatabase(taskId: String): Task? {
        // Replace this stub with actual DB or data source fetch
        // For example:
        // return yourDatabase.getTaskById(taskId)
        return null
    }

    private fun markTaskCompletion(taskId: String, completed: Boolean) {
        // Your logic to mark the task complete/incomplete
        // For example, update DB, update UI, show toast, etc.
        // Example stub:
        // yourDatabase.updateTaskCompletion(taskId, completed)
    }

    private fun deleteTask(taskId: String) {
        // Your logic to delete the task
        // For example, delete from DB, finish activity, notify user
        // Example stub:
        // yourDatabase.deleteTask(taskId)
        finish()
    }
}
