package deakin.gopher.guardian.services

import android.app.Activity
import android.content.Intent
import deakin.gopher.guardian.model.login.Role
import deakin.gopher.guardian.view.general.Homepage4admin
import deakin.gopher.guardian.view.general.Homepage4caretaker
import deakin.gopher.guardian.view.general.Homepage4nurse
import deakin.gopher.guardian.view.general.LoginActivity
import deakin.gopher.guardian.view.general.PatientListActivity
import deakin.gopher.guardian.view.general.PinCodeActivity
import deakin.gopher.guardian.view.general.RegisterActivity
import deakin.gopher.guardian.view.general.Setting
import deakin.gopher.guardian.view.general.TaskAddActivity
import deakin.gopher.guardian.view.general.TasksListActivity

class NavigationService(val activity: Activity) {
    fun toHomeScreenForRole(role: Role) {
        val intent = when (role) {
            Role.Caretaker -> Intent(activity, Homepage4caretaker::class.java)
            Role.Nurse -> Intent(activity, Homepage4nurse::class.java)
            Role.Admin -> Intent(activity, Homepage4admin::class.java)
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

    fun toPinCodeActivity(role: Role) {
        val intent = Intent(activity, PinCodeActivity::class.java)
        intent.putExtra("role", role)
        activity.startActivity(intent)
        // We keep LoginActivity in the stack in case user wants to go back from PIN screen?
        // Actually, usually you'd want to finish() it too if PIN is mandatory.
        // If we want the back button on PIN screen to go back to Login, we don't finish() here.
    }
}
