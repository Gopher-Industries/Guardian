package deakin.gopher.guardian.view.general

import androidx.core.view.GravityCompat

class DrawerActivity : BaseActivity() {
    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        findViewById(R.id.imageMenu)
            .setOnClickListener { view -> drawerLayout.openDrawer(GravityCompat.START) }
        val navigationView: NavigationView = findViewById(R.id.navigationView)
        navigationView.setItemIconTintList(null)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            val id: Int = menuItem.getItemId()
            if (R.id.menuProfile == id) {
                // Add intent
            } else if (R.id.menuNofications == id) {
                // Add intent
            }
            true
        }
    }
}