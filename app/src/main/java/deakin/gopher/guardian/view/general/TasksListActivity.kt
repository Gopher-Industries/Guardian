package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import deakin.gopher.guardian.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TasksListActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var menuButton: ImageView
    private lateinit var addTaskButton: MaterialButton

    private lateinit var cardView: CardView
    private lateinit var expandIcon: ImageView
    private lateinit var descText: TextView
    private lateinit var assignedText: TextView
    private var isExpanded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks_list)

        // Drawer setup
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        menuButton = findViewById(R.id.task_list_manu_button)

        // Handle nav drawer toggle
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Handle navigation item selections
        navigationView.setNavigationItemSelectedListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this@TasksListActivity, Homepage4caretaker::class.java))
                }
                R.id.add_task -> {
                    startActivity(Intent(this@TasksListActivity, TaskAddActivity::class.java))
                }
                R.id.nav_signout -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this@TasksListActivity, LoginActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        // Handle Add Task button
        addTaskButton = findViewById(R.id.add_task_material_button)
        addTaskButton.setOnClickListener {
            val intent = Intent(this@TasksListActivity, TaskAddActivity::class.java)
            startActivity(intent)
        }

        // Handle Card Expand/Collapse logic
        cardView = findViewById(R.id.demo_task_card)
        expandIcon = findViewById(R.id.expand_icon)
        descText = findViewById(R.id.task_desc)
        assignedText = findViewById(R.id.task_assigned)

        expandIcon.setOnClickListener {
            isExpanded = !isExpanded
            descText.visibility = if (isExpanded) TextView.VISIBLE else TextView.GONE
            assignedText.visibility = if (isExpanded) TextView.VISIBLE else TextView.GONE
            expandIcon.setImageResource(
                if (isExpanded) R.drawable.ic_expand_less else R.drawable.ic_expand_more
            )
        }

        // Optional: Handle SearchView query
        val searchView: SearchView = findViewById(R.id.task_list_searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(this@TasksListActivity, "Searching for: $query", Toast.LENGTH_SHORT).show()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Implement real-time filtering if needed
                return false
            }
        })
    }
}
