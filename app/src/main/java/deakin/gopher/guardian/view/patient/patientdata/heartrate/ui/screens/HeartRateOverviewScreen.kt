package deakin.gopher.guardian.view.patient.patientdata.heartrate.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import deakin.gopher.guardian.model.health.HeartRate
import deakin.gopher.guardian.model.health.average
import deakin.gopher.guardian.model.health.sortByDate
import deakin.gopher.guardian.services.HeartRateDataService
import deakin.gopher.guardian.view.patient.patientdata.heartrate.HeartRateRoutes
import deakin.gopher.guardian.view.patient.patientdata.heartrate.ui.components.HeartRateChart
import deakin.gopher.guardian.view.theme.GuardianTheme

@Suppress("ktlint:standard:function-naming")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeartRateOverviewScreen(
    heartRates: List<HeartRate>,
    navController: NavController,
) {
    GuardianTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Heart Rate") },
                    navigationIcon = {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Return to Heart Rate Patient",
                                modifier = Modifier.padding(horizontal = 12.dp),
                            )
                        }
                    },
                    colors =
                        topAppBarColors(
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
            },
            bottomBar = {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                ) {
                    Text(
                        text = "Patient Average: ${heartRates.average()}",
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            },
        ) { innerPadding ->
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
            ) {
                HeartRateChart(heartRates = heartRates)
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Preview(
    showBackground = true,
    heightDp = 640,
    widthDp = 360,
)
@Composable
fun HeartRateScreenPreview() {
    val heartRates = HeartRateDataService.generateTestData(20).sortByDate()
    val navController = rememberNavController()
    HeartRateOverviewScreen(heartRates, navController)
}
