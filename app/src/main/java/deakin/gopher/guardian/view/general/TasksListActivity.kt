package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.TaskListAdapter
import deakin.gopher.guardian.model.Task
import deakin.gopher.guardian.model.login.Role
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.NavigationService
import deakin.gopher.guardian.services.api.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class TasksListActivity : AppCompatActivity() {
    private var taskListAdapter: TaskListAdapter? = null
    private var firebaseQuery: Query? = null
    private var overviewCardview: CardView? = null
    private lateinit var plusButton: ImageButton
    private lateinit var progressTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var errorStateLayout: LinearLayout
    private lateinit var errorStateText: TextView
    private lateinit var btnRetry: Button

    private var allTasks: MutableList<Task> = mutableListOf()
    private var currentQueryText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks_list)

        val currentUser =
            try {
                SessionManager.getCurrentUser()
            } catch (e: Exception) {
                null
            }

        if (currentUser == null) {
            Toast.makeText(this, "Session expired, please login again", Toast.LENGTH_SHORT).show()
            NavigationService(this).toLogin()
            return
        }

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val taskListMenuBtn: ImageView = findViewById(R.id.task_list_manu_button)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navigationService = NavigationService(this)
        val canAddTasks = currentUser.role == Role.Caretaker

        plusButton = findViewById(R.id.imageView62)
        progressTextView = findViewById(R.id.task_progress_text)
        recyclerView = findViewById(R.id.task_list_recycleView)
        progressBar = findViewById(R.id.task_list_progress_bar)
        emptyStateLayout = findViewById(R.id.empty_state_layout)
        errorStateLayout = findViewById(R.id.error_state_layout)
        errorStateText = findViewById(R.id.error_state_text)
        btnRetry = findViewById(R.id.btn_retry)

        navigationView.setItemIconTintList(null)
        navigationView.menu.findItem(R.id.add_task)?.isVisible = canAddTasks

        // Setup side menu listener
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    try {
                        val role = SessionManager.getCurrentUser().role
                        navigationService.toHomeScreenForRole(role)
                    } catch (e: Exception) {
                        navigationService.toLogin()
                    }
                }
                R.id.add_task -> {
                    if (canAddTasks) {
                        navigationService.onLaunchTaskCreator()
                    }
                }
                R.id.nav_signout -> {
                    navigationService.onSignOut()
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        taskListMenuBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        plusButton.setOnClickListener {
            if (canAddTasks) {
                navigationService.onLaunchTaskCreator()
            }
        }

        overviewCardview = findViewById(R.id.task_list_task_overview)
        val taskSearchView: SearchView = findViewById(R.id.task_list_searchView)
        val addTaskButton: Button = findViewById(R.id.add_task_button)
        val addItemLayout: View = findViewById(R.id.add_item_layout)

        addTaskButton.setOnClickListener {
            if (canAddTasks) {
                navigationService.onLaunchTaskCreator()
            }
        }

        if (!canAddTasks) {
            plusButton.visibility = View.GONE
            addItemLayout.visibility = View.GONE
        }

        // Initialize RecyclerView and Adapter with Status Update Callback
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@TasksListActivity)
        taskListAdapter =
            TaskListAdapter(mutableListOf()) { task, newStatus, onSuccess, onError ->
                handleTaskStatusUpdate(task, newStatus, onSuccess, onError)
            }
        recyclerView.adapter = taskListAdapter

        btnRetry.setOnClickListener {
            loadTasks()
        }

        taskSearchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(s: String?): Boolean = false

                override fun onQueryTextChange(s: String?): Boolean {
                    currentQueryText = s.orEmpty()
                    filterAndDisplayTasks()
                    return true
                }
            },
        )

        loadTasks()
    }

    private fun loadTasks() {
        showLoadingState()

        val currentUser =
            try {
                SessionManager.getCurrentUser()
            } catch (e: Exception) {
                null
            }
        val token =
            try {
                "Bearer ${SessionManager.getToken()}"
            } catch (e: Exception) {
                ""
            }

        if (currentUser == null || token.isBlank()) {
            showErrorState("Authentication required. Please log in.")
            return
        }

        lifecycleScope.launch {
            try {
                val response =
                    withContext(Dispatchers.IO) {
                        ApiClient.apiService.getTasksByAssignee(token, currentUser.id)
                    }

                if (response.isSuccessful && response.body() != null) {
                    val taskList = response.body()!!
                    allTasks.clear()
                    allTasks.addAll(taskList)
                    Log.d("TasksList", "API tasks received: ${allTasks.size}")
                    if (allTasks.isEmpty()) {
                        showEmptyState()
                    } else {
                        showSuccessState()
                        filterAndDisplayTasks()
                        Log.d("TasksList", "Adapter item count: ${taskListAdapter?.itemCount}")
                        if (allTasks.isNotEmpty()) {
                            Log.d(
                                "TasksList",
                                "First task ID: ${allTasks[0].taskId}, Title: ${allTasks[0].title}, Status: ${allTasks[0].status}",
                            )
                        }
                    }
                } else {
                    Log.w("TasksListActivity", "Backend GET tasks returned ${response.code()}, falling back to Firebase if available")
                    fetchDataFromFirebaseFallback()
                }
            } catch (e: Exception) {
                Log.e("TasksListActivity", "Error loading tasks from backend: ${e.localizedMessage}", e)
                fetchDataFromFirebaseFallback()
            }
        }
    }

    private fun handleTaskStatusUpdate(
        task: Task,
        newStatus: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val token =
            try {
                "Bearer ${SessionManager.getToken()}"
            } catch (e: Exception) {
                ""
            }
        if (token.isBlank() || task.taskId.isBlank()) {
            onError("Task ID or authentication token missing")
            return
        }

        lifecycleScope.launch {
            try {
                val statusBody = newStatus.toRequestBody("text/plain".toMediaTypeOrNull())
                val response =
                    withContext(Dispatchers.IO) {
                        ApiClient.apiService.updateTaskStatus(token, task.taskId, statusBody)
                    }

                if (response.isSuccessful) {
                    task.status = newStatus
                    updateChecklistProgress()
                    onSuccess()
                } else {
                    val errorMsg = "Update failed: HTTP ${response.code()}"
                    onError(errorMsg)
                }
            } catch (e: Exception) {
                onError("Network error: ${e.localizedMessage ?: "Failed to reach server"}")
            }
        }
    }

    private fun filterAndDisplayTasks() {
        val filteredList =
            if (currentQueryText.isBlank()) {
                allTasks
            } else {
                allTasks.filter { task ->
                    task.description.contains(currentQueryText, ignoreCase = true) ||
                        (task.title != null && task.title.contains(currentQueryText, ignoreCase = true)) ||
                        task.taskId.contains(currentQueryText, ignoreCase = true)
                }
            }

        if (filteredList.isEmpty() && allTasks.isNotEmpty()) {
            taskListAdapter?.updateTaskList(emptyList())
            showEmptyState("No tasks match your search.")
        } else if (allTasks.isEmpty()) {
            showEmptyState()
        } else {
            showSuccessState()
            taskListAdapter?.updateTaskList(filteredList)
            updateChecklistProgress()
        }
    }

    private fun updateChecklistProgress() {
        val completedCount = allTasks.count { it.completed }
        progressTextView.text = "Checklist $completedCount / ${allTasks.size}"
    }

    private fun showLoadingState() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        emptyStateLayout.visibility = View.GONE
        errorStateLayout.visibility = View.GONE
    }

    private fun showSuccessState() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        emptyStateLayout.visibility = View.GONE
        errorStateLayout.visibility = View.GONE
        updateChecklistProgress()
    }

    private fun showEmptyState(message: String = "No tasks assigned.") {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        emptyStateLayout.visibility = View.VISIBLE
        errorStateLayout.visibility = View.GONE
        val emptyText: TextView = emptyStateLayout.findViewById(R.id.empty_state_text)
        emptyText.text = message
        progressTextView.text = "Checklist 0 / ${allTasks.size}"
    }

    private fun showErrorState(message: String) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        emptyStateLayout.visibility = View.GONE
        errorStateLayout.visibility = View.VISIBLE
        errorStateText.text = message
    }

    private fun fetchDataFromFirebaseFallback() {
        firebaseQuery = FirebaseDatabase.getInstance().reference.child("caretaker_tasks")
        firebaseQuery?.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val taskList = mutableListOf<Task>()
                    for (taskSnapshot in dataSnapshot.children) {
                        val task = taskSnapshot.getValue(Task::class.java)
                        if (task != null) {
                            if (task.taskId.isBlank()) {
                                task.taskId = taskSnapshot.key.orEmpty()
                            }
                            taskList.add(task)
                        }
                    }
                    allTasks.clear()
                    allTasks.addAll(taskList)
                    if (allTasks.isEmpty()) {
                        showEmptyState()
                    } else {
                        showSuccessState()
                        filterAndDisplayTasks()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    showErrorState("Failed to load tasks: ${databaseError.message}")
                }
            },
        )
    }
}
