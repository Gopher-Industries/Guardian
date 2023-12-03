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
import deakin.gopher.guardian.services.HeartRateDataService
import deakin.gopher.guardian.view.patient.patientdata.heartrate.ui.screens.AddHeartRateScreen
import deakin.gopher.guardian.view.patient.patientdata.heartrate.ui.screens.HeartRateOverviewScreen
import deakin.gopher.guardian.view.theme.GuardianTheme

class HeartRateActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuardianTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    HeartRateNavHost()
                }
            }
        }
    }
}

@Composable
@Suppress("ktlint:standard:function-naming")
fun HeartRateNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = HeartRateRoutes.HeartRateOverview.toString(),
    ) {
        composable(HeartRateRoutes.HeartRateOverview.route) {
            HeartRateOverviewScreen(
                heartRates = HeartRateDataService.generateTestData(20),
                navController,
            )
        }
        composable(HeartRateRoutes.AddHeartRate.route) { AddHeartRateScreen(navController) }
    }
}
