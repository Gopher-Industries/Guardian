package deakin.gopher.guardian.view.patient.patientdata.medicaldiagnostics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import deakin.gopher.guardian.R

class MedicalDiagnosticsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medical_diagnostics)
        val tabLayout = findViewById<TabLayout>(R.id.medicalDiagnosticsTabLayout)
        val viewPager2 = findViewById<ViewPager2>(R.id.medicalDiagnosticsViewPager)
        TabLayoutMediator(
            tabLayout,
            viewPager2,
        ) { tab: TabLayout.Tab, position: Int ->
            if (0 == position) {
                tab.setText("Current")
            } else {
                tab.setText("Past")
            }
        }
            .attach()
    }
}
