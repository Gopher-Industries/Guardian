package deakin.gopher.guardian.view.caretaker

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Button
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.view.general.BaseActivity
import deakin.gopher.guardian.view.general.DrawerNavigationHelper

class EditCaretakerProfileActivity : BaseActivity() {
    private lateinit var saveButton: Button
    private lateinit var menuButton: ImageView
    val emojiCodePoint = 0x1F97A
    val emojiString = String(Character.toChars(emojiCodePoint))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_caretakerprofile)

        saveButton = findViewById(R.id.btnSave)
        menuButton = findViewById(R.id.menuButton)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)

        DrawerNavigationHelper.bindStandardDrawer(this, drawerLayout, navigationView, menuButton)

        saveButton.setOnClickListener {
            Toast.makeText(this, "Why Firebase not working? $emojiString", Toast.LENGTH_LONG).show()
            val medicalDiagnosticsActivityIntent =
                Intent(this, CaretakerProfileActivity::class.java)
            startActivity(medicalDiagnosticsActivityIntent)
        }
    }
}
