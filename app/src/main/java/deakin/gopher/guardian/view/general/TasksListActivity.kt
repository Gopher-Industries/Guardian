package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private lateinit var taskListAdapter: TaskListAdapter
    private lateinit var query: Query

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks_list)

        val taskListRecyclerView: RecyclerView = findViewById(R.id.task_list_recycleView)
        val taskSearchView: SearchView = findViewById(R.id.task_list_searchView)

        // Initialize the TaskListAdapter
        taskListAdapter = TaskListAdapter()
        taskListRecyclerView.layoutManager = GridLayoutManager(this, 1)
        taskListRecyclerView.adapter = taskListAdapter

        // Get reference to the Firebase Database
        val databaseReference = FirebaseDatabase.getInstance().reference.child("nurse-tasks")

        // Set up initial query to fetch all tasks
        query = databaseReference.orderByChild("description")

        // Attach a ValueEventListener to update the list when data changes in the Firebase Database
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val tasks = mutableListOf<Task>()
                for (taskSnapshot in dataSnapshot.children) {
                    val task = taskSnapshot.getValue(Task::class.java)
                    task?.let {
                        tasks.add(it)
                    }
                }
                taskListAdapter.updateData(tasks)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Error loading tasks from Firebase Database", databaseError.toException())
                // Handle error (e.g., show error message to the user)
            }
        })

        taskSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
//                    val filteredTasks = tasks.filter { task ->
//                        task.description.contains(newText, ignoreCase = true)
//                    }
//                    taskListAdapter.updateData(filteredTasks)
                }
                return true
            }
        })
    }

    companion object {
        private const val TAG = "TasksListActivity"
    }
}

