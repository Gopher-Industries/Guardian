package deakin.gopher.guardian.view.caretaker.notifications

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.view.caretaker.notifications.confirmincident.ConfirmIncidentActivity
import deakin.gopher.guardian.view.caretaker.notifications.falsealarm.FalseAlertConfirmedActivity
import deakin.gopher.guardian.view.general.BaseActivity

class FallAlertActivity : BaseActivity() {
    var fallAlertMenuButton: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fall_alert)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        fallAlertMenuButton = findViewById(R.id.menuButton)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        navigationView.itemIconTintList = null

        fallAlertMenuButton?.let { button ->
            button.setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        val confirmIncidentButton = findViewById<ImageButton>(R.id.confirmIncidentButton)
        val falseAlarmButton = findViewById<ImageButton>(R.id.falseAlarmButton)

        confirmIncidentButton.setOnClickListener { v: View? ->
            val medicalDiagnosticsActivityIntent =
                Intent(this@FallAlertActivity, ConfirmIncidentActivity::class.java)
            startActivity(medicalDiagnosticsActivityIntent)
        }

        falseAlarmButton.setOnClickListener { v: View? ->
            val medicalDiagnosticsActivityIntent =
                Intent(this@FallAlertActivity, FalseAlertConfirmedActivity::class.java)
            startActivity(medicalDiagnosticsActivityIntent)
        }
    }
}
