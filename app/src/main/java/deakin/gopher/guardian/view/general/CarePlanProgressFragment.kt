package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import deakin.gopher.guardian.R

class CarePlanProgressFragment(private val patientId: String) : Fragment() {

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
        // Add logic to fetch and display care plan progress using `patientId`
    }
}
