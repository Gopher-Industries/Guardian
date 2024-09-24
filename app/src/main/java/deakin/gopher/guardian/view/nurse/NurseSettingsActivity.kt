package deakin.gopher.guardian.view.nurse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import deakin.gopher.guardian.R
import deakin.gopher.guardian.databinding.ActivityNurseSettingsBinding

class NurseSettingsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityNurseSettingsBinding
    private var isDarkMode = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNurseSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.llNotification.setOnClickListener(this)
        binding.llPatientDailyReport.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.llPatientDailyReport.id -> {
                val intent = Intent(this, DailyReportActivity::class.java)
                startActivity(intent)
            }

            binding.llNotification.id -> {
                val intent = Intent(this, NotificationActivity::class.java)
                startActivity(intent)
            }
        }

    }
}