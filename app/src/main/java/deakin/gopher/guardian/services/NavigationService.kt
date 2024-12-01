package deakin.gopher.guardian.services

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import deakin.gopher.guardian.model.login.RoleName
import deakin.gopher.guardian.view.general.Homepage4admin
import deakin.gopher.guardian.view.general.Homepage4caretaker
import deakin.gopher.guardian.view.general.Homepage4nurse
import deakin.gopher.guardian.view.general.LoginActivity
import deakin.gopher.guardian.view.general.PinCodeActivity
import deakin.gopher.guardian.view.general.RegisterActivity
import deakin.gopher.guardian.view.general.Setting
import deakin.gopher.guardian.view.general.TaskAddActivity
import deakin.gopher.guardian.view.general.TasksListActivity

class NavigationService(val activity: Activity) {
    fun toHomeScreenForRole(roleName: RoleName) {
        when (roleName) {
            RoleName.Caretaker -> {
                activity.startActivity(
                    Intent(
                        activity.applicationContext,
                        Homepage4caretaker::class.java,
                    ),
                )
            }

            RoleName.Nurse -> {
                activity.startActivity(
                    Intent(
                        activity.applicationContext,
                        Homepage4nurse::class.java,
                    ),
                )
            }

            RoleName.Admin -> {
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

    fun toPinCodeActivity(roleName: RoleName) {
        val intent = Intent(activity.applicationContext, PinCodeActivity::class.java)
        intent.putExtra("role", roleName)
        activity.startActivity(intent)
    }

    class NavigationHelper(private val context: Context) {
        fun navigateToRoleDashboard(role: String) {
            val intent =
                when (role) {
                    "Caretaker" -> Intent(context, Homepage4caretaker::class.java)
                    "Nurse" -> Intent(context, Homepage4nurse::class.java)
                    "Admin" -> Intent(context, Homepage4admin::class.java)
                    else -> {
                        Toast.makeText(context, "Unknown role", Toast.LENGTH_SHORT).show()
                        return
                    }
                }
            context.startActivity(intent)
            if (context is Activity) {
                (context as Activity).finish()
            }
        }
    }
}
