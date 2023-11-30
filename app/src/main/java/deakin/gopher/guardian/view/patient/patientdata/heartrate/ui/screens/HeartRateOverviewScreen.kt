package deakin.gopher.guardian.view.patient.patientdata.heartrate.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import deakin.gopher.guardian.model.health.HeartRate
import deakin.gopher.guardian.model.health.soryByDate
import deakin.gopher.guardian.view.patient.patientdata.heartrate.HeartRateRoutes
import deakin.gopher.guardian.view.patient.patientdata.heartrate.ui.components.HeartRateChart
import deakin.gopher.guardian.view.theme.GuardianTheme
import java.util.Date
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeartRateOverviewScreen(heartRates: List<HeartRate>, navController: NavController) {
    GuardianTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Heart Rate") },
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Return to Heart Rate Patient",
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    },
                    colors = topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    // popup dialog to add heart rate data including measurement and date time
                    navController.navigate(HeartRateRoutes.AddHeartRate.toString())
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Heart Rate")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                HeartRateChart(heartRates = heartRates)
            }
        }
    }
}

@Preview(
    showBackground = true,
    heightDp = 640,
    widthDp = 360
)
@Composable
fun HeartRateScreenPreview() {
    val heartRates = listOf(
        HeartRate(
            heartRateId = UUID.randomUUID(),
            patientId = UUID.randomUUID(),
            measurement = 100,
            measurementDate = Date()
        ),
        HeartRate(
            heartRateId = UUID.randomUUID(),
            patientId = UUID.randomUUID(),
            measurement = 60,
            measurementDate = Date()
        ),
        HeartRate(
            heartRateId = UUID.randomUUID(),
            patientId = UUID.randomUUID(),
            measurement = 55,
            measurementDate = Date()
        ),
        HeartRate(
            heartRateId = UUID.randomUUID(),
            patientId = UUID.randomUUID(),
            measurement = 120,
            measurementDate = Date()
        ),
    ).soryByDate()

    val navController = rememberNavController()
    HeartRateOverviewScreen(heartRates, navController)
}
