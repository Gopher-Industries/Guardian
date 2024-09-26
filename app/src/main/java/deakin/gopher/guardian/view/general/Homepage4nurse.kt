package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amin.muteapp.api.ServiceBuilder
import com.google.gson.Gson
import com.google.gson.JsonElement
import deakin.gopher.guardian.R
import deakin.gopher.guardian.api.nurse.model.LoginResponse
import deakin.gopher.guardian.model.RegistrationStatusMessage
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.EmailPasswordAuthService
import deakin.gopher.guardian.services.NavigationService
import deakin.gopher.guardian.services.api.ApiService
import deakin.gopher.guardian.view.hide
import org.apache.commons.httpclient.HttpStatus
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Homepage4nurse : AppCompatActivity() {

    var sessionManager: SessionManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage4nurse)

        sessionManager = SessionManager(this@Homepage4nurse)
        sessionManager!!.getToken()?.let { Log.e("Print--->", it) }


        val tasksButton: Button = findViewById(R.id.tasksButton_nurse)
        val settingsButton: Button = findViewById(R.id.settingsButton_nurse)
        val signOutButton: Button = findViewById(R.id.sighOutButton_nurse)

        tasksButton.setOnClickListener {
            NavigationService(this).onLaunchTasks()
        }

        // settings button
        settingsButton.setOnClickListener {
            NavigationService(this).onSettings()
        }

        // sign out button
        signOutButton.setOnClickListener {
            EmailPasswordAuthService.signOut(this)
            finish()
        }

        getPatients()

        getProfile()

        getDailyReports()

    }

    private fun getPatients() {
        val apiservice = ServiceBuilder.buildService(this)?.create(ApiService::class.java)
        val call = apiservice?.getPatients()
        call?.enqueue(
            object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>,
                    response: Response<JsonElement>,
                ) {
                    if (response.code() == HttpStatus.SC_OK && response.isSuccessful && response.body() != null) {
                        Log.e("Responsee---->", Gson().toJson(response.body()))

                    } else if (response.code() == HttpStatus.SC_BAD_REQUEST) {
                        // Handle error
                        val errorBody = response.errorBody()!!.string()
                        val jsonObject = JSONObject(errorBody)
                        val errorMessage = jsonObject.optString("error")
                        Toast.makeText(this@Homepage4nurse, errorMessage, Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(
                    call: Call<JsonElement>,
                    t: Throwable,
                ) {
                    t.message
                }
            },
        )
    }

    private fun getProfile() {
        val apiservice = ServiceBuilder.buildService(this)?.create(ApiService::class.java)
        val call = apiservice?.getProfile()
        call?.enqueue(
            object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>,
                    response: Response<JsonElement>,
                ) {
                    if (response.code() == HttpStatus.SC_OK && response.isSuccessful && response.body() != null) {
                        // Handle successful registration
                        Log.e("Responsee---->", Gson().toJson(response.body()))

                    } else if (response.code() == HttpStatus.SC_BAD_REQUEST) {
                        // Handle error
                        val errorBody = response.errorBody()!!.string()
                        val jsonObject = JSONObject(errorBody)
                        val errorMessage = jsonObject.optString("error")
                        Toast.makeText(this@Homepage4nurse, errorMessage, Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(
                    call: Call<JsonElement>,
                    t: Throwable,
                ) {
                    // Handle failure
                    t.message
                }
            },
        )
    }

    private fun getDailyReports() {
        val apiservice = ServiceBuilder.buildService(this)?.create(ApiService::class.java)
        val call = apiservice?.getDailyReports()
        call?.enqueue(
            object : Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>,
                    response: Response<JsonElement>,
                ) {
                    if (response.code() == HttpStatus.SC_OK && response.isSuccessful && response.body() != null) {
                        // Handle successful registration
                        Log.e("Responsee---->", Gson().toJson(response.body()))

                    } else if (response.code() == HttpStatus.SC_BAD_REQUEST) {
                        // Handle error
                        val errorBody = response.errorBody()!!.string()
                        val jsonObject = JSONObject(errorBody)
                        val errorMessage = jsonObject.optString("error")
                        Toast.makeText(this@Homepage4nurse, errorMessage, Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(
                    call: Call<JsonElement>,
                    t: Throwable,
                ) {
                    // Handle failure
                    t.message
                }
            },
        )
    }


}
