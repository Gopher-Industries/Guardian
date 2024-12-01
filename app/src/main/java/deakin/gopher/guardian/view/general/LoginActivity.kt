package deakin.gopher.guardian.view.general

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import deakin.gopher.guardian.R
import deakin.gopher.guardian.services.NavigationService

class LoginActivity : BaseActivity() {
    private var userRole: String = "Caretaker"
    private lateinit var gsoClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private val navigationHelper = NavigationService.NavigationHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // UI Elements
        val mEmail: EditText = findViewById(R.id.Email)
        val mPassword: EditText = findViewById(R.id.password)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val loginButton: Button = findViewById(R.id.loginBtn)
        val mCreateBtn: Button = findViewById(R.id.loginRegisterBtn)
        val loginGoogleButton: Button = findViewById(R.id.loginGoogleBtn)
        val forgotTextLink: TextView = findViewById(R.id.forgotPassword)
        val loginRoleRadioGroup: RadioGroup = findViewById(R.id.login_role_radioGroup)

        // FirebaseAuth Initialization
        auth = FirebaseAuth.getInstance()

        // Google Sign-In Configuration
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        gsoClient = GoogleSignIn.getClient(this, gso)

        // Role Selection
        loginRoleRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            userRole =
                when (checkedId) {
                    R.id.admin_radioButton -> "Admin"
                    R.id.caretaker_radioButton -> "Caretaker"
                    R.id.nurse_radioButton -> "Nurse"
                    else -> "Caretaker"
                }
        }

        // Login Button Listener
        loginButton.setOnClickListener {
            val email = mEmail.text.toString().trim()
            val password = mPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE
                loginUser(email, password, progressBar)
            } else {
                Toast.makeText(this, "Please enter both email and password.", Toast.LENGTH_SHORT).show()
            }
        }

        // Registration Button Listener
        mCreateBtn.setOnClickListener {
            val email = mEmail.text.toString().trim()
            val password = mPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE
                registerUser(email, password, userRole, progressBar)
            } else {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            }
        }

        // Google Sign-In Listener
        loginGoogleButton.setOnClickListener {
            val signInIntent = gsoClient.signInIntent
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
        }

        // Password Reset Listener
        forgotTextLink.setOnClickListener {
            val resetMail = EditText(this)
            val passwordResetDialog = AlertDialog.Builder(this)
            passwordResetDialog.setTitle("Reset Password")
            passwordResetDialog.setMessage("Enter your email to receive the reset link:")
            passwordResetDialog.setView(resetMail)
            passwordResetDialog.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                val mail = resetMail.text.toString()
                sendResetPasswordEmail(mail)
            }
            passwordResetDialog.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            passwordResetDialog.create().show()
        }
    }

    private fun loginUser(
        email: String,
        password: String,
        progressBar: ProgressBar,
    ) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            progressBar.visibility = View.GONE
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid
                userId?.let {
                    db.collection("users").document(it).get().addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            val role = dbTask.result?.getString("role")
                            role?.let { navigationHelper.navigateToRoleDashboard(it) }
                                ?: Toast.makeText(this, "Role not found.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to fetch user role.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(
        email: String,
        password: String,
        role: String,
        progressBar: ProgressBar,
    ) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            progressBar.visibility = View.GONE
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid
                userId?.let {
                    val userData = hashMapOf("email" to email, "role" to role)
                    db.collection("users").document(it).set(userData).addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            navigationHelper.navigateToRoleDashboard(role)
                        } else {
                            Toast.makeText(this, "Failed to save user data.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendResetPasswordEmail(email: String) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Reset link sent to $email", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to send reset link.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val RC_GOOGLE_SIGN_IN = 1000
    }
}
