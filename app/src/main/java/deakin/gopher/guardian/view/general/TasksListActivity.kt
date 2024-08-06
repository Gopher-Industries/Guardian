package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.TaskListAdapter
import deakin.gopher.guardian.model.Task
import deakin.gopher.guardian.services.NavigationService

class TasksListActivity : AppCompatActivity() {
    private var taskListAdapter: TaskListAdapter? = null
    private var query: Query? = null
    private var overviewCardview: CardView? = null
    private lateinit var plusButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks_list)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val taskListMenuBtn: ImageView = findViewById(R.id.task_list_manu_button)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        plusButton = findViewById(R.id.imageView62)
        navigationView.setItemIconTintList(null)
        taskListMenuBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
            navigationView.setNavigationItemSelectedListener { menuItem: MenuItem ->
                val id = menuItem.itemId
                when (id) {
                    R.id.nav_home -> startActivity(
                        Intent(this@TasksListActivity, Homepage4caretaker::class.java)
                    )
//                    R.id.nav_settings -> startActivity(
//                        Intent(this@TasksListActivity, Setting::class.java)
//                    )
                    R.id.add_task -> startActivity(
                        Intent(this@TasksListActivity, TaskAddActivity::class.java)
                    )
                    R.id.nav_signout -> {
                        FirebaseAuth.getInstance().signOut()
                        startActivity(Intent(this@TasksListActivity, LoginActivity::class.java))
                        finish()
                    }
                }
                true
            }
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

        taskSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String?): Boolean {
                query = if (s.isNullOrEmpty()) {
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
        })
    }

    private fun fetchDataFromFirebase() {
        query?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val taskList = mutableListOf<Task>()
                for (taskSnapshot in dataSnapshot.children) {
                    val task = taskSnapshot.getValue(Task::class.java)
                    if (task != null) {
                        taskList.add(task)
                    }
                }
                taskListAdapter?.updateTaskList(taskList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
            }
        })
    }
}
