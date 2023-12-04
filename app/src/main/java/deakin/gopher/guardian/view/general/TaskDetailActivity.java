package deakin.gopher.guardian.view.general;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.model.Task;

public class TaskDetailActivity extends AppCompatActivity {

    private TextView taskDescriptionTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        taskDescriptionTextView = findViewById(R.id.task_detail_description_text_view);

        String taskId = getIntent().getStringExtra("taskId");

        if (taskId != null) {

            DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference("tasks").child(taskId);
            taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Task task = dataSnapshot.getValue(Task.class);
                        if (task != null) {
                            taskDescriptionTextView.setText(task.getDescription());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(TaskDetailActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
