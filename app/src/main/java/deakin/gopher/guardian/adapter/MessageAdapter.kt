package deakin.gopher.guardian.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.communication.Message
import java.text.SimpleDateFormat
import java.util.Locale

class MessageAdapter(private val messageList: List<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messageList[position]
        holder.textViewMessage.text = message.content

        // Parse the date string to a Date object if it's in a valid format
        val sdfInput = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) // Adjust format as necessary
        val messageDate = sdfInput.parse(message.date)

        val sdfOutput = SimpleDateFormat("hh:mm a", Locale.getDefault()) // Output format for the timestamp
        holder.textViewTimestamp.text = sdfOutput.format(messageDate)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewMessage: TextView = itemView.findViewById(R.id.textViewMessage)
        val textViewTimestamp: TextView = itemView.findViewById(R.id.textViewTimestamp)
    }
}
