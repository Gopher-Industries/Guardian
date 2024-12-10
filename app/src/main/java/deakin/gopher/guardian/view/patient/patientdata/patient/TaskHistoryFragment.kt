package deakin.gopher.guardian.view.patient.taskhistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.TaskHistoryAdapter

class TaskHistoryFragment : Fragment() {

    private lateinit var taskHistoryRecyclerView: RecyclerView
    private lateinit var taskHistoryAdapter: TaskHistoryAdapter
    private val taskList = mutableListOf<String>() // Replace with your data model if needed

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_task_history, container, false)
        taskHistoryRecyclerView = rootView.findViewById(R.id.recycler_view_task_history)

        // Initialize the adapter and RecyclerView
        taskHistoryAdapter = TaskHistoryAdapter(taskList)
        taskHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        taskHistoryRecyclerView.adapter = taskHistoryAdapter

        loadTaskHistory()

        return rootView
    }

    private fun loadTaskHistory() {
        // Example logic to load tasks for the patient (replace with your data source)
        taskList.clear()
        taskList.add("Appointment with Dr. Smith - 2024-12-02")
        taskList.add("Blood Test Scheduled - 2024-12-05")
        taskList.add("Follow-up on Medication - 2024-12-10")

        // Notify the adapter that the data has changed
        taskHistoryAdapter.notifyDataSetChanged()
    }
}
