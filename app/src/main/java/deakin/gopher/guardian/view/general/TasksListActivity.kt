package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.TaskListAdapter
import deakin.gopher.guardian.model.BaseModel
import deakin.gopher.guardian.model.Task
import deakin.gopher.guardian.services.api.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TasksListActivity : AppCompatActivity() {
    private lateinit var taskListAdapter: TaskListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks_list)

        val recyclerView: RecyclerView = findViewById(R.id.task_list_recycleView)
        setupRecyclerView(recyclerView)

        fetchTasks()
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        taskListAdapter =
            TaskListAdapter(mutableListOf()) { taskId ->
                navigateToTaskDetail(taskId)
            }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskListAdapter
    }

    private fun fetchTasks() {
        ApiClient.apiService.getAllTasks().enqueue(
            object : Callback<BaseModel<List<Task>>> {
                override fun onResponse(
                    call: Call<BaseModel<List<Task>>>,
                    response: Response<BaseModel<List<Task>>>,
                ) {
                    if (response.isSuccessful) {
                        response.body()?.data?.let { tasks ->
                            taskListAdapter.updateTaskList(tasks)
                        } ?: Toast.makeText(this@TasksListActivity, "No tasks found", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@TasksListActivity, "Failed to fetch tasks", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(
                    call: Call<BaseModel<List<Task>>>,
                    t: Throwable,
                ) {
                    Toast.makeText(this@TasksListActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            },
        )
    }

    private fun navigateToTaskDetail(taskId: String) {
        val intent = Intent(this, TaskDetailActivity::class.java)
        intent.putExtra("taskId", taskId)
        startActivity(intent)
    }
}
