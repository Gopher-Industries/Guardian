package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.api.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.EditText
import android.widget.LinearLayout

class NurseProfileActivity : AppCompatActivity() {

    private lateinit var tvNurseName: TextView
    private lateinit var tvNurseEmail: TextView
    private lateinit var tvNursePhone: TextView
    private lateinit var tvNurseRole: TextView
    private lateinit var tvError: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEditHint: TextView
    private lateinit var editFormContainer: LinearLayout
    private lateinit var etNurseName: EditText
    private lateinit var etNurseEmail: EditText
    private lateinit var etNurseRole: EditText
    private lateinit var etNurseOrganization: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nurse_profile)

        tvNurseName = findViewById(R.id.tvNurseName)
        tvNurseEmail = findViewById(R.id.tvNurseEmail)
        tvNursePhone = findViewById(R.id.tvNursePhone)
        tvNurseRole = findViewById(R.id.tvNurseRole)
        tvError = findViewById(R.id.tvError)
        tvEditHint = findViewById(R.id.tvEditHint)

        editFormContainer = findViewById(R.id.editFormContainer)
        etNurseName = findViewById(R.id.etNurseName)
        etNurseEmail = findViewById(R.id.etNurseEmail)
        etNurseRole = findViewById(R.id.etNurseRole)
        etNurseOrganization = findViewById(R.id.etNurseOrganization)

        findViewById<View>(R.id.ivEditProfile).setOnClickListener {
            tvEditHint.visibility = View.VISIBLE
            editFormContainer.visibility = View.VISIBLE

            etNurseName.setText(tvNurseName.text.toString())
            etNurseEmail.setText(tvNurseEmail.text.toString())
            etNurseRole.setText(tvNurseRole.text.toString())
            etNurseOrganization.setText(tvNursePhone.text.toString().replace("Organization: ", ""))
        }
        progressBar = findViewById(R.id.progressBar)

        fetchNurseProfile()
    }

    private fun fetchNurseProfile() {
        val token = "Bearer ${SessionManager.getToken()}"
        val email = SessionManager.getCurrentUser().email

        progressBar.visibility = View.VISIBLE
        tvError.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            val response =
                try {
                    ApiClient.apiService.getNurseProfile(token, email)
                } catch (e: Exception) {
                    println("NURSE_PROFILE_EXCEPTION: ${e.message}")
                    null
                }

            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE

                if (response?.isSuccessful == true && response.body() != null) {
                    val nurseProfile = response.body()!!

                    tvNurseName.text = nurseProfile.fullname ?: "Name not available"
                    tvNurseEmail.text = nurseProfile.email ?: "Email not available"
                    tvNurseRole.text = nurseProfile.role?.name ?: "Role not available"
                    tvNursePhone.text =
                        "Organization: ${nurseProfile.organization?.name ?: "Not available"}"

                    tvError.visibility = View.GONE
                } else {
                    tvError.visibility = View.VISIBLE
                    tvError.text = "Failed to load nurse profile"
                }
            }
        }
    }
}