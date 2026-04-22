package deakin.gopher.guardian.view.general

import android.app.Activity
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.login.Role
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.EmailPasswordAuthService
import deakin.gopher.guardian.services.NavigationService

object DrawerNavigationHelper {
    @JvmStatic
    fun bindStandardDrawer(
        activity: Activity,
        drawerLayout: DrawerLayout,
        navigationView: NavigationView,
        menuButton: View,
        vararg extraMenuButtons: View?,
    ) {
        val openDrawerListener = View.OnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        menuButton.setOnClickListener(openDrawerListener)
        extraMenuButtons.forEach { button ->
            button?.setOnClickListener(openDrawerListener)
        }

        navigationView.itemIconTintList = null
        val navigationService = NavigationService(activity)
        val role =
            try {
                SessionManager.getCurrentUser().role
            } catch (e: Exception) {
                null
            }
        val canAddTasks = role == Role.Caretaker
        navigationView.menu.findItem(R.id.add_task)?.isVisible = canAddTasks

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    if (role != null) {
                        navigationService.toHomeScreenForRole(role)
                    } else {
                        navigationService.toLogin()
                    }
                }

                R.id.add_task -> {
                    if (canAddTasks) {
                        navigationService.onLaunchTaskCreator()
                    }
                }

                R.id.nav_signout -> {
                    EmailPasswordAuthService.signOut(activity)
                    activity.finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
}
