package deakin.gopher.guardian

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import deakin.gopher.guardian.view.theme.GuardianTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuardianTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    val startRoute =
                        intent?.getStringExtra("startRoute")
                            ?.takeIf { it.isNotBlank() }
                            ?: "welcome"
                    NavHost(navController = navController, startDestination = startRoute) {
                        // 1. Welcome
                        composable("welcome") {
                            WelcomeScreen(navController)
                        }

                        // 2. Role selection
                        composable("role_selection") {
                            RoleSelectionScreen(navController)
                        }

                        // 3. Doctor login
                        composable("doctor_login") {
                            DoctorLoginPage(navController)
                        }

                        // 4. Doctor home page
                        composable("doctor_home") {
                            DoctorHomeScreen(navController)
                        }

                        // 5. Patient report
                        composable("patient_report") {
                            PatientReportScreen(navController)
                        }

                        // 6. Medical summary for William S
                        composable("medical_summary") {
                            MedicalSummaryScreen(navController, patientName = "William S")
                        }

                        // 7. Assign nurse
                        composable("assign_nurse") {
                            AssignNurseScreen(navController)
                        }

                        // 8. Activity log
                        composable("activity_log") {
                            ActivityLogScreen(navController)
                        }

                        // 9. Appointment
                        composable("appointment") {
                            AppointmentScreen(navController)
                        }

                        // 10. Prescription
                        composable("prescription") {
                            PrescriptionScreen(navController)
                        }

                        composable(
                            route = "patient_prescriptions/{patientId}/{patientName}",
                            arguments =
                                listOf(
                                    navArgument("patientId") { type = NavType.StringType },
                                    navArgument("patientName") { type = NavType.StringType },
                                ),
                        ) { backStackEntry ->
                            val patientId =
                                backStackEntry.arguments?.getString("patientId").orEmpty()
                            val patientName =
                                Uri.decode(
                                    backStackEntry.arguments?.getString("patientName").orEmpty(),
                                )
                            PatientPrescriptionsScreen(
                                navController = navController,
                                patientId = patientId,
                                patientName = patientName,
                            )
                        }

                        composable(
                            route = "prescription_detail/{prescriptionId}/{patientName}",
                            arguments =
                                listOf(
                                    navArgument("prescriptionId") { type = NavType.StringType },
                                    navArgument("patientName") { type = NavType.StringType },
                                ),
                        ) { backStackEntry ->
                            val prescriptionId =
                                backStackEntry.arguments?.getString("prescriptionId").orEmpty()
                            val patientName =
                                Uri.decode(
                                    backStackEntry.arguments?.getString("patientName").orEmpty(),
                                )
                            PrescriptionDetailScreen(
                                navController = navController,
                                prescriptionId = prescriptionId,
                                patientName = patientName,
                            )
                        }

                        // 11. Billing
                        composable("billing") {
                            BillingScreen(navController)
                        }

                        // 12. Sign out
                        composable("sign_out") {
                            SignOutScreen(navController)
                        }
                    }
                }
            }
        }
    }
}
