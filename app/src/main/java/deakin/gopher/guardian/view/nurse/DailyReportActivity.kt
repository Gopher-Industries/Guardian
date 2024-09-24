package deakin.gopher.guardian.view.nurse

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.databinding.ActivityDailyReport2Binding

class DailyReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDailyReport2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDailyReport2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        navigationView.itemIconTintList = null

        binding.ivMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

    }
}