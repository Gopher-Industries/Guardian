package deakin.gopher.guardian.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import deakin.gopher.guardian.view.patient.patientdata.medicaldiagnostics.CurrentMedicalDiagnosticsFragment
import deakin.gopher.guardian.view.patient.patientdata.medicaldiagnostics.MedicalDiagnosticsFragment
import deakin.gopher.guardian.view.patient.patientdata.medicaldiagnostics.PastMedicalDiagnosticsFragment

class MedicalDiagnosticsViewPagerAdapter(
    private val patientId: String,
    private val parentFragment: MedicalDiagnosticsFragment,
) : FragmentStateAdapter(
        parentFragment,
    ) {
    override fun createFragment(position: Int): Fragment {
        if (position == 0) {
            val currentFragment = CurrentMedicalDiagnosticsFragment(patientId)
            parentFragment.currentFragment = currentFragment
            return currentFragment
        }
        val pastFragment = PastMedicalDiagnosticsFragment(patientId)
        parentFragment.pastFragment = pastFragment
        return pastFragment
    }

    override fun getItemCount(): Int {
        return 2
    }
}
