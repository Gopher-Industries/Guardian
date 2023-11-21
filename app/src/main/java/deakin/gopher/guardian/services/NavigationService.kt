package deakin.gopher.guardian.services

import android.app.Activity
import android.content.Intent
import deakin.gopher.guardian.model.login.RoleName
import deakin.gopher.guardian.view.general.Homepage4admin
import deakin.gopher.guardian.view.general.Homepage4caretaker
import deakin.gopher.guardian.view.general.LoginActivity
import deakin.gopher.guardian.view.general.RegisterActivity

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
                        Homepage4caretaker::class.java,
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

    fun toLogin() {
        activity.startActivity(
            Intent(
                activity.applicationContext,
                LoginActivity::class.java,
            ),
        )
    }
}
