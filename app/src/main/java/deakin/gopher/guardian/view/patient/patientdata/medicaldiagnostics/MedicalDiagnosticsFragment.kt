package deakin.gopher.guardian.view.patient.patientdata.medicaldiagnostics

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.MedicalDiagnosticsViewPagerAdapter
import deakin.gopher.guardian.view.patient.patientdata.heartrate.HeartRateActivity

class MedicalDiagnosticsFragment : Fragment {
    @JvmField
    var currentFragment: CurrentMedicalDiagnosticsFragment? = null

    @JvmField
    var pastFragment: PastMedicalDiagnosticsFragment? = null
    private var editButton: Button? = null
    private var isEditable = false
    private var patientId: String? = null

    constructor()
    constructor(patientId: String?) {
        this.patientId = patientId
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_medical_diagnostics, container, false)

        val editButton: Button = view.findViewById(R.id.header_edit_button)
        val heartRateButton: Button = view.findViewById(R.id.heart_rate_button)
        val tabLayout = view.findViewById<TabLayout>(R.id.medicalDiagnosticsTabLayout)
        val viewPager2 = view.findViewById<ViewPager2>(R.id.medicalDiagnosticsViewPager)
        val viewPagerAdapter = MedicalDiagnosticsViewPagerAdapter(patientId, this)
        viewPager2.adapter = viewPagerAdapter

        TabLayoutMediator(
            tabLayout,
            viewPager2,
        ) { tab: TabLayout.Tab, position: Int ->
            if (0 == position) {
                tab.text = "Current"
            } else {
                tab.text = "Past"
            }
        }
            .attach()

        heartRateButton.setOnClickListener {
            Intent(this.context, HeartRateActivity::class.java).also {
                startActivity(it)
            }
        }

        editButton.setOnClickListener {
            if (isEditable) {
                editButton.setBackgroundResource(R.drawable.medical_diagnostics_edit)
            } else {
                editButton.setBackgroundResource(R.drawable.medical_diagnostics_stop)
            }
            handleEditButtonClick()
        }
        return view
    }

    private fun handleEditButtonClick() {
        isEditable = !isEditable
        if (null != currentFragment) {
            currentFragment!!.setEditState(isEditable)
        }
        if (null != pastFragment) {
            pastFragment!!.setEditState(isEditable)
        }
    }
}
