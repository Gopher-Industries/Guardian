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
import deakin.gopher.guardian.services.EmailPasswordAuthService
import deakin.gopher.guardian.view.general.BaseActivity
import deakin.gopher.guardian.view.general.Homepage4admin

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

        falseAlertMenuButton?.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, Homepage4admin::class.java))
                    finish()
                    true
                }
                R.id.nav_signout -> {
                    androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle(R.string.sign_out)
                        .setMessage(R.string.sign_out_confirmation_message)
                        .setPositiveButton(R.string.sign_out) { _, _ ->
                            EmailPasswordAuthService.signOut(this)
                            finish()
                        }
                        .setNegativeButton(R.string.stay_in, null)
                        .show()
                    true
                }
                else -> false
            }
        }
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.layout_false_alert_confirmed)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val okButtonFalseAlert = dialog.findViewById<Button>(R.id.okButtonFalseAlert)
        okButtonFalseAlert.setOnClickListener { v: View? ->
            val intent =
                Intent(applicationContext, Homepage4admin::class.java)
            startActivity(intent)
            finish()
        }
        dialog.show()
    }
}
