package deakin.gopher.guardian.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.databinding.NotificationLayoutBinding
import java.text.SimpleDateFormat
import java.util.Locale

class NotificationAdapter(
    private val context: Context,
    private val list: ArrayList<String>
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    class ViewHolder(binding: NotificationLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var binding: NotificationLayoutBinding = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            NotificationLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
    }

    override fun getItemCount(): Int {
        return list.size
    }

}