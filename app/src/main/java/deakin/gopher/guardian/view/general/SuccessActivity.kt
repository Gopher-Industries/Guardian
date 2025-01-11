package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R

class SuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        // Find the Back to Notifications button
        val backToNotificationsButton: Button = findViewById(R.id.backToNotificationsButton)

        // Set an onClickListener to navigate back to NotificationsActivity
        backToNotificationsButton.setOnClickListener {
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
            finish() // Close the SuccessActivity to prevent going back here
        }
    }
}



