package deakin.gopher.guardian.view.caretaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import deakin.gopher.guardian.R
import deakin.gopher.guardian.view.general.BaseActivity
import deakin.gopher.guardian.view.general.Homepage4caretaker
import deakin.gopher.guardian.view.general.LoginActivity

class CaretakerProfileActivity : BaseActivity() {
    private lateinit var backButton: Button
    private lateinit var editButton: ImageView
    private lateinit var menuButton: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caretakerprofile)

        backButton = findViewById(R.id.backBtn)
        editButton = findViewById(R.id.editButton)
        menuButton = findViewById(R.id.menuButton)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        backButton.setOnClickListener {
            val medicalDiagnosticsActivityIntent =
                Intent(this, Homepage4caretaker::class.java)
            startActivity(medicalDiagnosticsActivityIntent)
        }

        editButton.setOnClickListener {
            val medicalDiagnosticsActivityIntent =
                Intent(this, EditCaretakerProfileActivity::class.java)
            startActivity(medicalDiagnosticsActivityIntent)
        }

        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, Homepage4caretaker::class.java))
                }

                R.id.nav_signout -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
}