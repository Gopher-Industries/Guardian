package deakin.gopher.guardian

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import deakin.gopher.guardian.view.general.DrawerNavigationHelper

class TrainingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_training2)

        val drawerLayout: DrawerLayout =
            findViewById(R.id.drawer_layout)

        val navigationView: NavigationView =
            findViewById(R.id.nav_view)

        val toolbar: MaterialToolbar =
            findViewById(R.id.training_toolbar)

        setSupportActionBar(toolbar)

        DrawerNavigationHelper.bindStandardDrawer(
            this,
            drawerLayout,
            navigationView,
            toolbar,
        )

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main),
        ) { view, insets ->
            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars(),
                )

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom,
            )

            insets
        }
    }
}
