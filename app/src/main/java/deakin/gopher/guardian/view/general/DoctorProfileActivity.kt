package deakin.gopher.guardian.view.general

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import deakin.gopher.guardian.DoctorProfileScreen
import deakin.gopher.guardian.view.theme.GuardianTheme

class DoctorProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GuardianTheme {
                DoctorProfileScreen(
                    onBack = {
                        finish()
                    }
                )
            }
        }
    }
}