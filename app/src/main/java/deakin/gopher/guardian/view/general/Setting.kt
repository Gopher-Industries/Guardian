package deakin.gopher.guardian.view.general

import android.view.Menu
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog

class Setting constructor() : BaseActivity(), View.OnClickListener {
    var settingsThemeButton: ConstraintLayout? = null
    var settingsNotificationButton: ConstraintLayout? = null
    var settingsAppUpdateButton: ConstraintLayout? = null
    var settingsFeedbackButton: ConstraintLayout? = null
    var notificationSwitch: Switch? = null
    var themeSwitch: Switch? = null
    var settingsMenuButton: ImageView? = null
    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        val userType: String = getIntent().getStringExtra("userType")
        settingsThemeButton = findViewById(R.id.settings_theme_button)
        settingsNotificationButton = findViewById(R.id.settings_notification_button)
        settingsAppUpdateButton = findViewById(R.id.settings_app_update_button)
        settingsFeedbackButton = findViewById(R.id.settings_feedback_button)
        settingsAppUpdateButton.setOnClickListener(this)
        settingsFeedbackButton.setOnClickListener(this)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        settingsMenuButton = findViewById(R.id.settings_menu_button)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        navigationView.setItemIconTintList(null)
        settingsMenuButton!!.setOnClickListener(
            View.OnClickListener({ v: View? -> drawerLayout.openDrawer(GravityCompat.START) })
        )
        val settingsThemeButton: ConstraintLayout = findViewById(R.id.settings_theme_button)
        notificationSwitch = findViewById(R.id.notification_switch)
        notificationSwitch.setOnCheckedChangeListener(
            CompoundButton.OnCheckedChangeListener({ buttonView: CompoundButton?, isChecked: Boolean ->
                handleNotificationSwitch(
                    isChecked
                )
            })
        )
        themeSwitch = findViewById(R.id.theme_switch)
        themeSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener({ buttonView: CompoundButton?, isChecked: Boolean ->
            handleThemeSwitch(
                isChecked
            )
        }))
        configureNavigationDrawer(userType)
    }

    public override fun onClick(v: View) {
        if (R.id.settings_feedback_button == v.getId()) {
            showFeedbackDialog()
        }
    }

    private fun initializeSwitchStates() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val isNightMode: Boolean = sharedPreferences.getBoolean("night_mode", false)
        themeSwitch.setChecked(isNightMode)
    }

    private fun handleNotificationSwitch(isChecked: Boolean) {
        if (isChecked) {
            showNotification()
            showToast("Notifications turned on")
        } else {
            showToast("Notifications turned off")
        }
    }

    private fun configureNavigationDrawer(userType: String) {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val menu: Menu = navigationView.getMenu()
        menu.clear()
        navigationView.inflateMenu(R.menu.nav_menu)
        navigationView.setNavigationItemSelectedListener(
            { menuItem ->
                var intent: Intent? = null
                when (menuItem.getItemId()) {
                    R.id.nav_home -> intent = Intent(
                        this@Setting,
                        if ((userType == "admin")) Homepage4admin::class.java else Homepage4caretaker::class.java
                    )

                    R.id.nav_signout -> {
                        FirebaseAuth.getInstance().signOut()
                        startActivity(Intent(this@Setting, LoginActivity::class.java))
                        finish()
                    }
                }
                if (intent != null) {
                    startActivity(intent)
                }
                val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            })
    }

    private fun handleThemeSwitch(isChecked: Boolean) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        if (isChecked) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            sharedPreferences.edit().putBoolean("night_mode", true).apply()
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            sharedPreferences.edit().putBoolean("night_mode", false).apply()
        }
    }

    private fun showNotification() {
        if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
            val channel: NotificationChannel = NotificationChannel(
                "channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    // feedback
    private fun showFeedbackDialog() {
        val builder: AlertDialog.Builder = Builder(this)
        builder.setTitle("Provide Feedback")
        val feedbackEditText: EditText = EditText(this)
        feedbackEditText.setHint("Enter your feedback...")
        builder.setView(feedbackEditText)
        builder.setPositiveButton(
            "Submit",
            { dialog, which ->
                val feedback: String = feedbackEditText.getText().toString()
                if (!feedback.isEmpty()) {
                    showToast("Feedback submitted: " + feedback)
                } else {
                    showToast("Please enter your feedback")
                }
            })
        builder.setNegativeButton("Cancel", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showToast(message: CharSequence) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        initializeSwitchStates()
    }
}