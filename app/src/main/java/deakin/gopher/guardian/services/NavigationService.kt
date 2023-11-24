package deakin.gopher.guardian.services

import android.app.Activity
import android.content.Intent
import deakin.gopher.guardian.model.login.RoleName
import deakin.gopher.guardian.view.general.Homepage4admin
import deakin.gopher.guardian.view.general.Homepage4caretaker
import deakin.gopher.guardian.view.general.Homepage4nurse
import deakin.gopher.guardian.view.general.RegisterActivity
import deakin.gopher.guardian.view.general.Setting
import deakin.gopher.guardian.view.general.LoginActivity

class NavigationService(val activity: Activity) {
    fun onLoginForRole(roleName: RoleName) {
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

    fun onRegister() {
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
                Setting::class.java
            )
        )
    }

    fun onSignOut() {
        activity.startActivity(
            Intent(
                activity.applicationContext,
                LoginActivity::class.java
            )
        )
    }
}
