package deakin.gopher.guardian.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import deakin.gopher.guardian.view.GP.GPAddFragment
import deakin.gopher.guardian.view.nextofkin.NoKAddFragment
import deakin.gopher.guardian.view.patient.PatientAddFragment

class PatientProfileAddAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PatientAddFragment()
            1 -> NoKAddFragment(1)
            2 -> NoKAddFragment(2)
            3 -> GPAddFragment(1)
            else -> GPAddFragment(2)
        }
    }

    override fun getItemCount(): Int {
        return 5
    }
}
