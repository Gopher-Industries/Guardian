package deakin.gopher.guardian.view.caretaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import deakin.gopher.guardian.R
import deakin.gopher.guardian.view.general.BaseActivity
import deakin.gopher.guardian.view.general.LoginActivity

class EditCaretakerProfileActivity : BaseActivity() {
    private lateinit var saveButton: Button
    private lateinit var menuButton: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private val emojiCodePoint = 0x1F97A
    private val emojiString = String(Character.toChars(emojiCodePoint))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_caretakerprofile)

        saveButton = findViewById(R.id.btnSave)
        menuButton = findViewById(R.id.menuButton)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, CaretakerProfileActivity::class.java))
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

        saveButton.setOnClickListener {
            Toast.makeText(this, "Why Firebase not working? $emojiString", Toast.LENGTH_LONG).show()
            val medicalDiagnosticsActivityIntent =
                Intent(this, CaretakerProfileActivity::class.java)
            startActivity(medicalDiagnosticsActivityIntent)
        }
    }
}