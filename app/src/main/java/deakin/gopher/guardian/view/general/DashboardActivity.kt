package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R

// Define the data class for tasks
data class Task(val id: Int, val name: String, val status: String)

class DashboardActivity : AppCompatActivity() {

    // Sample static tasks list
    private val tasks = mutableListOf(
        Task(1, "Check patient vitals", "Pending"),
        Task(2, "Assist with mobility", "In Progress"),
        Task(3, "Administer medication", "Completed")
    )

    // Declare views
    private lateinit var taskSummary: TextView
    private lateinit var viewTasksBtn: Button
    private lateinit var sortSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Initialize views
        taskSummary = findViewById(R.id.taskSummary)
        viewTasksBtn = findViewById(R.id.buttonViewTasks)
        sortSpinner = findViewById(R.id.sortSpinner)

        // Spinner setup
        val sortOptions = arrayOf("None", "Status", "Task Name")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortSpinner.adapter = spinnerAdapter

        // Spinner change listener
        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selected = sortOptions[position]
                val sortedTasks = when (selected) {
                    "Status" -> tasks.sortedBy { it.status }
                    "Task Name" -> tasks.sortedBy { it.name }
                    else -> tasks
                }

                updateTaskSummary(sortedTasks)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Show initial summary
        updateTaskSummary(tasks)

        // View all tasks button navigation
        viewTasksBtn.setOnClickListener {
            startActivity(Intent(this, TasksListActivity::class.java))
        }
    }

    // Helper method to update summary
    private fun updateTaskSummary(sortedTasks: List<Task>) {
        val total = sortedTasks.size
        val pending = sortedTasks.count { it.status == "Pending" }
        val inProgress = sortedTasks.count { it.status == "In Progress" }
        val completed = sortedTasks.count { it.status == "Completed" }

        taskSummary.text = "Total Tasks: $total\nPending: $pending | In Progress: $inProgress | Completed: $completed"
    }
}
