package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.NotificationsAdapter
import deakin.gopher.guardian.model.Notification
import deakin.gopher.guardian.services.api.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationsActivity : AppCompatActivity() {

    private lateinit var newNotificationsAdapter: NotificationsAdapter
    private lateinit var previousNotificationsAdapter: NotificationsAdapter
    private lateinit var newNotificationsRecyclerView: RecyclerView
    private lateinit var previousNotificationsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        // Initialize RecyclerViews
        newNotificationsRecyclerView = findViewById(R.id.newNotificationsRecyclerView)
        previousNotificationsRecyclerView = findViewById(R.id.previousNotificationsRecyclerView)

        newNotificationsAdapter = NotificationsAdapter { notification ->
            // Handle click for New Notifications
            navigateToNextPage(notification)
        }
        previousNotificationsAdapter = NotificationsAdapter { notification ->
            // Handle click for Previous Notifications
            navigateToNextPage(notification)
        }

        // Set up RecyclerViews
        newNotificationsRecyclerView.adapter = newNotificationsAdapter
        newNotificationsRecyclerView.layoutManager = LinearLayoutManager(this)

        previousNotificationsRecyclerView.adapter = previousNotificationsAdapter
        previousNotificationsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Load notifications
        loadNotifications()
    }

    private fun fetchNotifications() {
        val call = ApiClient.apiService.getNotifications()
        call.enqueue(object : Callback<List<Notification>> {
            override fun onResponse(call: Call<List<Notification>>, response: Response<List<Notification>>) {
                if (response.isSuccessful) {
                    val notifications = response.body() ?: emptyList()
                    val (newNotifications, previousNotifications) = notifications.partition { it.isNew }
                    newNotificationsAdapter.submitList(newNotifications)
                    previousNotificationsAdapter.submitList(previousNotifications)
                } else {
                    Toast.makeText(this@NotificationsActivity, "Failed to load notifications", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Notification>>, t: Throwable) {
                Toast.makeText(this@NotificationsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToNextPage(notification: Notification) {
        val intent = Intent(this, SendMessageActivity::class.java)
        intent.putExtra("notification_id", notification.id) // Pass notification ID
        intent.putExtra("notification_message", notification.message) // Pass the notification message
        startActivity(intent)
    }


    private fun loadNotifications() {
        // New Notifications (4 entries)
        val newNotifications = listOf(
            Notification(1, "Nurse send a notification about John's condition", "9/10/2024", true),
            Notification(2, "Caretaker has sent updates on Kelly's activities", "9/10/2024", true),
            Notification(3, "Nurse send a notification about James's condition", "11/10/2024", true),
            Notification(4, "Caretaker has sent updates on Anne's activities", "12/10/2024", true)
        )

        // Previous Notifications (4 entries)
        val previousNotifications = listOf(
            Notification(5, "Nurse send a notification about Eva's condition", "10/9/2024", false),
            Notification(6, "Caretaker has sent updates on Helen's activities", "13/9/2024", false),
            Notification(7, "Nurse send a notification about Bruce's condition", "13/9/2024", false),
            Notification(8, "Caretaker has sent updates on Jack's activities", "13/9/2024", false)
        )

        // Submit data to the adapters
        newNotificationsAdapter.submitList(newNotifications)
        previousNotificationsAdapter.submitList(previousNotifications)
    }

}
