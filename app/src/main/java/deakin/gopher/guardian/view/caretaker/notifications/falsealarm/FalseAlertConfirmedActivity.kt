package deakin.gopher.guardian.view.caretaker.notifications.falsealarm

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.view.caretaker.CaretakerDashboardActivity
import deakin.gopher.guardian.view.general.BaseActivity

class FalseAlertConfirmedActivity : BaseActivity() {
    var falseAlertMenuButton: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_false_alert)

        val submitFalseAlertButton = findViewById<Button>(R.id.submitFalseAlert)

        submitFalseAlertButton.setOnClickListener { v: View? -> showDialog() }

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        falseAlertMenuButton = findViewById(R.id.menuButton)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        navigationView.itemIconTintList = null

        falseAlertMenuButton?.setOnClickListener(
            View.OnClickListener { v: View? ->
                drawerLayout.openDrawer(GravityCompat.START)
            },
        )
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.layout_false_alert_confirmed)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val okButtonFalseAlert = dialog.findViewById<Button>(R.id.okButtonFalseAlert)
        okButtonFalseAlert.setOnClickListener { v: View? ->
            val intent =
                Intent(applicationContext, CaretakerDashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
        dialog.show()
    }
}
