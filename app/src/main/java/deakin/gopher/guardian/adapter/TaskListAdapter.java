package deakin.gopher.guardian.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.model.Task;
import deakin.gopher.guardian.view.general.TaskDetailActivity;

public class TaskListAdapter extends FirebaseRecyclerAdapter<Task, TaskListAdapter.TaskViewHolder> {

  private final Context context;

  public TaskListAdapter(Context context, @NonNull FirebaseRecyclerOptions<Task> options) {
    super(options);
    this.context = context;
  }

  @Override
  protected void onBindViewHolder(@NonNull TaskViewHolder holder, int position, @NonNull Task model) {
    holder.bind(model);
  }

  @NonNull
  @Override
  public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item, parent, false);
    return new TaskViewHolder(view);
  }

  class TaskViewHolder extends RecyclerView.ViewHolder {
    private final TextView taskDescriptionTextView;

    TaskViewHolder(@NonNull View itemView) {
      super(itemView);
      taskDescriptionTextView = itemView.findViewById(R.id.task_description_text_view);

      itemView.setOnClickListener(v -> {
        int position = getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
          Task task = getItem(position);
          if (task != null) {
            Intent intent = new Intent(context, TaskDetailActivity.class);
            intent.putExtra("taskId", task.getTaskId());
            context.startActivity(intent);
          }
        }
      });
    }

    void bind(Task task) {
      taskDescriptionTextView.setText(task.getDescription());
    }
  }
}
