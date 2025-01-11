package deakin.gopher.guardian.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Notification

class NotificationsAdapter(private val onClick: (Notification) -> Unit) :
    RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>() {

    private val notifications = mutableListOf<Notification>()

    fun submitList(newNotifications: List<Notification>) {
        notifications.clear()
        notifications.addAll(newNotifications)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        return NotificationViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    override fun getItemCount(): Int = notifications.size

    class NotificationViewHolder(itemView: View, private val onClick: (Notification) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val notificationIcon: ImageView = itemView.findViewById(R.id.notificationIcon)
        private val notificationMessage: TextView = itemView.findViewById(R.id.notificationMessage)
        private val notificationDate: TextView = itemView.findViewById(R.id.notificationDate)

        fun bind(notification: Notification) {
            notificationMessage.text = notification.message
            notificationDate.text = notification.date
            itemView.setOnClickListener { onClick(notification) }
        }
    }
}
