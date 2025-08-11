package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R

class ExercisePortalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_portal)

        val exercises = listOf(
            Pair("Arm Stretch", "Stretch your arms upwards for 10 seconds"),
            Pair("Neck Roll", "Gently roll your neck clockwise and counter-clockwise"),
            Pair("Leg Lift", "Lift each leg alternately while seated")
        )

        val container: LinearLayout = findViewById(R.id.exercise_list_container)

        for (exercise in exercises) {
            val title = exercise.first
            val desc = exercise.second

            val exerciseView = LayoutInflater.from(this)
                .inflate(R.layout.exercise_item, container, false)

            val titleView: TextView = exerciseView.findViewById(R.id.exercise_title)
            val descView: TextView = exerciseView.findViewById(R.id.exercise_desc)
            val startButton: Button = exerciseView.findViewById(R.id.start_button)

            titleView.text = title
            descView.text = desc

            startButton.setOnClickListener {
                val intent = Intent(this, ExerciseDetailActivity::class.java)
                intent.putExtra("title", title)
                intent.putExtra("desc", desc)
                startActivity(intent)
            }

            container.addView(exerciseView)
        }
    }
}
