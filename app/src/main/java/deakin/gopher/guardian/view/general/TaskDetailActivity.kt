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
import deakin.gopher.guardian.model.login.Role
import deakin.gopher.guardian.model.login.SessionManager

class TaskDetailActivity : AppCompatActivity() {
    companion object {
        private const val CARETAKER_TASKS_NODE = "caretaker_tasks"
        private const val NURSE_TASKS_NODE = "nurse_tasks"
        private const val PATIENT_TASKS_NODE = "patient_tasks"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)
        val taskDescriptionTextView: TextView = findViewById(R.id.task_detail_description_text_view)
        val taskAssignedNurseTextView: TextView = findViewById(R.id.task_text_view_nurse_name)
        val taskPriorityTextView: TextView = findViewById(R.id.task_text_view_priority)
        val completeButton: Button = findViewById(R.id.task_button_mark_complete)
        val backButton: Button = findViewById(R.id.task_detail_back_button)
        val deleteButton: Button = findViewById(R.id.task_button_mark_delete)
        val incompleteButton: Button = findViewById(R.id.task_button_mark_incomplete)
        val buttonsContainer: View = findViewById(R.id.buttons_container)
        val currentRole = SessionManager.getCurrentUser().role
        val canUpdateCompletion = currentRole == Role.Nurse
        val canDeleteTask = currentRole == Role.Caretaker
        val taskId = intent.getStringExtra("taskId")

        backButton.setOnClickListener {
            finish()
        }

        deleteButton.visibility = if (canDeleteTask) View.VISIBLE else View.GONE
        if (!canUpdateCompletion) {
            completeButton.visibility = View.GONE
            incompleteButton.visibility = View.GONE
        }
        buttonsContainer.visibility =
            if (canUpdateCompletion || canDeleteTask) {
                View.VISIBLE
            } else {
                View.GONE
            }

        if (taskId.isNullOrBlank()) {
            Toast.makeText(this, "Task details are unavailable", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val taskRef = FirebaseDatabase.getInstance().getReference(CARETAKER_TASKS_NODE).child(taskId)
        taskRef.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val task =
                            dataSnapshot.getValue(
                                Task::class.java,
                            )
                        if (task != null) {
                            var textdesc = getString(R.string.task_description_prefix) + " " + task.description
                            var textnur = getString(R.string.assigned_nurse_prefix) + " " + task.assignedNurse
                            var textprior = getString(R.string.priority_prefix) + " " + task.priority.toString()

                            taskDescriptionTextView.setText(textdesc)
                            taskAssignedNurseTextView.setText(textnur)
                            taskPriorityTextView.setText(textprior)

                            if (canUpdateCompletion) {
                                if (task.completed) {
                                    completeButton.visibility = View.GONE
                                    incompleteButton.visibility = View.VISIBLE
                                } else {
                                    incompleteButton.visibility = View.GONE
                                    completeButton.visibility = View.VISIBLE
                                }
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@TaskDetailActivity,
                            "Task details are unavailable",
                            Toast.LENGTH_SHORT,
                        ).show()
                        finish()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        this@TaskDetailActivity,
                        databaseError.toString(),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            },
        )

        fun updateTaskCompletion(completed: Boolean) {
            val updates =
                hashMapOf<String, Any?>(
                    "$CARETAKER_TASKS_NODE/$taskId/completed" to completed,
                    "$NURSE_TASKS_NODE/$taskId/completed" to completed,
                )
            FirebaseDatabase.getInstance()
                .reference
                .updateChildren(updates)
                .addOnSuccessListener {
                    Toast.makeText(
                        this@TaskDetailActivity,
                        if (completed) "Task marked as completed" else "Task marked as incomplete",
                        Toast.LENGTH_SHORT,
                    ).show()
                    completeButton.visibility = if (completed) View.GONE else View.VISIBLE
                    incompleteButton.visibility = if (completed) View.VISIBLE else View.GONE
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this@TaskDetailActivity,
                        "Failed to update task",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
        }

        fun deleteTask() {
            val updates =
                hashMapOf<String, Any?>(
                    "$CARETAKER_TASKS_NODE/$taskId" to null,
                    "$NURSE_TASKS_NODE/$taskId" to null,
                    "$PATIENT_TASKS_NODE/$taskId" to null,
                )
            FirebaseDatabase.getInstance()
                .reference
                .updateChildren(updates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to delete task", Toast.LENGTH_SHORT).show()
                }
        }
        completeButton.setOnClickListener {
            updateTaskCompletion(true)
        }
        incompleteButton.setOnClickListener {
            updateTaskCompletion(false)
        }
        deleteButton.setOnClickListener {
            deleteTask()
        }
    }
}
