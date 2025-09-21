package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.model.register.NotificationItem
import deakin.gopher.guardian.services.EmailPasswordAuthService
import deakin.gopher.guardian.services.NavigationService
import deakin.gopher.guardian.services.api.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Homepage4nurse : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage4nurse)

        // Get current nurse user
        val user = SessionManager.getCurrentUser()
        val userId = user.id // nurse ID

        // Update welcome title
        val titleText: TextView = findViewById(R.id.medicalDiagnosticsTitleTextView)
        titleText.append(" ${user.name.split(" ").getOrNull(0) ?: ""}")

        // Initialize buttons
        val patientsButton: Button = findViewById(R.id.patientsButton_nurse)
        val settingsButton: Button = findViewById(R.id.settingsButton_nurse)
        val signOutButton: Button = findViewById(R.id.sighOutButton_nurse)

        patientsButton.setOnClickListener {
            NavigationService(this).onLaunchPatientList()
        }

        settingsButton.setOnClickListener {
            NavigationService(this).onSettings()
        }

        signOutButton.setOnClickListener {
            EmailPasswordAuthService.signOut(this)
            finish()
        }

        // Fetch notifications from backend
        fetchNotifications()

        val coordinatorLayout: CoordinatorLayout = findViewById(R.id.homepageCoordinator)

        showDropdownNotification(
            coordinatorLayout,
            "New Patient Assigned",
            "A new Patient has been assigned to you",
        )
    }

    private fun fetchNotifications() {
        val token = "Bearer ${SessionManager.getToken()}"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.getMyNotifications(token)
                if (response.isSuccessful) {
                    val notifications: List<NotificationItem> = response.body() ?: emptyList()

                    // Display notifications (toast for now)
                    withContext(Dispatchers.Main) {
                        notifications.forEach { notification ->
                            Toast.makeText(
                                this@Homepage4nurse,
                                notification.message,
                                Toast.LENGTH_LONG,
                            ).show()
                        }
                    }
                } else {
                    Log.e("Homepage4nurse", "Failed to fetch notifications: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("Homepage4nurse", "Error fetching notifications", e)
            }
        }
    }

    fun showDropdownNotification(
        coordinatorLayout: CoordinatorLayout,
        title: String,
        message: String,
    ) {
        val inflater = LayoutInflater.from(coordinatorLayout.context)
        val notificationView =
            inflater.inflate(R.layout.dropdown_notification, coordinatorLayout, false)

        val titleText: TextView = notificationView.findViewById(R.id.notificationTitle)
        val messageText: TextView = notificationView.findViewById(R.id.notificationMessage)
        val dismissBtn: ImageView = notificationView.findViewById(R.id.notificationDismiss)

        titleText.text = title
        messageText.text = message

        // Add view to CoordinatorLayout
        coordinatorLayout.addView(notificationView)

        // Slide-down animation
        notificationView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val height = notificationView.measuredHeight.toFloat()

        notificationView.translationY = -height
        notificationView.animate()
            .translationY(0f)
            .setDuration(300)
            .start()

        // Auto-dismiss after 2s
        val dismissRunnable =
            Runnable {
                notificationView.animate()
                    .translationY(-height)
                    .setDuration(300)
                    .withEndAction { coordinatorLayout.removeView(notificationView) }
                    .start()
            }
        notificationView.postDelayed(dismissRunnable, 2000)

        // Manual dismiss
        dismissBtn.setOnClickListener {
            notificationView.removeCallbacks(dismissRunnable)
            dismissRunnable.run()
        }
    }
}
