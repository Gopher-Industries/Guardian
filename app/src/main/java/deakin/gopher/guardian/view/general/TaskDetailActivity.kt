package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.BaseModel
import deakin.gopher.guardian.model.Task
import deakin.gopher.guardian.services.api.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        } else {
            Toast.makeText(this, "Task ID is missing", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadTaskDetails(taskId: String) {
        ApiClient.apiService.getTaskById(taskId).enqueue(
            object : Callback<BaseModel<Task>> {
                override fun onResponse(
                    call: Call<BaseModel<Task>>,
                    response: Response<BaseModel<Task>>,
                ) {
                    if (response.isSuccessful) {
                        response.body()?.data?.let { task ->
                            findViewById<TextView>(R.id.task_detail_description_text_view).text =
                                task.description
                        } ?: showMessage("Task details are empty")
                    } else {
                        showMessage("Failed to load task: ${response.message()}")
                    }
                }

                override fun onFailure(
                    call: Call<BaseModel<Task>>,
                    t: Throwable,
                ) {
                    showMessage("Error loading task: ${t.localizedMessage}")
                }
            },
        )
    }

    private fun markAsCompleted(taskId: String) {
        ApiClient.apiService.updateTaskCompletion(taskId, true).enqueue(
            object : Callback<BaseModel<Unit>> {
                override fun onResponse(
                    call: Call<BaseModel<Unit>>,
                    response: Response<BaseModel<Unit>>,
                ) {
                    if (response.isSuccessful) {
                        showMessage("Task marked as completed")
                    } else {
                        showMessage("Failed to mark task as complete: ${response.message()}")
                    }
                }

                override fun onFailure(
                    call: Call<BaseModel<Unit>>,
                    t: Throwable,
                ) {
                    showMessage("Error marking task as completed: ${t.localizedMessage}")
                }
            },
        )
    }

    private fun deleteTask(taskId: String) {
        ApiClient.apiService.deleteTask(taskId).enqueue(
            object : Callback<BaseModel<Unit>> {
                override fun onResponse(
                    call: Call<BaseModel<Unit>>,
                    response: Response<BaseModel<Unit>>,
                ) {
                    if (response.isSuccessful) {
                        showMessage("Task deleted successfully")
                        finish()
                    } else {
                        showMessage("Failed to delete task: ${response.message()}")
                    }
                }

                override fun onFailure(
                    call: Call<BaseModel<Unit>>,
                    t: Throwable,
                ) {
                    showMessage("Error deleting task: ${t.localizedMessage}")
                }
            },
        )
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
