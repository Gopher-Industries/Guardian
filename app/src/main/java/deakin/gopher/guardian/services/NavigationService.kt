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
        when (role) {
            Role.Caretaker -> {
                activity.startActivity(
                    Intent(
                        activity.applicationContext,
                        Homepage4caretaker::class.java,
                    ),
                )
            }

            Role.Nurse -> {
                activity.startActivity(
                    Intent(
                        activity.applicationContext,
                        Homepage4nurse::class.java,
                    ),
                )
            }

            Role.Admin -> {
                activity.startActivity(
                    Intent(
                        activity.applicationContext,
                        Homepage4admin::class.java,
                    ),
                )
            }
        }
    }

    fun toRegistration() {
        activity.startActivity(
            Intent(
                activity.applicationContext,
                RegisterActivity::class.java,
            ),
        )
    }

    fun onSettings() {
        activity.startActivity(
            Intent(
                activity.applicationContext,
                Setting::class.java,
            ),
        )
    }

    fun onSignOut() {
        activity.startActivity(
            Intent(
                activity.applicationContext,
                LoginActivity::class.java,
            ),
        )
    }

    fun onLaunchPatientList() {
        activity.startActivity(
            Intent(
                activity.applicationContext,
                PatientListActivity::class.java,
            ),
        )
    }

    fun onLaunchTasks() {
        activity.startActivity(
            Intent(
                activity.applicationContext,
                TasksListActivity::class.java,
            ),
        )
    }

    fun onLaunchTaskCreator() {
        activity.startActivity(
            Intent(
                activity.applicationContext,
                TaskAddActivity::class.java,
            ),
        )
    }

    fun toLogin() {
        activity.startActivity(
            Intent(
                activity.applicationContext,
                LoginActivity::class.java,
            ),
        )
    }

    fun toPinCodeActivity(role: Role) {
        val intent = Intent(activity.applicationContext, PinCodeActivity::class.java)
        intent.putExtra("role", role)
        activity.startActivity(intent)
    }
}
