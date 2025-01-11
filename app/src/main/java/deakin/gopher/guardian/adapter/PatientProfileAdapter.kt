package deakin.gopher.guardian.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import deakin.gopher.guardian.view.general.CarePlanProgressFragment
import deakin.gopher.guardian.view.general.HealthDataForViewFragment
import deakin.gopher.guardian.view.general.TaskHistoryFragment
import deakin.gopher.guardian.view.gp.GPProfileFragment
import deakin.gopher.guardian.view.nextofkin.NextOfKinFragment
import deakin.gopher.guardian.view.patient.careplan.CarePlanSummaryActivityFragment
import deakin.gopher.guardian.view.patient.patientdata.medicaldiagnostics.MedicalDiagnosticsFragment
import deakin.gopher.guardian.view.patient.patientdata.patient.PatientProfileFragment

class PatientProfileAdapter(
    private val patientId: String,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PatientProfileFragment()
            1 -> NextOfKinFragment()
            2 -> GPProfileFragment()
            3 -> MedicalDiagnosticsFragment()
            4 -> HealthDataForViewFragment()
            5 -> CarePlanSummaryActivityFragment()
            6 -> TaskHistoryFragment()
            7 -> CarePlanProgressFragment()
            else -> HealthDataForViewFragment()
        }.apply {
            arguments = Bundle().apply { putString("patientId", patientId) }
        }
    }

    override fun getItemCount(): Int {
        return 8 // Total fragments
    }
}
