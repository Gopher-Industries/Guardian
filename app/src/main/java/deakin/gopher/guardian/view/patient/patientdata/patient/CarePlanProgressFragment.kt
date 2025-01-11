package deakin.gopher.guardian.view.patient.careplanprogress

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

    private lateinit var carePlanProgressRecyclerView: RecyclerView
    private lateinit var carePlanProgressAdapter: CarePlanProgressAdapter
    private val progressList = mutableListOf<String>() // Replace with actual care plan progress data

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_care_plan_progress, container, false)
        carePlanProgressRecyclerView = rootView.findViewById(R.id.recycler_view_care_plan_progress)

        // Initialize the adapter and RecyclerView
        carePlanProgressAdapter = CarePlanProgressAdapter(progressList)
        carePlanProgressRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        carePlanProgressRecyclerView.adapter = carePlanProgressAdapter

        loadCarePlanProgress()

        return rootView
    }

    private fun loadCarePlanProgress() {
        // Example logic to load care plan progress (replace with actual data)
        progressList.clear()
        progressList.add("Therapy session completed - 2024-12-01")
        progressList.add("Blood pressure medication - 2024-12-03")
        progressList.add("Follow-up on physiotherapy - 2024-12-08")

        // Notify the adapter that the data has changed
        carePlanProgressAdapter.notifyDataSetChanged()
    }
}
