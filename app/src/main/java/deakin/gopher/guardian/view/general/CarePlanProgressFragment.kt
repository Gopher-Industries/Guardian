package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.CarePlanProgressAdapter

class CarePlanProgressFragment(private val patientId: String) : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CarePlanProgressAdapter

    // Sample data for testing
    private val progressList = listOf(
        "Milestone 1: Completed",
        "Milestone 2: In Progress",
        "Milestone 3: Pending"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for Care Plan Progress
        return inflater.inflate(R.layout.fragment_care_plan_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_care_plan_progress)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Set up adapter
        adapter = CarePlanProgressAdapter(progressList) // Replace with actual data fetched using `patientId`
        recyclerView.adapter = adapter
    }
}

