package deakin.gopher.guardian.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.PatientStatus
import deakin.gopher.guardian.model.Task
import deakin.gopher.guardian.view.general.PatientProfileActivity

class Adapter_for_TaskAdd(
    private val context: Context,
    options: FirebaseRecyclerOptions<Task?>,
   ) : FirebaseRecyclerAdapter<Task?, Adapter_for_TaskAdd.MyViewHolder?>(options) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.task_list_item, parent, false)
        return Adapter_for_TaskAdd.MyViewHolder(view, context)
    }

    override fun onDataChanged() {
        super.onDataChanged()
        if (itemCount == 0) {
            Log.d("TaskListAdapter", "No archived patients found.")
        } else {
            Log.d("TaskListAdapter", "Archived Task loaded: $itemCount")
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Adapter_for_TaskAdd.MyViewHolder, position: Int, model: Task, ) {

        holder.TaskName.text = "${model.Task_Name}"

        holder.tv_Task_Description.text = "${model.Description}"
    }



    class MyViewHolder(itemView: View, private val context: Context) :
        RecyclerView.ViewHolder(itemView) {
        var TaskName: TextView
        var tv_Task_Description:TextView

        init {
            TaskName = itemView.findViewById(R.id.TaskName)

            tv_Task_Description = itemView.findViewById(R.id.tv_Task_Description)
        }


    }


}