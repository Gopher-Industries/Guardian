package deakin.gopher.guardian

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class FlexibilityExerciseActivity : AppCompatActivity() {

    data class ExerciseStep(
        val stepTitle: String,
        val stepDescription: String,
        val imageResId: Int
    )

    private val steps = listOf(
        ExerciseStep(
            "Step 1",
            "Sit or stand tall with shoulders relaxed.\n" +
                    "Slowly tilt your head toward your right shoulder (don’t lift your shoulder).\n" +
                    "Hold for 20–30 seconds, feeling a stretch along the opposite side of your neck.\n" +
                    "Switch sides. Repeat 2–3 times per side.",
            R.drawable.side_bend
        ),
        ExerciseStep(
            "Step 2",
            "Sit on the floor with legs straight.\n" +
                    "Reach forward towards your toes.\n" +
                    "Hold for 20–30 seconds.\n" +
                    "Repeat twice.",
            R.drawable.sholder_rolls
        ),
        ExerciseStep(
            "Step 3",
            "Stand with feet hip-width apart.\n" +
                    "Stretch arms above your head and lean to one side.\n" +
                    "Hold for 20 seconds.\n" +
                    "Switch sides.",
            R.drawable.chest_opener
        )
    )

    private var currentStepIndex = 0

    private lateinit var stepTitleText: TextView
    private lateinit var stepDescriptionText: TextView
    private lateinit var stepImageView: ImageView
    private lateinit var backButton: Button
    private lateinit var nextButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flexibility_exercise_steps)

        stepTitleText = findViewById(R.id.benefits_exercising)
        stepDescriptionText = findViewById(R.id.exerciseInstructionsText)
        stepImageView = findViewById(R.id.streching1)
        backButton = findViewById(R.id.backButton1)
        nextButton = findViewById(R.id.nextButton1)

        // First load
        updateStep()

        // Back button click
        backButton.setOnClickListener {
            if (currentStepIndex == 0) {
                // Back to portal
                finish()
            } else {
                currentStepIndex--
                updateStep()
            }
        }

        // Next button click
        nextButton.setOnClickListener {
            if (currentStepIndex < steps.size - 1) {
                currentStepIndex++
                updateStep()
            } else {
                // Mark as completed
                val sharedPreferences = getSharedPreferences("exercise_prefs", MODE_PRIVATE)
                sharedPreferences.edit()
                    .putBoolean("completed_flexibility", true)
                    .putBoolean("saved_flexibility", false)
                    .apply()

                Toast.makeText(this, "Flexibility exercise marked as complete", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun updateStep() {
        val step = steps[currentStepIndex]
        stepTitleText.text = step.stepTitle
        stepDescriptionText.text = step.stepDescription
        stepImageView.setImageResource(step.imageResId)

        // Update button labels based on position
        backButton.text = if (currentStepIndex == 0) "Back to Portal" else "Back"
        nextButton.text = if (currentStepIndex == steps.size - 1) "Mark as Completed" else "Next"
    }
}
