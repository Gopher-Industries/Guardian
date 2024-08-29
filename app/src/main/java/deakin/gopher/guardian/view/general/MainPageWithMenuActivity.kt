package deakin.gopher.guardian.view.general

import android.view.View
import android.widget.Button
import androidx.core.view.GravityCompat

class MainPageWithMenuActivity : BaseActivity() {
    var menu_DrawerLayout: DrawerLayout? = null
    var menu_button: Button? = null
    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page_with_menu)
        menu_DrawerLayout = findViewById(R.id.menu_drawerLayout)
        menu_button = findViewById(R.id.menu_button_main_page)
        menu_button!!.setOnClickListener { view: View? ->
            if (menu_DrawerLayout.isDrawerOpen(GravityCompat.START)) {
                menu_DrawerLayout.closeDrawer(GravityCompat.START)
            } else {
                menu_DrawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }
}