package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
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

class TasksListActivity : AppCompatActivity() {
    private var taskListAdapter: TaskListAdapter? = null
    private var query: Query? = null
    private var overviewCardview: CardView? = null
    private lateinit var plusButton: ImageButton
    private lateinit var progressTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks_list)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val taskListMenuBtn: ImageView = findViewById(R.id.task_list_manu_button)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navigationService = NavigationService(this)
        val canAddTasks = SessionManager.getCurrentUser().role == Role.Caretaker

        plusButton = findViewById(R.id.imageView62)
        progressTextView = findViewById(R.id.task_progress_text)
        navigationView.setItemIconTintList(null)
        navigationView.menu.findItem(R.id.add_task).isVisible = canAddTasks

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

        val taskListRecyclerView: RecyclerView = findViewById(R.id.task_list_recycleView)
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

        // Initialize RecyclerView and Adapter
        taskListRecyclerView.layoutManager = GridLayoutManager(this@TasksListActivity, 1)
        taskListAdapter = TaskListAdapter(mutableListOf())
        taskListRecyclerView.adapter = taskListAdapter

        // Set up Firebase query
        query = FirebaseDatabase.getInstance().reference.child("caretaker_tasks")

        // Fetch and display data from Firebase
        fetchDataFromFirebase()

        taskSearchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(s: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(s: String?): Boolean {
                    query =
                        if (s.isNullOrEmpty()) {
                            FirebaseDatabase.getInstance().reference.child("caretaker_tasks")
                        } else {
                            FirebaseDatabase.getInstance()
                                .reference
                                .child("caretaker_tasks")
                                .orderByChild("description")
                                .startAt(s)
                                .endAt(s + "\uf8ff")
                                .limitToFirst(10)
                        }
                    fetchDataFromFirebase()
                    return true
                }
            },
        )
    }

    private fun fetchDataFromFirebase() {
        query?.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val taskList = mutableListOf<Task>()
                    var completedCount = 0
                    for (taskSnapshot in dataSnapshot.children) {
                        val task = taskSnapshot.getValue(Task::class.java)
                        if (task != null) {
                            taskList.add(task)
                            if (task.completed) {
                                completedCount++
                            }
                        }
                    }
                    taskListAdapter?.updateTaskList(taskList)
                    progressTextView.text = "Checklist $completedCount / ${taskList.size}"
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle possible errors.
                }
            },
        )
    }
}
