package deakin.gopher.guardian.view.general

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import deakin.gopher.guardian.R
import java.util.Objects

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val mEmail: EditText = findViewById(R.id.Email)
        val mPassword: EditText = findViewById(R.id.password)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val Auth = FirebaseAuth.getInstance()
        val mLoginBtn: Button = findViewById(R.id.loginBtn)
        val mCreateBtn: Button = findViewById(R.id.createText)
        val forgotTextLink: TextView = findViewById(R.id.forgotPassword)
        val role_radioGroup: RadioGroup = findViewById(R.id.login_role_radioGroup)


        mLoginBtn.setOnClickListener {
            val email = mEmail.text.toString().trim { it <= ' ' }
            val password = mPassword.text.toString().trim { it <= ' ' }
            val role: String
            val selectedRadioButtonId = role_radioGroup.checkedRadioButtonId
            if (-1 != selectedRadioButtonId) {
                val seletedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
                role = seletedRadioButton.text.toString()
                val sharedPreferences = getSharedPreferences("MY_PREF", MODE_PRIVATE)
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
        mCreateBtn.setOnClickListener(
            View.OnClickListener { v: View? ->
                startActivity(
                    Intent(
                        applicationContext, RegisterActivity::class.java
                    )
                )
            })
        forgotTextLink.setOnClickListener(
            View.OnClickListener { v: View ->
                val resetMail = EditText(v.context)
                val passwordResetDialog = AlertDialog.Builder(v.context)
                passwordResetDialog.setTitle("Reset Password ?")
                passwordResetDialog.setMessage("Enter Your Email To Received Reset Link.")
                passwordResetDialog.setView(resetMail)
                passwordResetDialog.setPositiveButton(
                    "Yes"
                ) { dialog: DialogInterface?, which: Int ->
                    // extract the email and send reset link
                    val mail = resetMail.text.toString()
                    Auth.sendPasswordResetEmail(mail)
                        .addOnSuccessListener { aVoid: Void? ->
                            Toast.makeText(
                                this@LoginActivity,
                                "Reset Link Sent To Your Email.",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                        .addOnFailureListener { e: Exception ->
                            Toast.makeText(
                                this@LoginActivity,
                                "Error ! Reset Link is Not Sent" + e.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                }
                passwordResetDialog.setNegativeButton(
                    "No"
                ) { dialog: DialogInterface?, which: Int -> }
                passwordResetDialog.create().show()
            })
    }
}