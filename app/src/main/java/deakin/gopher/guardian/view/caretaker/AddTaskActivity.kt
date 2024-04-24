package deakin.gopher.guardian.view.caretaker

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import deakin.gopher.guardian.R
import deakin.gopher.guardian.view.general.Homepage4caretaker
import deakin.gopher.guardian.view.general.LoginActivity

class AddTaskActivity : AppCompatActivity() {

    var Task_list_searchView: SearchView? = null
    var taskListMenuButton: ImageView? = null
   var ll_for_AddTask: LinearLayout?=null

    var Iv_for_AddTask:ImageView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        val addPatientIcon = findViewById<ImageView>(R.id.imageView62)
        taskListMenuButton = findViewById<ImageView>(R.id.Task_list_menu_button)
        ll_for_AddTask=findViewById(R.id.ll_for_AddTask)
        Iv_for_AddTask=findViewById(R.id.Iv_for_AddTask)
        var drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)




        var recyclerview = findViewById<RecyclerView>(R.id.recycleView)
        recyclerview.layoutManager = LinearLayoutManager(this)
        var adapter = Adapter_for_AddedTask(GlobleClass.tasks)
        recyclerview.adapter = adapter

        Task_list_searchView = findViewById(R.id.Task_list_searchView)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val menu = navigationView.menu
        menu.clear()


        navigationView.inflateMenu(R.menu.task_nav_menu)


        navigationView.itemIconTintList = null


        Iv_for_AddTask?.setOnClickListener{

            intent = Intent(this@AddTaskActivity, TaskAddFormActivity::class.java)
            startActivity(intent)
        }


        taskListMenuButton?.setOnClickListener(
            View.OnClickListener { v: View? ->
                if (null != drawerLayout) {
                    drawerLayout.openDrawer(GravityCompat.START)
                    navigationView.setNavigationItemSelectedListener { menuItem: MenuItem ->
                        val id = menuItem.itemId
                        var intent: Intent? = null
                        when (id) {
                            R.id.nav_home -> intent = Intent(
                                this@AddTaskActivity, Homepage4caretaker::class.java
                            )

                            R.id.Add_Task -> {
                                ll_for_AddTask?.visibility=View.VISIBLE
                                if (null != drawerLayout) {
                                    drawerLayout.closeDrawers()
                                }
                            }


                            R.id.nav_signout -> {
                                FirebaseAuth.getInstance().signOut()
                                startActivity(
                                    Intent(
                                        this@AddTaskActivity, LoginActivity::class.java
                                    )
                                )
                                finish()
                            }
                        }
                        if (intent != null) {
                            startActivity(intent)
                            finish()
                        }
                        true
                    }
                }
            })



    }
}