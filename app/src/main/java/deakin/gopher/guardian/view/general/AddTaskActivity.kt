package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R

class AddTaskActivity : AppCompatActivity() {

    private lateinit var taskTitleInput: EditText
    private lateinit var taskDescriptionInput: EditText
    private lateinit var dueDateInput: EditText
    private lateinit var assignToInput: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        // Initialize the EditText and Button views
        taskTitleInput = findViewById(R.id.inputTaskTitle)
        taskDescriptionInput = findViewById(R.id.inputTaskDescription)
        dueDateInput = findViewById(R.id.inputDueDate)
        assignToInput = findViewById(R.id.inputAssignTo)
        submitButton = findViewById(R.id.buttonSubmitTask)

        // Set up the Submit Task button functionality
        submitButton.setOnClickListener {
            val taskTitle = taskTitleInput.text.toString().trim()
            val taskDescription = taskDescriptionInput.text.toString().trim()
            val dueDate = dueDateInput.text.toString().trim()
            val assignTo = assignToInput.text.toString().trim()

            // Validate input fields
            if (taskTitle.isEmpty() || taskDescription.isEmpty() || dueDate.isEmpty() || assignTo.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // For now, display a success message
                // Later, you can add functionality to save or process the task
                Toast.makeText(
                    this,
                    "Task Submitted:\nTitle: $taskTitle\nDescription: $taskDescription\nDue Date: $dueDate\nAssign To: $assignTo",
                    Toast.LENGTH_LONG
                ).show()

                // Optionally, clear the fields after submission
                taskTitleInput.text.clear()
                taskDescriptionInput.text.clear()
                dueDateInput.text.clear()
                assignToInput.text.clear()
            }
        }
    }
}
