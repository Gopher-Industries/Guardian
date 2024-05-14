package deakin.gopher.guardian.view.general

import android.os.Bundle
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
        val taskId = intent.getStringExtra("taskId")
        if (taskId != null) {
            val taskRef = FirebaseDatabase.getInstance().getReference("tasks").child(taskId)
            taskRef.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val task =
                                dataSnapshot.getValue(
                                    Task::class.java,
                                )
                            if (task != null) {
                                taskDescriptionTextView.setText(task.description)
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
        }
    }
}
