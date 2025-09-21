//package deakin.gopher.guardian
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import deakin.gopher.guardian.view.theme.GuardianTheme
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            GuardianTheme {
//                Surface(color = MaterialTheme.colorScheme.background) {
//                    val navController = rememberNavController()
//                    NavHost(navController = navController, startDestination = "welcome") {
//                        composable("welcome") { WelcomeScreen(navController) }
//                        composable("role_selection") { RoleSelectionScreen(navController) }
//                        composable("doctor_login") { DoctorLoginPage(navController) }
//                        composable("doctor_home") { DoctorHomeScreen(navController) }
//                        composable("patient_report") { PatientReportScreen(navController) }
//                        composable("medical_summary") {
//                            MedicalSummaryScreen(navController, patientName = "William S")
//                        }
//                        composable("assign_nurse") { AssignNurseScreen(navController) }
//                        composable("activity_log") { ActivityLogScreen(navController) }
//                        composable("appointment") { AppointmentScreen(navController) }
//                        composable("prescription") { PrescriptionScreen(navController) }
//                        composable("billing") { BillingScreen(navController) }
//                        composable("sign_out") { SignOutScreen(navController) }
//                        // 🚨 Removed: composable("edit_profile") { EditCaretakerProfileActivity(navController) }
//                    }
//                }
//            }
//        }
//    }
//}

package deakin.gopher.guardian

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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

                    // Navigation Graph
                    NavHost(
                        navController = navController,
                        startDestination = "welcome"
                    ) {
                        composable("welcome") {
                            WelcomeScreen(navController)
                        }
                        composable("role_selection") {
                            RoleSelectionScreen(navController)
                        }
                        composable("doctor_login") {
                            DoctorLoginPage(navController)
                        }
                        composable("doctor_home") {
                            DoctorHomeScreen(navController)
                        }
                        composable("patient_report") {
                            PatientReportScreen(navController)
                        }
                        composable("medical_summary") {
                            MedicalSummaryScreen(navController, patientName = "William S")
                        }
                        composable("assign_nurse") {
                            AssignNurseScreen(navController)
                        }
                        composable("activity_log") {
                            ActivityLogScreen(navController)
                        }
                        composable("appointment") {
                            AppointmentScreen(navController)
                        }

                        // Prescription screen (now ready for dynamic patient data later)
                        composable("prescription") {
                            PrescriptionScreen(navController)
                        }

                        composable("billing") {
                            BillingScreen(navController)
                        }
                        composable("sign_out") {
                            SignOutScreen(navController)
                        }

                        // Placeholder for edit profile - to be updated later if needed
                        // 🚨 Removed previously to avoid crash
                        // composable("edit_profile") { EditCaretakerProfileActivity(navController) }
                    }
                }
            }
        }
    }
}
