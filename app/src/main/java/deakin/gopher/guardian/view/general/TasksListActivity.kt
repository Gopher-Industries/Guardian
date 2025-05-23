package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.TaskListAdapter
import deakin.gopher.guardian.model.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat

class TasksListActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var menuButton: ImageView
    private lateinit var addTaskButton: MaterialButton

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskListAdapter

    private val taskApiService = ApiClient.retrofit.create(TaskApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks_list)

        // View setup
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        menuButton = findViewById(R.id.task_list_manu_button)
        addTaskButton = findViewById(R.id.add_task_material_button)
        recyclerView = findViewById(R.id.tasks_recycler_view)

        // RecyclerView & Adapter setup
        adapter = TaskListAdapter(mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        adapter.setOnTaskDeleteListener(object : TaskListAdapter.OnTaskDeleteListener {
            override fun onDeleteTask(task: Task, position: Int) {
                if (task.taskId == null) {
                    Toast.makeText(this@TasksListActivity, "Task ID is null", Toast.LENGTH_SHORT).show()
                    return
                }

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = taskApiService.deleteTask(task.taskId)
                        withContext(Dispatchers.Main) {
                            val rootView = findViewById<View>(R.id.drawer_layout)
                            if (response.isSuccessful) {
                                adapter.removeTaskAt(position)
                                Snackbar.make(rootView, "Task deleted", Snackbar.LENGTH_SHORT)
                                    .setBackgroundTint(ContextCompat.getColor(this@TasksListActivity, R.color.colorError))
                                    .setTextColor(ContextCompat.getColor(this@TasksListActivity, android.R.color.black))
                                    .show()
                            } else {
                                Snackbar.make(rootView, "Failed to delete task: ${response.code()}", Snackbar.LENGTH_SHORT)
                                    .setBackgroundTint(ContextCompat.getColor(this@TasksListActivity, R.color.colorError))
                                    .setTextColor(ContextCompat.getColor(this@TasksListActivity, android.R.color.white))
                                    .show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            val rootView = findViewById<View>(R.id.drawer_layout)
                            Snackbar.make(rootView, "Error: ${e.localizedMessage}", Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(ContextCompat.getColor(this@TasksListActivity, R.color.colorError))
                                .setTextColor(ContextCompat.getColor(this@TasksListActivity, android.R.color.white))
                                .show()
                        }
                    }
                }

            }
        })


        // Menu Button
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Navigation Drawer Actions
        navigationView.setNavigationItemSelectedListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> startActivity(Intent(this@TasksListActivity, Homepage4caretaker::class.java))
                R.id.add_task -> startActivity(Intent(this@TasksListActivity, TaskAddActivity::class.java))
                R.id.nav_signout -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this@TasksListActivity, LoginActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        // Add Task Button
        addTaskButton.setOnClickListener {
            startActivity(Intent(this@TasksListActivity, TaskAddActivity::class.java))
        }

        // Load Tasks
        fetchTasksFromApi()
    }

    private fun fetchTasksFromApi() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = taskApiService.getAllTasks()
                if (response.isSuccessful) {
                    val tasks = response.body() ?: emptyList()
                    withContext(Dispatchers.Main) {
                        adapter.updateTaskList(tasks)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@TasksListActivity, "Failed to fetch tasks: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@TasksListActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
