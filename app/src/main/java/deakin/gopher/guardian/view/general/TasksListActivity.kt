package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.TaskListAdapter
import deakin.gopher.guardian.model.Task
import deakin.gopher.guardian.services.NavigationService

class TasksListActivity : AppCompatActivity() {
    private var taskListAdapter: TaskListAdapter? = null
    private var query: Query? = null
    private var overviewCardview: CardView? = null

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

        taskListAdapter = TaskListAdapter(getTestData())
        taskListRecyclerView.layoutManager = GridLayoutManager(this@TasksListActivity, 1)
        taskListRecyclerView.adapter = taskListAdapter

        taskSearchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(s: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(s: String?): Boolean {
                    query =
                        if (s.isNullOrEmpty()) {
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

                    return true
                }
            },
        )
    }

    private fun getTestData(): List<Task> {
        return listOf(
            Task("1", "Task 1 Description", "Patient 1"),
            Task("2", "Task 2 Description", "Patient 2"),
        )
    }
}
