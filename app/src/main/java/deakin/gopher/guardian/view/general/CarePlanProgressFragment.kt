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

class CarePlanProgressFragment : Fragment() {

    private var patientId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        patientId = arguments?.getString("patientId")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_care_plan_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_care_plan_progress)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val progressList = listOf(
            "Milestone 1: Completed",
            "Milestone 2: In Progress",
            "Milestone 3: Pending",
        ) // Replace with actual API data using patientId

        recyclerView.adapter = CarePlanProgressAdapter(progressList)
    }
}
