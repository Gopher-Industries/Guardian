package deakin.gopher.guardian.view.caretaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.view.general.BaseActivity
import deakin.gopher.guardian.view.general.DrawerNavigationHelper
import deakin.gopher.guardian.view.general.Homepage4caretaker

class CaretakerProfileActivity : BaseActivity() {
    private lateinit var backButton: Button
    private lateinit var editButton: ImageView
    private lateinit var menuButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caretakerprofile)

        backButton = findViewById(R.id.backBtn)
        editButton = findViewById(R.id.editButton)
        menuButton = findViewById(R.id.menuButton)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)

        DrawerNavigationHelper.bindStandardDrawer(this, drawerLayout, navigationView, menuButton)

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
    }
}
