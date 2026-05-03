package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Task

class TaskDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        val taskDescriptionTextView: TextView = findViewById(R.id.task_detail_description_text_view)
        val taskAssignedNurseTextView: TextView = findViewById(R.id.task_text_view_nurse_name)
        val taskPriorityTextView: TextView = findViewById(R.id.task_text_view_priority)

        val completeButton: Button = findViewById(R.id.task_button_mark_complete)
        val incompleteButton: Button = findViewById(R.id.task_button_mark_incomplete)
        val deleteButton: Button = findViewById(R.id.task_button_mark_delete)
        val backButton: Button = findViewById(R.id.task_detail_back_button)

        val taskId = intent.getStringExtra("taskId")

        // ADDED: Handle missing taskId (prevents crash / blank screen)
        if (taskId == null) {
            Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val taskRef = FirebaseDatabase.getInstance().getReference("nurse-tasks").child(taskId)

        // ADDED: Show loading state (basic UX improvement)
        taskDescriptionTextView.text = "Loading..."
        taskAssignedNurseTextView.text = ""
        taskPriorityTextView.text = ""

        taskRef.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    // ADDED: Handle empty snapshot (task deleted or missing)
                    if (!dataSnapshot.exists()) {
                        Toast.makeText(
                            this@TaskDetailActivity,
                            "Task not found",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                        return
                    }

                    val task = dataSnapshot.getValue(Task::class.java)

                    if (task != null) {

                        val textdesc =
                            getString(R.string.task_description_prefix) + " " + task.description
                        val textnur =
                            getString(R.string.assigned_nurse_prefix) + " " + task.assignedNurse
                        val textprior =
                            getString(R.string.priority_prefix) + " " + task.priority.toString()

                        taskDescriptionTextView.text = textdesc
                        taskAssignedNurseTextView.text = textnur
                        taskPriorityTextView.text = textprior

                        // IMPROVED: Clearer button state handling
                        if (task.completed) {
                            completeButton.visibility = View.GONE
                            incompleteButton.visibility = View.VISIBLE
                        } else {
                            incompleteButton.visibility = View.GONE
                            completeButton.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // IMPROVED: Better error message
                    Toast.makeText(
                        this@TaskDetailActivity,
                        "Failed to load task",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
        )

        // IMPROVED: Disable button during action to prevent multiple clicks
        fun markAsCompleted() {
            completeButton.isEnabled = false

            taskRef.child("completed").setValue(true)
                .addOnSuccessListener {
                    Toast.makeText(this, "Task marked as completed", Toast.LENGTH_SHORT).show()
                    completeButton.visibility = View.GONE
                    incompleteButton.visibility = View.VISIBLE
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update task", Toast.LENGTH_SHORT).show()
                    completeButton.isEnabled = true
                }
        }

        fun markAsIncomplete() {
            incompleteButton.isEnabled = false

            taskRef.child("completed").setValue(false)
                .addOnSuccessListener {
                    Toast.makeText(this, "Task marked as incomplete", Toast.LENGTH_SHORT).show()
                    incompleteButton.visibility = View.GONE
                    completeButton.visibility = View.VISIBLE
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update task", Toast.LENGTH_SHORT).show()
                    incompleteButton.isEnabled = true
                }
        }

        fun deleteTask() {
            deleteButton.isEnabled = false

            taskRef.removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to delete task", Toast.LENGTH_SHORT).show()
                    deleteButton.isEnabled = true
                }
        }

        completeButton.setOnClickListener {
            markAsCompleted()
        }

        incompleteButton.setOnClickListener {
            markAsIncomplete()
        }

        deleteButton.setOnClickListener {
            deleteTask()
        }

        backButton.setOnClickListener {
            finish()
        }
    }
}