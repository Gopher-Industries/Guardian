package deakin.gopher.guardian.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.databinding.ItemPatientLogBinding
import deakin.gopher.guardian.model.PatientLog
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class PatientLogAdapter(
    private var logs: List<PatientLog>,
    private val onDeleteClick: (PatientLog) -> Unit,
) : RecyclerView.Adapter<PatientLogAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemPatientLogBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding =
            ItemPatientLogBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return ViewHolder(binding)
    }

    override fun getItemCount() = logs.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val log = logs[position]

        holder.binding.title.text = log.title
        holder.binding.description.text = log.description
        holder.binding.meta.text =
            "${log.createdBy.fullname} • ${formatDate(log.createdAt)}"

        holder.binding.deleteBtn.setOnClickListener {
            onDeleteClick(log)
        }
    }

    fun updateData(newLogs: List<PatientLog>) {
        logs = newLogs
        notifyDataSetChanged()
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            val outputFormat =
                SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

            val date = inputFormat.parse(dateString)

            outputFormat.format(date!!)
        } catch (e: Exception) {
            dateString
        }
    }
}
