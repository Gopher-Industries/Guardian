package deakin.gopher.guardian.view.general

import android.app.Activity
import android.view.View
import androidx.appcompat.widget.Toolbar
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
        val openDrawerListener =
            View.OnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }

        if (menuButton is Toolbar) {
            menuButton.setNavigationOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        } else {
            menuButton.setOnClickListener(openDrawerListener)
        }

        extraMenuButtons.forEach { button ->
            button?.setOnClickListener(openDrawerListener)
        }

        navigationView.itemIconTintList = null

        val navigationService = NavigationService(activity)

        val role =
            try {
                SessionManager.getCurrentUser().role
            } catch (_: Exception) {
                null
            }

        val isDoctor = role == Role.Doctor
        val isNurse = role == Role.Nurse
        val isAdmin = role == Role.Admin
        val isCaretaker = role == Role.Caretaker
        val hasRoleNavigation = role != null

        val menu = navigationView.menu

        menu.findItem(R.id.nav_patient_list)?.apply {
            isVisible = hasRoleNavigation
            title =
                activity.getString(
                    if (isDoctor || isNurse) {
                        R.string.my_patients
                    } else {
                        R.string.patient_list
                    },
                )
        }

        menu.findItem(R.id.nav_patient_logs)?.isVisible = isNurse

        menu.findItem(R.id.nav_add_patient)?.isVisible = isAdmin
        menu.findItem(R.id.nav_daily_report)?.isVisible = isAdmin
        menu.findItem(R.id.nav_nurse_roster)?.isVisible = isAdmin

        menu.findItem(R.id.nav_task_list)?.isVisible = isCaretaker
        menu.findItem(R.id.add_task)?.isVisible = isCaretaker
        menu.findItem(R.id.nav_profile)?.isVisible = isCaretaker
        menu.findItem(R.id.nav_monitoring)?.isVisible = isCaretaker
        menu.findItem(R.id.nav_training)?.isVisible = isCaretaker
        menu.findItem(R.id.nav_exercise_portal)?.isVisible = isCaretaker

        menu.findItem(R.id.nav_settings)?.isVisible = hasRoleNavigation

        navigationView.setNavigationItemSelectedListener { menuItem ->
            val handled =
                when (menuItem.itemId) {
                    R.id.nav_home -> {
                        if (role != null) {
                            navigationService.toHomeScreenForRole(role)
                        } else {
                            navigationService.toLogin()
                        }
                        true
                    }

                    R.id.nav_patient_list -> {
                        if (hasRoleNavigation) {
                            navigationService.onLaunchPatientList()
                        }
                        hasRoleNavigation
                    }

                    R.id.nav_patient_logs -> {
                        if (isNurse) {
                            navigationService.onPatientLogs()
                        }
                        isNurse
                    }

                    R.id.nav_add_patient -> {
                        if (isAdmin) {
                            navigationService.onLaunchAddPatient()
                        }
                        isAdmin
                    }

                    R.id.nav_daily_report -> {
                        if (isAdmin) {
                            navigationService.onLaunchDailyReport()
                        }
                        isAdmin
                    }

                    R.id.nav_nurse_roster -> {
                        if (isAdmin) {
                            navigationService.onLaunchNurseRoster()
                        }
                        isAdmin
                    }

                    R.id.nav_task_list -> {
                        if (isCaretaker) {
                            navigationService.onLaunchTasks()
                        }
                        isCaretaker
                    }

                    R.id.add_task -> {
                        if (isCaretaker) {
                            navigationService.onLaunchTaskCreator()
                        }
                        isCaretaker
                    }

                    R.id.nav_profile -> {
                        if (isCaretaker) {
                            navigationService.onLaunchCaretakerProfile()
                        }
                        isCaretaker
                    }

                    R.id.nav_monitoring -> {
                        if (isCaretaker) {
                            navigationService.onLaunchMonitoring()
                        }
                        isCaretaker
                    }

                    R.id.nav_training -> {
                        if (isCaretaker) {
                            navigationService.onLaunchTraining()
                        }
                        isCaretaker
                    }

                    R.id.nav_exercise_portal -> {
                        if (isCaretaker) {
                            navigationService.onLaunchExercisePortal()
                        }
                        isCaretaker
                    }

                    R.id.nav_settings -> {
                        if (hasRoleNavigation) {
                            navigationService.onSettings()
                        }
                        hasRoleNavigation
                    }

                    R.id.nav_signout -> {
                        EmailPasswordAuthService.signOut(activity)
                        activity.finish()
                        true
                    }

                    else -> false
                }

            if (handled) {
                drawerLayout.closeDrawer(GravityCompat.START)
            }

            handled
        }
    }
}
