package com.example.nurselogbook.view.nurse

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nurselogbook.databinding.ActivityNurseLoginBinding

class NurseLoginActivity : AppCompatActivity() {

    private lateinit var b: ActivityNurseLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityNurseLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnLogin.setOnClickListener {
            val email = b.etEmail.text?.toString()?.trim().orEmpty()
            val pass  = b.etPassword.text?.toString()?.trim().orEmpty()
            if (email == "mdevereu@deakin.edu.au" && pass == "Aa1!Bb2@Cc3#") {
                startActivity(Intent(this, OtpActivity::class.java))
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
