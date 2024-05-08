package deakin.gopher.guardian.view.caretaker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.Adapter_for_TaskAdd
import deakin.gopher.guardian.view.general.Homepage4caretaker
import deakin.gopher.guardian.view.general.LoginActivity
import com.google.firebase.database.Query
import deakin.gopher.guardian.DataBase.DataBase
import deakin.gopher.guardian.model.Task
import java.util.Objects

class AddTaskActivity : AppCompatActivity() {

    var Task_list_searchView: SearchView? = null
    var taskListMenuButton: ImageView? = null
   var cd_for_AddTask: CardView?=null
    var query: Query? = null

    var add_task_button:Button?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        val addPatientIcon = findViewById<ImageView>(R.id.imageView62)
        taskListMenuButton = findViewById<ImageView>(R.id.Task_list_menu_button)
        cd_for_AddTask=findViewById(R.id.cd_for_AddTask)
        add_task_button=findViewById(R.id.add_task_button);
        var drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        var recyclerview = findViewById<RecyclerView>(R.id.recycleView)
        Task_list_searchView = findViewById(R.id.Task_list_searchView)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val menu = navigationView.menu
        menu.clear()


        navigationView.inflateMenu(R.menu.task_nav_menu)


        navigationView.itemIconTintList = null


        add_task_button?.setOnClickListener{

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
                                cd_for_AddTask?.visibility=View.VISIBLE
                                if (null != drawerLayout) {
                                    drawerLayout.closeDrawers()
                                }
                            }


                            R.id.nav_signout -> {

                                SignOut()

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



        val adapterforTask =
            Adapter_for_TaskAdd(this@AddTaskActivity, all_options)
        recyclerview!!.setLayoutManager(GridLayoutManager(this@AddTaskActivity, 1))
        recyclerview!!.setAdapter(adapterforTask)
        adapterforTask.startListening()




    }
    fun SignOut()
    {
        var dataBase = DataBase(this@AddTaskActivity)
        dataBase.open()
        dataBase.LogOut()
        dataBase.close()
    }
    val all_query: Query = FirebaseDatabase.getInstance().reference.child("Add_Task")

    val all_options = FirebaseRecyclerOptions.Builder<Task>()
        .setQuery(
            all_query
        ) { snapshot: DataSnapshot ->
            val Task_Name =
                if (null == snapshot.child("Task_Name").value) "" else snapshot.child("Task_Name").value
                    .toString()

            val Description = if (null == snapshot.child("Description")
                    .value
            ) " " else snapshot.child("Description").value.toString()



            val task = Task(Task_Name,Description

            )


            task
        }
        .build()


}