package deakin.gopher.guardian.view.nurse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import deakin.gopher.guardian.adapter.NotificationAdapter
import deakin.gopher.guardian.databinding.ActivityNotificationBinding

class NotificationActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var list: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener(this)
        list = ArrayList()

        list.add("")
        list.add("")
        list.add("")
        list.add("")
        list.add("")
        list.add("")
        list.add("")
        list.add("")
        list.add("")
        list.add("")
        list.add("")
        list.add("")
        list.add("")
        list.add("")
        list.add("")
        list.add("")

        notificationAdapter = NotificationAdapter(this, list)
        binding.rvNotification.adapter = notificationAdapter

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.ivBack.id -> {
                finish()
            }
        }
    }
}