package deakin.gopher.guardian;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ModulesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up module navigation
        setupModuleNavigation();
    }

    // This method is called when the back button is clicked
    public void onBackButtonClicked(View view) {
        // This will go back to the previous activity
        onBackPressed(); // Calls the system's onBackPressed() method
    }

    private void setupModuleNavigation() {
        // Lambda for Module 1 click listener
        TextView module1 = findViewById(R.id.module1);
        module1.setOnClickListener(v -> {
            Intent intent = new Intent(ModulesActivity.this, Module1CoursesActivity.class);
            startActivity(intent);
        });

        // Lambda for Module 2 click listener
        TextView module2 = findViewById(R.id.module2);
        module2.setOnClickListener(v -> {
            Intent intent = new Intent(ModulesActivity.this, Module2CoursesActivity.class);
            startActivity(intent);
        });

        // Lambda for Module 3 click listener
        TextView module3 = findViewById(R.id.module3);
        module3.setOnClickListener(v -> {
            Intent intent = new Intent(ModulesActivity.this, Module3CoursesActivity.class);
            startActivity(intent);
        });
    }


}
