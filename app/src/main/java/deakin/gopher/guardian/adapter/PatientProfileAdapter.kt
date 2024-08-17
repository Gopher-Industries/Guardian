package deakin.gopher.guardian.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import deakin.gopher.guardian.view.GP.GPProfileFragment
import deakin.gopher.guardian.view.general.HealthDataForViewFragment
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
            0 -> {
                val fragment = PatientProfileFragment()
                val bundle =
                    Bundle().apply {
                        putString("patientId", patientId)
                    }
                fragment.arguments = bundle
                fragment
            }
            1 -> NextOfKinFragment()
            2 -> GPProfileFragment()
            3 -> MedicalDiagnosticsFragment(patientId)
            4 -> HealthDataForViewFragment()
            5 -> CarePlanSummaryActivityFragment(patientId)
            else -> HealthDataForViewFragment()
        }
    }

    override fun getItemCount(): Int {
        return 6
    }
}
