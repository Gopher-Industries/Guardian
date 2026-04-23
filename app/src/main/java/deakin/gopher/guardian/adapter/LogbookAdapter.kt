package deakin.gopher.guardian.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.logbook.LogEntry
import java.text.SimpleDateFormat
import java.util.*

class LogbookAdapter(
    private val onLogClick: (LogEntry) -> Unit,
    private val onDeleteClick: (LogEntry) -> Unit
) : RecyclerView.Adapter<LogbookAdapter.LogEntryViewHolder>() {

    private var logs = mutableListOf<LogEntry>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogEntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_log_entry, parent, false)
        return LogEntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogEntryViewHolder, position: Int) {
        holder.bind(logs[position], onLogClick, onDeleteClick)
    }

    override fun getItemCount() = logs.size

    fun updateLogs(newLogs: List<LogEntry>) {
        logs.clear()
        logs.addAll(newLogs)
        notifyDataSetChanged()
    }

    class LogEntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.log_title)
        private val descriptionText: TextView = itemView.findViewById(R.id.log_description)
        private val createdByText: TextView = itemView.findViewById(R.id.created_by_text)
        private val createdAtText: TextView = itemView.findViewById(R.id.created_at_text)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)
        private val roleBadge: TextView = itemView.findViewById(R.id.role_badge)

        fun bind(
            log: LogEntry,
            onLogClick: (LogEntry) -> Unit,
            onDeleteClick: (LogEntry) -> Unit
        ) {
            titleText.text = log.title
            descriptionText.text = log.description
            createdByText.text = log.createdBy.fullname

            // Format the creation date
            val formattedDate = formatCreatedAt(log.createdAt)
            createdAtText.text = formattedDate

            // Set role badge
            roleBadge.text = log.createdBy.role.uppercase()
            val roleBadgeColor = when (log.createdBy.role.lowercase()) {
                "nurse" -> itemView.context.getColor(R.color.nurse_role_color)
                "admin" -> itemView.context.getColor(R.color.admin_role_color)
                "doctor" -> itemView.context.getColor(R.color.doctor_role_color)
                else -> itemView.context.getColor(R.color.default_role_color)
            }
            roleBadge.setBackgroundColor(roleBadgeColor)

            // Click listeners
            itemView.setOnClickListener { onLogClick(log) }
            deleteButton.setOnClickListener { onDeleteClick(log) }
        }

        private fun formatCreatedAt(createdAt: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val outputFormat = SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault())
                val date = inputFormat.parse(createdAt)
                date?.let { outputFormat.format(it) } ?: createdAt
            } catch (e: Exception) {
                // Fallback to original string if parsing fails
                createdAt
            }
        }
    }
}