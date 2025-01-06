package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import deakin.gopher.guardian.R

class TaskDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        val taskId = intent.getStringExtra("taskId")
        val taskDescriptionTextView: TextView = findViewById(R.id.task_detail_description_text_view)
        val completeButton: Button = findViewById(R.id.task_button_mark_complete)
        val deleteButton: Button = findViewById(R.id.task_button_mark_delete)
        val editButton: Button = findViewById(R.id.task_button_edit)

        if (taskId != null) {
            loadTaskDetails(taskId)

            completeButton.setOnClickListener {
                markAsCompleted(taskId)
            }

            deleteButton.setOnClickListener {
                deleteTask(taskId)
            }

            editButton.setOnClickListener {
                val intent = Intent(this, TaskAddActivity::class.java)
                intent.putExtra("taskId", taskId)
                startActivity(intent)
            }
        }
    }

    private fun loadTaskDetails(taskId: String) {
        val taskRef = FirebaseDatabase.getInstance().reference.child("caretaker_tasks").child(taskId)
        taskRef.get().addOnSuccessListener { snapshot ->
            val task = snapshot.getValue(Task::class.java)
            if (task != null) {
                findViewById<TextView>(R.id.task_detail_description_text_view).text = task.description
            }
        }
    }

    private fun markAsCompleted(taskId: String) {
        val taskRef = FirebaseDatabase.getInstance().reference.child("caretaker_tasks").child(taskId)
        taskRef.child("completed").setValue(true).addOnSuccessListener {
            Toast.makeText(this, "Task marked as completed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteTask(taskId: String) {
        val taskRef = FirebaseDatabase.getInstance().reference.child("caretaker_tasks").child(taskId)
        taskRef.removeValue().addOnSuccessListener {
            Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
