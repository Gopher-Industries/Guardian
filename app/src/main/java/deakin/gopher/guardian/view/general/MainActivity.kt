package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import deakin.gopher.guardian.DataBase.DataBase
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.login.RoleName
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.EmailPasswordAuthService
import deakin.gopher.guardian.services.NavigationService

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val getStartedButton = findViewById<Button>(R.id.getStartedButton)
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        getStartedButton.setOnClickListener { _ -> onGetStartedClick() }
        logoutButton.setOnClickListener { _ -> onLogoutClick() }

        FirebaseMessaging.getInstance()
            .token
            .addOnCompleteListener { task: Task<String?> ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d("FCM Token", token ?: "Token is null")
                } else {
                    Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
                }
            }
    }

    private fun onGetStartedClick() {



        val dataBase= DataBase(this@MainActivity);
        dataBase.open()
       val login= dataBase.check_Login_Status()
        dataBase.close();
        if(login!=null && login.isLogin!=null && login.isLogin.equals("true"))
        {
            var UserType=login.userType.toString();

            if(login.isLogin.equals("true"))
            {
                if(UserType.equals(resources.getString(R.string.caretaker_role_name)))
                {
                    startActivity(Intent(this@MainActivity, Homepage4caretaker::class.java))

                }else if(UserType.equals(resources.getString(R.string.company_admin_role_name)))
                {
                    startActivity(Intent(this@MainActivity, Homepage4admin::class.java))

                }
                else if(UserType.equals(resources.getString(R.string.nurse_role_name)))
                {
                    startActivity(Intent(this@MainActivity, Homepage4nurse::class.java))

                }
            }
            else{
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            }


        }
        else{

            startActivity(Intent(this@MainActivity, LoginActivity::class.java))

        }


       /* val sessionManager = SessionManager(this)
        if (!sessionManager.isLoggedIn) {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        } else {
            startActivity(Intent(this@MainActivity, Homepage4caretaker::class.java))
        }*/



    }

    private fun onLogoutClick() {

        var database = DataBase(this@MainActivity)
        database.open()
        database.LogOut()
        database.close()


        EmailPasswordAuthService.signOut(this)
        finish()
    }
}
