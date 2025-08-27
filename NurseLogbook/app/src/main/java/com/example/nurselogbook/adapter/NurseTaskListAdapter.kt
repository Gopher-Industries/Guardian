package com.example.nurselogbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nurselogbook.R
import com.example.nurselogbook.model.NurseTask   // <- IMPORTANT: correct import

class NurseTaskListAdapter(
    private val items: MutableList<NurseTask>,
    private val onChecked: (NurseTask, Boolean) -> Unit
) : RecyclerView.Adapter<NurseTaskListAdapter.TaskVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nurse_task, parent, false)
        return TaskVH(view)
    }

    override fun onBindViewHolder(holder: TaskVH, position: Int) {
        val t = items[position]
        holder.title.text = t.title
        holder.details.text = t.details
        holder.patientTime.text = "${t.patientName} • ${t.time}"

        // avoid callback firing during programmatic change
        holder.cbDone.setOnCheckedChangeListener(null)
        holder.cbDone.isChecked = t.done
        holder.cbDone.setOnCheckedChangeListener { _, checked ->
            onChecked(t, checked)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class TaskVH(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.tvTaskTitle)
        val details: TextView = v.findViewById(R.id.tvTaskDetails)
        val patientTime: TextView = v.findViewById(R.id.tvPatientTime)
        val cbDone: CheckBox = v.findViewById(R.id.cbDone)
    }
}

