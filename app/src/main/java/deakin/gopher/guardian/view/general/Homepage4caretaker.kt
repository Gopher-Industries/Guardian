package deakin.gopher.guardian.view.general

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.view.animation.Animation
import android.view.View
import android.view.animation.AnimationUtils
import android.os.Bundle
import android.widget.Button
import com.airbnb.lottie.LottieAnimationView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import deakin.gopher.guardian.PatientExerciseModules
import deakin.gopher.guardian.R
import deakin.gopher.guardian.TrainingActivity
import deakin.gopher.guardian.services.EmailPasswordAuthService
import deakin.gopher.guardian.view.caretaker.CaretakerProfileActivity
import deakin.gopher.guardian.view.falldetection.FallDetectionActivity

class Homepage4caretaker : BaseActivity() {
    private lateinit var patientListButton: Button
    private lateinit var settingsButton: Button
    private lateinit var signOutButton: Button
    private lateinit var profileButton: Button
    private lateinit var taskListButton: Button
    private lateinit var trainingButton: Button
    private lateinit var monitorButton: Button

    private lateinit var chatBotGreeting: TextView

    private lateinit var exercisePortalButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load the saved theme preference from SharedPreferences
        val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val isNightMode = sharedPreferences.getBoolean("night_mode", false)

        // Apply the correct theme based on the saved preference
        if (isNightMode) {
            setTheme(R.style.Theme_TeamGuardians_Dark)  // Dark theme
        } else {
            setTheme(R.style.Theme_TeamGuardians)  // Light theme
        }

        // Set content view after theme has been applied
        setContentView(R.layout.activity_homepage4caretaker)

        patientListButton = findViewById(R.id.patientListButton)
        settingsButton = findViewById(R.id.settingsButton3)
        signOutButton = findViewById(R.id.sighOutButton)
        profileButton = findViewById(R.id.caretaker_profile)
        taskListButton = findViewById(R.id.taskListButton)
        trainingButton = findViewById(R.id.trainingButton)
        monitorButton = findViewById(R.id.monitorButton)

        chatBotGreeting = findViewById(R.id.chatBotGreeting)

        // Apply the pop-out animation to the greeting text
        val popOutAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.popout)
        chatBotGreeting.startAnimation(popOutAnimation)


//         For the redirecting of the chatbot

        val chatBotAnimation = findViewById<LottieAnimationView>(R.id.chatBotAnimation)
        val chatBotGreeting = findViewById<TextView>(R.id.chatBotGreeting)

        val redirectToChat = View.OnClickListener {
            val intent = Intent(this, ChatBotActivity::class.java)
            startActivity(intent)
        }

        chatBotAnimation.setOnClickListener(redirectToChat)
        chatBotGreeting.setOnClickListener(redirectToChat)

        exercisePortalButton = findViewById(R.id.exerciseportal)

        // patient list button
        patientListButton.setOnClickListener {
            val medicalDiagnosticsActivityIntent =
                Intent(this@Homepage4caretaker, PatientListActivity::class.java)
            medicalDiagnosticsActivityIntent.putExtra("userType", "caretaker")
            startActivity(medicalDiagnosticsActivityIntent)
        }

        // settings button
        settingsButton.setOnClickListener {
            val medicalDiagnosticsActivityIntent =
                Intent(this@Homepage4caretaker, Setting::class.java)
            medicalDiagnosticsActivityIntent.putExtra("userType", "caretaker")
            startActivity(medicalDiagnosticsActivityIntent)
        }

        // tasklist button
        taskListButton.setOnClickListener {
            startActivity(
                Intent(this@Homepage4caretaker, TasksListActivity::class.java),
            )
        }

        // sign out button
        signOutButton.setOnClickListener {
            EmailPasswordAuthService.signOut(this)
            finish()
        }

        profileButton.setOnClickListener {
            val medicalDiagnosticsActivityIntent =
                Intent(this@Homepage4caretaker, CaretakerProfileActivity::class.java)
            startActivity(medicalDiagnosticsActivityIntent)
        }

        monitorButton.setOnClickListener {
            startFallDetectionActivity()
        }

        // training button
        trainingButton.setOnClickListener {
            startActivity(
                Intent(this@Homepage4caretaker, TrainingActivity::class.java),
            )
        }

        //  exercise portal button
        exercisePortalButton.setOnClickListener {
            startActivity(
                Intent(this@Homepage4caretaker, PatientExerciseModules::class.java),
            )
        }
    }

    @OptIn(UnstableApi::class)
    fun startFallDetectionActivity() {
        val fallDetectionActivityIntent =
            Intent(this@Homepage4caretaker, FallDetectionActivity::class.java)
        startActivity(fallDetectionActivityIntent)
    }

    // Function to toggle theme if needed (could be triggered when theme changes in Settings)
    private fun toggleTheme(isNightMode: Boolean) {
        val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("night_mode", isNightMode)  // Save theme preference
        editor.apply()

        // Apply the correct theme based on the user's choice
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        // Recreate the activity to apply the new theme
        recreate()
    }
}
