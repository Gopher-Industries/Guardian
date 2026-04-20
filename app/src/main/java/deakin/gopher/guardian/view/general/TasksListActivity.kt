package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.TaskListAdapter
import deakin.gopher.guardian.model.Task
import deakin.gopher.guardian.services.NavigationService

class TasksListActivity : AppCompatActivity() {
    private var taskListAdapter: TaskListAdapter? = null
    private var query: Query? = null
    private var overviewCardview: CardView? = null
    private lateinit var plusButton: ImageButton

    // ADDED: Keep a reference to the Firebase listener to avoid duplicate listeners
    private var taskValueEventListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks_list)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val taskListMenuBtn: ImageView = findViewById(R.id.task_list_manu_button)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        plusButton = findViewById(R.id.imageView62)
        navigationView.setItemIconTintList(null)

        // FIXED: Navigation item listener moved outside menu button click
        // This prevents the listener from being attached again every time the menu button is pressed
        navigationView.setNavigationItemSelectedListener { menuItem: MenuItem ->
            val id = menuItem.itemId
            when (id) {
                R.id.nav_home ->
                    startActivity(
                        Intent(this@TasksListActivity, Homepage4caretaker::class.java),
                    )

//                R.id.nav_settings -> startActivity(
//                    Intent(this@TasksListActivity, Setting::class.java)
//                )

                R.id.add_task ->
                    startActivity(
                        Intent(this@TasksListActivity, TaskAddActivity::class.java),
                    )

                R.id.nav_signout -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this@TasksListActivity, LoginActivity::class.java))
                    finish()
                }
            }
            true
        }

        // SIMPLIFIED: Menu button only opens the drawer
        taskListMenuBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        plusButton.setOnClickListener {
            NavigationService(this).onLaunchTaskCreator()
        }

        val taskListRecyclerView: RecyclerView = findViewById(R.id.task_list_recycleView)
        overviewCardview = findViewById(R.id.task_list_task_overview)
        val taskSearchView: SearchView = findViewById(R.id.task_list_searchView)
        val addTaskButton: Button = findViewById(R.id.add_task_button)

        addTaskButton.setOnClickListener {
            NavigationService(this).onLaunchTaskCreator()
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

                    // REFRESH: Reload Firebase data when search text changes
                    fetchDataFromFirebase()
                    return true
                }
            },
        )
    }

    private fun fetchDataFromFirebase() {
        // ADDED: Remove previous listener before attaching a new one
        // This avoids duplicate callbacks when search text changes
        taskValueEventListener?.let { listener ->
            query?.removeEventListener(listener)
        }

        taskValueEventListener =
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val taskList = mutableListOf<Task>()

                    for (taskSnapshot in dataSnapshot.children) {
                        val task = taskSnapshot.getValue(Task::class.java)
                        if (task != null) {
                            taskList.add(task)
                        }
                    }

                    taskListAdapter?.updateTaskList(taskList)

                    // ADDED: Basic empty-state handling using existing CardView
                    if (taskList.isEmpty()) {
                        overviewCardview?.visibility = View.GONE
                    } else {
                        overviewCardview?.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // ADDED: Show simple error feedback to improve user experience
                    Toast.makeText(
                        this@TasksListActivity,
                        "Failed to load tasks: ${databaseError.message}",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }

        query?.addValueEventListener(taskValueEventListener!!)
    }

    // ADDED: Remove listener when activity is destroyed to improve stability
    override fun onDestroy() {
        super.onDestroy()
        taskValueEventListener?.let { listener ->
            query?.removeEventListener(listener)
        }
    }
}