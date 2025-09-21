// package deakin.gopher.guardian.view.general
//
// import android.content.Intent
// import android.os.Bundle
// import android.util.Log
// import android.widget.Button
// import com.google.android.gms.tasks.Task
// import com.google.firebase.messaging.FirebaseMessaging
// import deakin.gopher.guardian.R
// import deakin.gopher.guardian.model.login.Role
// import deakin.gopher.guardian.model.login.SessionManager
//
// class MainActivity : BaseActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        val getStartedButton = findViewById<Button>(R.id.getStartedButton)
//
//        getStartedButton.setOnClickListener { _ -> onGetStartedClick() }
//
//        FirebaseMessaging.getInstance()
//            .token
//            .addOnCompleteListener { task: Task<String?> ->
//                if (task.isSuccessful) {
//                    val token = task.result
//                    Log.d("FCM Token", token ?: "Token is null")
//                } else {
//                    Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
//                }
//            }
//    }
//
//    private fun onGetStartedClick() {
//        if (!SessionManager.isLoggedIn) {
//            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
//        } else {
//            // Retrieve the user roleName from SessionManager
//            val userRole = SessionManager.getCurrentUser().role
//
//            when (userRole) {
//                Role.Admin -> {
//                    startActivity(Intent(this@MainActivity, Homepage4admin::class.java))
//                }
//                Role.Caretaker -> {
//                    startActivity(Intent(this@MainActivity, Homepage4caretaker::class.java))
//                }
//                Role.Nurse -> {
//                    startActivity(Intent(this@MainActivity, Homepage4nurse::class.java))
//                }
//                Role.Doctor -> {
//                    startActivity(Intent(this@MainActivity, Homepage4doctor::class.java))
//                }
//            }
//        }
//    }
// }

package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.login.Role
import deakin.gopher.guardian.model.login.SessionManager

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ✅ Initialize SessionManager before using it
        SessionManager.init(applicationContext)

        val getStartedButton = findViewById<Button>(R.id.getStartedButton)
        getStartedButton.setOnClickListener { _ -> onGetStartedClick() }

        // ✅ Safely fetch FCM token without crashing
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task: Task<String?> ->
                try {
                    if (task.isSuccessful) {
                        val token = task.result
                        Log.d("FCM Token", token ?: "Token is null")
                    } else {
                        Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", "FCM token exception", e)
                }
            }
    }

    private fun onGetStartedClick() {
        if (!SessionManager.isLoggedIn) {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            return
        }

        try {
            // ✅ Safely retrieve the user role
            val userRole = SessionManager.getCurrentUser().role

            when (userRole) {
                Role.Admin -> startActivity(Intent(this@MainActivity, Homepage4admin::class.java))
                Role.Caretaker -> startActivity(Intent(this@MainActivity, Homepage4caretaker::class.java))
                Role.Nurse -> startActivity(Intent(this@MainActivity, Homepage4nurse::class.java))
                Role.Doctor -> startActivity(Intent(this@MainActivity, Homepage4doctor::class.java))
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Failed to get user role, redirecting to login", e)
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }
    }
}
