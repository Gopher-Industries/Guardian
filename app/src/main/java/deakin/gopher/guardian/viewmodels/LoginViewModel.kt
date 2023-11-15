package deakin.gopher.guardian.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import deakin.gopher.guardian.model.login.EmailAddress
import deakin.gopher.guardian.model.login.GuardianAuthService
import deakin.gopher.guardian.model.login.LoginError
import deakin.gopher.guardian.model.login.LoginResult
import deakin.gopher.guardian.model.login.NavigationDestination
import deakin.gopher.guardian.model.login.Password
import deakin.gopher.guardian.model.login.Role
import deakin.gopher.guardian.model.login.RoleName

class LoginViewModel : ViewModel() {
    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult>
        get() = _loginResult

    private val _navigationDestination = MutableLiveData<NavigationDestination>()
    val navigationDestination: LiveData<NavigationDestination>
        get() = _navigationDestination

    val authService = GuardianAuthService().firebaseAuthService

    fun onLoginButtonClicked(rawEmail: String, rawPassword: String, rawRoleName: RoleName) {
        val emailAddress = EmailAddress(rawEmail)
        val password = Password(rawPassword)
        val role = Role(rawRoleName)

        if (!emailAddress.isValid()) {
            _loginResult.value = LoginResult.Failure(LoginError.EmailInvalidError)
            return
        }

        if (!password.isValid()) {
            _loginResult.value = LoginResult.Failure(LoginError.PasswordValidationError)
            return
        }

        if (!role.isValid()) {
            throw IllegalStateException("Role should be valid. Received ${role.roleName}")
        }

        authService.signInWithEmailAndPassword(emailAddress.emailAddress, password.password)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    setNavigationDestinationForRole(role)
                    _loginResult.value = LoginResult.Success(role)
                } else {
                    _loginResult.value = LoginResult.Failure(LoginError.AuthenticationError)
                }
            }
    }

    private fun setNavigationDestinationForRole(role: Role) {
        when (role.roleName) {
            is RoleName.Caretaker -> {
                _navigationDestination.value = NavigationDestination.CaretakerHomepage
            }

            is RoleName.Admin -> {
                _navigationDestination.value = NavigationDestination.AdminHomepage
            }

            is RoleName.Nurse -> {
                _navigationDestination.value = NavigationDestination.NurseHomepage
            }
        }
    }
//    onLoginButtonClicked() {
//        val email = mEmail.text.toString().trim { it <= ' ' }
//        val password = mPassword.text.toString().trim { it <= ' ' }
//        val role: String
//        val selectedRadioButtonId = role_radioGroup.checkedRadioButtonId
//        if (-1 != selectedRadioButtonId) {
//            val seletedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
//            role = seletedRadioButton.text.toString()
//            val sharedPreferences = getSharedPreferences("MY_PREF", AppCompatActivity.MODE_PRIVATE)
//            val editor = sharedPreferences.edit()
//            if (role == "Caretaker") {
//                editor.putInt("login_role", 0)
//            } else {
//                editor.putInt("login_role", 1)
//            }
//            editor.apply()
//        } else {
//            Toast.makeText(this@LoginActivity, "please choose a role", Toast.LENGTH_SHORT)
//                .show()
//            return@setOnClickListener
//        }
//        if (TextUtils.isEmpty(email)) {
//            mEmail.error = "Email is Required."
//            return@setOnClickListener
//        }
//        if (TextUtils.isEmpty(password)) {
//            mPassword.error = "Password is Required."
//            return@setOnClickListener
//        }
//        if (6 > password.length) {
//            mPassword.error = "Password Must be >= 6 Characters"
//            return@setOnClickListener
//        }
//        progressBar.visibility = View.VISIBLE
//
//        // authenticate the user
//        Auth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task: Task<AuthResult?> ->
//                var role1 = ""
//                val selectedRadioButtonId1 = role_radioGroup.checkedRadioButtonId
//                if (-1 != selectedRadioButtonId1) {
//                    val seletedRadioButton =
//                        findViewById<RadioButton>(selectedRadioButtonId1)
//                    role1 = seletedRadioButton.text.toString()
//                }
//                if (task.isSuccessful) {
//                    Toast.makeText(
//                        this@LoginActivity, "Logged in Successfully", Toast.LENGTH_SHORT
//                    )
//                        .show()
//                    if (role1 == "Caretaker") {
//                        Toast.makeText(
//                            this@LoginActivity, "Logged in as CareTaker", Toast.LENGTH_SHORT
//                        )
//                            .show()
//                        startActivity(
//                            Intent(applicationContext, Homepage4caretaker::class.java)
//                        )
//                    } else {
//                        startActivity(
//                            Intent(
//                                applicationContext,
//                                Homepage4admin::class.java
//                            )
//                        )
//                    }
//                } else {
//                    Toast.makeText(
//                        this@LoginActivity,
//                        "Error ! " + Objects.requireNonNull(task.exception).message,
//                        Toast.LENGTH_SHORT
//                    )
//                        .show()
//                    progressBar.visibility = View.GONE
//                }
//            }
//    }
}