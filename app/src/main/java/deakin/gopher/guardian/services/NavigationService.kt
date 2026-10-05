package deakin.gopher.guardian.services

import android.app.Activity
import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import deakin.gopher.guardian.PatientExerciseModules
import deakin.gopher.guardian.TrainingActivity
import deakin.gopher.guardian.model.login.Role
import deakin.gopher.guardian.view.caretaker.CaretakerProfileActivity
import deakin.gopher.guardian.view.falldetection.FallDetectionActivity
import deakin.gopher.guardian.view.general.Homepage4admin
import deakin.gopher.guardian.view.general.Homepage4caretaker
import deakin.gopher.guardian.view.general.Homepage4doctor
import deakin.gopher.guardian.view.general.Homepage4nurse
import deakin.gopher.guardian.view.general.LoginActivity
import deakin.gopher.guardian.view.general.NurseRosterActivity
import deakin.gopher.guardian.view.general.PatientListActivity
import deakin.gopher.guardian.view.general.PatientProfileAddActivity
import deakin.gopher.guardian.view.general.RegisterActivity
import deakin.gopher.guardian.view.general.Setting
import deakin.gopher.guardian.view.general.TaskAddActivity
import deakin.gopher.guardian.view.general.TasksListActivity
import deakin.gopher.guardian.view.patient.PatientLogsActivity
import deakin.gopher.guardian.view.patient.dailyreport.DailyReportActivity

class NavigationService(val activity: Activity) {
    fun toHomeScreenForRole(role: Role) {
        val intent =
            when (role) {
                Role.Caretaker -> Intent(activity, Homepage4caretaker::class.java)
                Role.Nurse -> Intent(activity, Homepage4nurse::class.java)
                Role.Admin -> Intent(activity, Homepage4admin::class.java)
                Role.Doctor -> Intent(activity, Homepage4doctor::class.java)
            }
        // Clear back stack so user cannot go back to Login/PIN screens
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
        activity.finish()
    }

    fun toRegistration() {
        activity.startActivity(
            Intent(
                activity,
                RegisterActivity::class.java,
            ),
        )
    }

    fun onSettings() {
        activity.startActivity(
            Intent(
                activity,
                Setting::class.java,
            ),
        )
    }

    fun onPatientLogs() {
        activity.startActivity(
            Intent(
                activity.applicationContext,
                PatientLogsActivity::class.java,
            ),
        )
    }

    fun onSignOut() {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
        activity.finish()
    }

    fun onLaunchPatientList() {
        activity.startActivity(
            Intent(
                activity,
                PatientListActivity::class.java,
            ),
        )
    }

    fun onLaunchAddPatient() {
        activity.startActivity(
            Intent(
                activity,
                PatientProfileAddActivity::class.java,
            ),
        )
    }

    fun onLaunchDailyReport() {
        activity.startActivity(
            Intent(
                activity,
                DailyReportActivity::class.java,
            ),
        )
    }

    fun onLaunchNurseRoster() {
        activity.startActivity(
            Intent(
                activity,
                NurseRosterActivity::class.java,
            ),
        )
    }

    fun onLaunchCaretakerProfile() {
        activity.startActivity(
            Intent(
                activity,
                CaretakerProfileActivity::class.java,
            ),
        )
    }

    @OptIn(UnstableApi::class)
    fun onLaunchMonitoring() {
        activity.startActivity(
            Intent(
                activity,
                FallDetectionActivity::class.java,
            ),
        )
    }

    fun onLaunchTraining() {
        activity.startActivity(
            Intent(
                activity,
                TrainingActivity::class.java,
            ),
        )
    }

    fun onLaunchExercisePortal() {
        activity.startActivity(
            Intent(
                activity,
                PatientExerciseModules::class.java,
            ),
        )
    }

    fun onLaunchTasks() {
        activity.startActivity(
            Intent(
                activity,
                TasksListActivity::class.java,
            ),
        )
    }

    fun onLaunchTaskCreator() {
        activity.startActivity(
            Intent(
                activity,
                TaskAddActivity::class.java,
            ),
        )
    }

    fun toLogin() {
        val intent = Intent(activity, LoginActivity::class.java)
        // If coming from Registration, we want to clear the Registration screen from stack
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        activity.startActivity(intent)
        activity.finish()
    }
}
