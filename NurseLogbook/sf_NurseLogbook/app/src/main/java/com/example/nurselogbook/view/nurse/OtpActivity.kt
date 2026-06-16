package com.example.nurselogbook.view.nurse

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nurselogbook.databinding.ActivityOtpBinding

class OtpActivity : AppCompatActivity() {

    private lateinit var b: ActivityOtpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(b.root)

        val boxes = listOf(b.et1, b.et2, b.et3, b.et4, b.et5, b.et6)

        boxes.forEachIndexed { i, et ->
            et.filters = arrayOf(InputFilter.LengthFilter(1))
            et.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && i < boxes.lastIndex) boxes[i + 1].requestFocus()
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (before == 1 && count == 0 && i > 0 && et.text?.isEmpty() == true) {
                        boxes[i - 1].requestFocus()
                    }
                }
            })
        }

        b.btnSubmitOtp.setOnClickListener {
            val code = boxes.joinToString("") { it.text?.toString().orEmpty() }
            if (code.length == 6) {
                startActivity(Intent(this, NurseTaskListActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Enter all 6 digits", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
