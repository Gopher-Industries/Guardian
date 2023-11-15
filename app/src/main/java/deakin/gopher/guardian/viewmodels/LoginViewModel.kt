package deakin.gopher.guardian.viewmodels

import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import deakin.gopher.guardian.view.general.Homepage4admin
import deakin.gopher.guardian.view.general.Homepage4caretaker
import java.util.Objects

class LoginViewModel : ViewModel() {
    val Auth = FirebaseAuth.getInstance()
    onLoginButtonClicked() {
        val email = mEmail.text.toString().trim { it <= ' ' }
        val password = mPassword.text.toString().trim { it <= ' ' }
        val role: String
        val selectedRadioButtonId = role_radioGroup.checkedRadioButtonId
        if (-1 != selectedRadioButtonId) {
            val seletedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
            role = seletedRadioButton.text.toString()
            val sharedPreferences = getSharedPreferences("MY_PREF", AppCompatActivity.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            if (role == "Caretaker") {
                editor.putInt("login_role", 0)
            } else {
                editor.putInt("login_role", 1)
            }
            editor.apply()
        } else {
            Toast.makeText(this@LoginActivity, "please choose a role", Toast.LENGTH_SHORT)
                .show()
            return@setOnClickListener
        }
        if (TextUtils.isEmpty(email)) {
            mEmail.error = "Email is Required."
            return@setOnClickListener
        }
        if (TextUtils.isEmpty(password)) {
            mPassword.error = "Password is Required."
            return@setOnClickListener
        }
        if (6 > password.length) {
            mPassword.error = "Password Must be >= 6 Characters"
            return@setOnClickListener
        }
        progressBar.visibility = View.VISIBLE

        // authenticate the user
        Auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                var role1 = ""
                val selectedRadioButtonId1 = role_radioGroup.checkedRadioButtonId
                if (-1 != selectedRadioButtonId1) {
                    val seletedRadioButton =
                        findViewById<RadioButton>(selectedRadioButtonId1)
                    role1 = seletedRadioButton.text.toString()
                }
                if (task.isSuccessful) {
                    Toast.makeText(
                        this@LoginActivity, "Logged in Successfully", Toast.LENGTH_SHORT
                    )
                        .show()
                    if (role1 == "Caretaker") {
                        Toast.makeText(
                            this@LoginActivity, "Logged in as CareTaker", Toast.LENGTH_SHORT
                        )
                            .show()
                        startActivity(
                            Intent(applicationContext, Homepage4caretaker::class.java)
                        )
                    } else {
                        startActivity(
                            Intent(
                                applicationContext,
                                Homepage4admin::class.java
                            )
                        )
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Error ! " + Objects.requireNonNull(task.exception).message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    progressBar.visibility = View.GONE
                }
            }
    }
}