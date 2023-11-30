package deakin.gopher.guardian.view.patient.patientdata.heartrate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import deakin.gopher.guardian.model.health.HeartRate
import deakin.gopher.guardian.view.patient.patientdata.heartrate.ui.screens.AddHeartRateScreen
import deakin.gopher.guardian.view.patient.patientdata.heartrate.ui.screens.HeartRateOverviewScreen
import deakin.gopher.guardian.view.theme.GuardianTheme
import java.util.Date
import java.util.UUID

class HeartRateActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuardianTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HeartRateNavHost()
                }
            }
        }
    }
}

@Composable
fun HeartRateNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = HeartRateRoutes.HeartRateOverview.toString()
    ) {
        composable(HeartRateRoutes.HeartRateOverview.toString()) {
            HeartRateOverviewScreen(
                heartRates = listOf(
                    HeartRate(
                        heartRateId = UUID.randomUUID(),
                        patientId = UUID.randomUUID(),
                        measurement = 100,
                        measurementDate = Date()
                    ),
                    HeartRate(
                        heartRateId = UUID.randomUUID(),
                        patientId = UUID.randomUUID(),
                        measurement = 40,
                        measurementDate = Date()
                    ),
                ),
                navController
            )
        }
        composable(HeartRateRoutes.AddHeartRate.toString()) { AddHeartRateScreen() }
    }
}
