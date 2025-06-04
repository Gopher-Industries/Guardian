package com.example.guardionmonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Theme
import com.example.guardionmonitor.ui.theme.GuardionMonitorTheme

// Screens
import com.example.guardionmonitor.WelcomeScreen
import com.example.guardionmonitor.RoleSelectionScreen
import com.example.guardionmonitor.DoctorLoginPage
import com.example.guardionmonitor.DoctorHomeScreen
import com.example.guardionmonitor.PatientReportScreen
import com.example.guardionmonitor.MedicalSummaryScreen
import com.example.guardionmonitor.AssignNurseScreen
import com.example.guardionmonitor.ActivityLogScreen
import com.example.guardionmonitor.AppointmentScreen
import com.example.guardionmonitor.PrescriptionScreen
import com.example.guardionmonitor.BillingScreen
import com.example.guardionmonitor.SignOutScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuardionMonitorTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "welcome") {

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
