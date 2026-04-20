package deakin.gopher.guardian.view.caretaker.notifications.confirmincident

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.services.EmailPasswordAuthService
import deakin.gopher.guardian.view.general.BaseActivity
import deakin.gopher.guardian.view.general.Homepage4admin

class CallAmbulanceActivity : BaseActivity() {
    var hospitalSpinner: Spinner? = null
    var hospitalSelection: String? = null
    var confirmIncidentMenuButton: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_ambulance)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        confirmIncidentMenuButton = findViewById(R.id.menuButton)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        navigationView.itemIconTintList = null

        confirmIncidentMenuButton?.let { button ->
            button.setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }
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

        hospitalSpinner = findViewById(R.id.hospitalSpinner)

        // Create an ArrayAdapter using the string array and a default Spinner layout
        val hospitalAdapter =
            ArrayAdapter.createFromResource(
                applicationContext,
                R.array.hospital_list_array,
                R.layout.spinner_layout,
            )

        // Apply the adapter to the Spinner
        hospitalSpinner?.setAdapter(hospitalAdapter)

        // Set the Spinner to the list of choices
        hospitalSpinner?.setOnItemSelectedListener(SpinnerActivity())

        hospitalSpinner?.setOnItemSelectedListener(
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    selectedItemView: View,
                    position: Int,
                    id: Long,
                ) {
                    hospitalSelection = parentView.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            },
        )
    }

    fun onConfirmIncidentCancelClick(view: View?) {
        val medicalDiagnosticsActivityIntent =
            Intent(this@CallAmbulanceActivity, Homepage4admin::class.java)
        startActivity(medicalDiagnosticsActivityIntent)
    }

    class SpinnerActivity : Activity(), AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View,
            pos: Int,
            id: Long,
        ) {
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}
