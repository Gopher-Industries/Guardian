// DashboardActivity.kt
package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val taskSummary: TextView = findViewById(R.id.taskSummary)
        val viewTasksBtn: Button = findViewById(R.id.buttonViewTasks)

        // Dummy data clearly showing task summary
        taskSummary.text = "Total Tasks: 5\nPending: 2 | In Progress: 2 | Completed: 1"

        // Button click clearly navigates to existing TasksListActivity
        viewTasksBtn.setOnClickListener {
            startActivity(Intent(this, TasksListActivity::class.java))
        }
    }
}
