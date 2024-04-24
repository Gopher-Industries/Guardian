package deakin.gopher.guardian.view.caretaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R

class Adapter_for_AddedTask(var tasks: MutableList<TaskAddedClass>) : RecyclerView.Adapter<Adapter_for_AddedTask.ViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int, ): Adapter_for_AddedTask.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_adapter_for_task, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: Adapter_for_AddedTask.ViewHolder, position: Int) {

        holder.texttask.text = tasks[position].taskName

    }

    override fun getItemCount(): Int {

        return  tasks.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val texttask: TextView = itemView.findViewById(R.id.texttask)
    }
}