package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
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
    var taskListAdapter: TaskListAdapter? = null
    var query: Query? = null
    var overviewCardview: CardView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks_list)
        val taskListRecyclerView: RecyclerView = findViewById(R.id.task_list_recycleView)
        overviewCardview = findViewById(R.id.task_list_task_overview)
        val taskSearchView: SearchView = findViewById(R.id.task_list_searchView)

        val addTaskButton: Button = findViewById(R.id.add_task_button)
        addTaskButton.setOnClickListener {
            NavigationService(this).onLaunchTaskCreator()
        }

        query = FirebaseDatabase.getInstance().reference.child("tasks")

        val allOptions =
            FirebaseRecyclerOptions.Builder<Task>()
                .setQuery(query!!, Task::class.java)
                .build()

        val taskListAdapterDefault = TaskListAdapter(this@TasksListActivity, allOptions)
        taskListRecyclerView.layoutManager = GridLayoutManager(this@TasksListActivity, 1)
        taskListRecyclerView.adapter = taskListAdapterDefault
        taskListAdapterDefault.startListening()

        taskSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String?): Boolean {
                query = if (s.isNullOrEmpty()) {
                    FirebaseDatabase.getInstance().reference.child("tasks")
                } else {
                    FirebaseDatabase.getInstance()
                        .reference
                        .child("tasks")
                        .orderByChild("description")
                        .startAt(s)
                        .endAt(s + "\uf8ff")
                        .limitToFirst(10)
                }

                val options = FirebaseRecyclerOptions.Builder<Task>()
                    .setQuery(query!!, Task::class.java)
                    .build()

                taskListAdapter = TaskListAdapter(this@TasksListActivity, options)
                query!!.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            taskListAdapter = TaskListAdapter(this@TasksListActivity, options)
                            taskListRecyclerView.layoutManager =
                                GridLayoutManager(this@TasksListActivity, 1)
                            taskListRecyclerView.adapter = taskListAdapter
                            taskListAdapter!!.startListening()
                        } else {
                            taskListRecyclerView.layoutManager =
                                GridLayoutManager(this@TasksListActivity, 1)
                            taskListRecyclerView.adapter =
                                TaskListAdapter(this@TasksListActivity, options)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })

                return true
                }
            },
        )
    }
}
