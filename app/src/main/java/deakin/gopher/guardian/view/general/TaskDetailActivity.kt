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
        var taskDescriptionTextView: TextView = findViewById(R.id.task_detail_description_text_view)
        var taskAssignedNurseTextView: TextView = findViewById(R.id.task_text_view_nurse_name)
        var taskPriorityTextView: TextView = findViewById(R.id.task_text_view_priority)
        var completeButton: Button = findViewById(R.id.task_button_mark_complete)
        var backButton: Button = findViewById(R.id.task_detail_back_button)
        var deleteButton: Button = findViewById(R.id.task_button_mark_delete)
        var incompleteButton: Button = findViewById(R.id.task_button_mark_incomplete)
        val taskId = intent.getStringExtra("taskId")
        if (taskId != null) {
            val taskRef = FirebaseDatabase.getInstance().getReference("nurse-tasks").child(taskId)
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

                                if (task.completed) {
                                    completeButton.visibility = View.GONE
                                    incompleteButton.visibility = View.VISIBLE
                                } else {
                                    incompleteButton.visibility = View.GONE
                                    completeButton.visibility = View.VISIBLE
                                }
                            }
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

            fun markAsCompleted() {
                val taskRef = FirebaseDatabase.getInstance().getReference("nurse-tasks").child(taskId)
                taskRef.child("completed").setValue(true)
                Toast.makeText(this@TaskDetailActivity, "Task marked as completed", Toast.LENGTH_SHORT).show()
                completeButton.visibility = View.GONE
                incompleteButton.visibility = View.VISIBLE
            }

            fun markAsIncomplete() {
                val taskRef = FirebaseDatabase.getInstance().getReference("nurse-tasks").child(taskId)
                taskRef.child("completed").setValue(false)
                Toast.makeText(this@TaskDetailActivity, "Task marked as incomplete", Toast.LENGTH_SHORT).show()
                incompleteButton.visibility = View.GONE
                completeButton.visibility = View.VISIBLE
            }

            fun deleteTask() {
                val taskRef =
                    FirebaseDatabase.getInstance().getReference("nurse-tasks").child(taskId)
                taskRef.removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to delete task", Toast.LENGTH_SHORT).show()
                    }
            }
            completeButton.setOnClickListener {
                markAsCompleted()
            }
            incompleteButton.setOnClickListener {
                markAsIncomplete()
            }
            backButton.setOnClickListener {
                finish()
            }
            deleteButton.setOnClickListener {
                deleteTask()
            }
        }
    }
}
