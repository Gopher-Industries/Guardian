package deakin.gopher.guardian

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Host activity for the patient exercise modules
 */
class PatientExerciseModules : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_exercise_container)

        if (savedInstanceState == null) {
            val fragment = PatientExercisePortalFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}
