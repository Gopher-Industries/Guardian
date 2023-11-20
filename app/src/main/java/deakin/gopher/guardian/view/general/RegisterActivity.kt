package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import deakin.gopher.guardian.R

class RegisterActivity : AppCompatActivity() {
    var mEmail: EditText? = null
    var mPassword: EditText? = null
    var mRegisterBtn: Button? = null
    var mLoginBtn: TextView? = null
    var Auth: FirebaseAuth? = null
    var progressBar: ProgressBar? = null
    var fStore: FirebaseFirestore? = null
    var userID: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val backToLoginButton = findViewById<Button>(R.id.backToLoginButton)
        backToLoginButton.setOnClickListener { view: View? ->
            val loginIntent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(loginIntent)
        }
        val mEmail: EditText = findViewById(R.id.Email)
        val mPassword: EditText = findViewById(R.id.password)
        val mRegisterBtn: Button = findViewById(R.id.registerBtn)
        val mLoginBtn: Button = findViewById(R.id.loginRegisterBtn)


        val Auth = FirebaseAuth.getInstance()
        val fStore = FirebaseFirestore.getInstance()


        val progressBar = findViewById(R.id.progressBar)
        mRegisterBtn.setOnClickListener { v: View? ->
            val email = mEmail.text.toString().trim { it <= ' ' }
            val password = mPassword.text.toString().trim { it <= ' ' }
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
            progressBar.setVisibility(View.VISIBLE)

            // register the user in firebase
            Auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task: Task<AuthResult?> ->
                    if (task.isSuccessful) {

                        // send verification link
                        val fuser = Auth.currentUser
                        fuser
                            .sendEmailVerification()
                            .addOnSuccessListener { aVoid: Void? ->
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Verification Email Has been Sent.",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                            .addOnFailureListener { e: Exception ->
                                Log.d(
                                    TAG,
                                    "onFailure: Email not sent " + e.message
                                )
                            }
                        Toast.makeText(
                            this@RegisterActivity,
                            "User Created.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        userID = Auth.currentUser!!.uid
                        val documentReference = fStore.collection("users").document(
                            userID!!
                        )
                        val user: MutableMap<String, Any> = HashMap()
                        user["email"] = email
                        documentReference
                            .set(user)
                            .addOnSuccessListener { aVoid: Void? ->
                                Log.d(
                                    TAG,
                                    "onSuccess: user Profile is created for $userID"
                                )
                            }
                            .addOnFailureListener { e: Exception ->
                                Log.d(
                                    TAG,
                                    "onFailure: $e"
                                )
                            }
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Error ! " + task.exception!!.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        progressBar.setVisibility(View.GONE)
                    }
                }
        }
        
        mLoginBtn.setOnClickListener(
            View.OnClickListener { v: View? ->
                startActivity(
                    Intent(
                        applicationContext, LoginActivity::class.java
                    )
                )
            })
    }

    companion object {
        const val TAG = "TAG"
    }
}