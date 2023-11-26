package deakin.gopher.guardian.view.general

import android.os.Bundle
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
import deakin.gopher.guardian.adapter.PatientListAdapter
import deakin.gopher.guardian.model.Patient

class TasksListActivity : AppCompatActivity() {
    var taskListAdapter: PatientListAdapter? = null
    var query: Query? = null
    var overviewCardview: CardView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks_list)
        val taskListRecyclerView: RecyclerView = findViewById(R.id.task_list_recycleView)
        overviewCardview = findViewById(R.id.task_list_task_overview)
        val taskSearchView: SearchView = findViewById(R.id.task_list_searchView)

        val allQuery: Query = FirebaseDatabase.getInstance().reference.child("task_profile")
        val allOptions =
            FirebaseRecyclerOptions.Builder<Patient>()
                .setQuery(
                    allQuery,
                ) { snapshot: DataSnapshot ->
                    val firstname =
                        if (null == snapshot.child("first_name").value) "" else snapshot.child("first_name").value.toString()
                    val middlename =
                        if (null == snapshot.child("middle_name").value) "" else snapshot.child("middle_name").value.toString()
                    val lastname =
                        if (null == snapshot.child("last_name").value) "" else snapshot.child("last_name").value.toString()
                    val task = Patient(snapshot.key, firstname, lastname)
                    if ("" !== middlename) task.middleName = middlename
                    task
                }
                .build()
        val taskListAdapterDefault = PatientListAdapter(this@TasksListActivity, allOptions)
        taskListRecyclerView.setLayoutManager(GridLayoutManager(this@TasksListActivity, 1))
        taskListRecyclerView.setAdapter(taskListAdapterDefault)
        taskListAdapterDefault.startListening()
        taskSearchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(s: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(s: String): Boolean {
                    query =
                        if (s.isEmpty()) {
                            FirebaseDatabase.getInstance().reference.child("task_profile")
                        } else {
                            FirebaseDatabase.getInstance()
                                .reference
                                .child("task_profile")
                                .orderByChild("first_name")
                                .startAt(s)
                                .endAt(s + "\uf8ff")
                                .limitToFirst(10)
                        }
                    val options =
                        FirebaseRecyclerOptions.Builder<Patient>()
                            .setQuery(
                                query!!,
                            ) { snapshot: DataSnapshot ->
                                val task =
                                    Patient(
                                        snapshot.key,
                                        snapshot.child("first_name").value.toString(),
                                        snapshot.child("last_name").value.toString(),
                                    )
                                val middleName = snapshot.child("middle_name").value
                                if (null != middleName) task.middleName = middleName.toString()
                                task
                            }
                            .build()
                    taskListAdapter = PatientListAdapter(this@TasksListActivity, options)
                    query!!.addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    taskListAdapter =
                                        PatientListAdapter(this@TasksListActivity, options)
                                    taskListRecyclerView.setLayoutManager(
                                        GridLayoutManager(this@TasksListActivity, 1),
                                    )
                                    taskListRecyclerView.setAdapter(taskListAdapter)
                                    taskListAdapter!!.startListening()
                                } else {
                                    taskListRecyclerView.setLayoutManager(
                                        GridLayoutManager(this@TasksListActivity, 1),
                                    )
                                    taskListRecyclerView.setAdapter(
                                        PatientListAdapter(this@TasksListActivity, options),
                                    )
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        },
                    )
                    return true
                }
            },
        )
    }
}
