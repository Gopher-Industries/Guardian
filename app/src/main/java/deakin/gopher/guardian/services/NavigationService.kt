package deakin.gopher.guardian.services

import android.app.Activity
import android.content.Intent
import deakin.gopher.guardian.model.login.Role
import deakin.gopher.guardian.view.general.Homepage4admin
import deakin.gopher.guardian.view.general.Homepage4caretaker
import deakin.gopher.guardian.view.general.Homepage4doctor
import deakin.gopher.guardian.view.general.Homepage4nurse
import deakin.gopher.guardian.view.general.LoginActivity
import deakin.gopher.guardian.view.general.PatientListActivity
import deakin.gopher.guardian.view.general.PinCodeActivity
import deakin.gopher.guardian.view.general.RegisterActivity
import deakin.gopher.guardian.view.general.Setting
import deakin.gopher.guardian.view.general.TaskAddActivity
import deakin.gopher.guardian.view.general.TasksListActivity
import deakin.gopher.guardian.view.patient.PatientLogsActivity

class NavigationService(private val activity: Activity) {

    fun toHomeScreenForRole(role: Role) {
        val intent =
            when (role) {
                Role.Caretaker -> Intent(activity, Homepage4caretaker::class.java)
                Role.Nurse -> Intent(activity, Homepage4nurse::class.java)
                Role.Admin -> Intent(activity, Homepage4admin::class.java)
                Role.Doctor -> Intent(activity, Homepage4doctor::class.java)
            }

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        activity.startActivity(intent)
        activity.finish()
    }

    fun toRegistration() {
        activity.startActivity(
            Intent(activity, RegisterActivity::class.java),
        )
    }

    fun onSettings() {
        val intent = Intent(activity, Setting::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP

        activity.startActivity(intent)
    }

    fun onPatientLogs() {
        activity.startActivity(
            Intent(
                activity,
                PatientLogsActivity::class.java,
            ),
        )
    }

    fun onSignOut() {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        activity.startActivity(intent)
        activity.finish()
    }

    fun onLaunchPatientList() {
        val intent = Intent(activity, PatientListActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP

        activity.startActivity(intent)
    }

    fun onLaunchTasks() {
        val intent = Intent(activity, TasksListActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP

        activity.startActivity(intent)
    }

    fun onLaunchTaskCreator() {
        val intent = Intent(activity, TaskAddActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP

        activity.startActivity(intent)
    }

    fun toLogin() {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        activity.startActivity(intent)
        activity.finish()
    }

    fun toPinCodeActivity(role: Role) {
        val intent = Intent(activity, PinCodeActivity::class.java)
        intent.putExtra("role", role)
        activity.startActivity(intent)
    }
}