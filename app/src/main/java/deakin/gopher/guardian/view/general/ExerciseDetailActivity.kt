package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R

class ExerciseDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_detail)

        // Get data passed from ExercisePortalActivity
        val title = intent.getStringExtra("title")
        val desc = intent.getStringExtra("desc")

        // Bind UI
        val titleTextView = findViewById<TextView>(R.id.detail_title)
        val descTextView = findViewById<TextView>(R.id.detail_desc)

        titleTextView.text = title
        descTextView.text = desc
    }
}
