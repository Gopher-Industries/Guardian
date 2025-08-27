package deakin.gopher.guardian

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class StrengthExerciseActivity : AppCompatActivity() {

    data class ExerciseStep(
        val stepTitle: String,
        val stepDescription: String,
        val imageResId: Int
    )

    private val steps = listOf(
        ExerciseStep(
            "Chair Squats",
            "1. Stand in front of a sturdy chair with feet shoulder-width apart.\n" +
                    "2. Lower yourself down as if sitting, keeping knees behind toes.\n" +
                    "3. Lightly touch the chair with your hips (don’t sit fully).\n" +
                    "4. Push through your heels to return to standing.",
            R.drawable.chair_squats
        ),
        ExerciseStep(
            "Wall Push-Ups",
            "1. Stand facing a wall, feet shoulder-width apart.\n" +
                    "2. Place your hands flat on the wall at chest height.\n" +
                    "3. Bend your elbows and lean towards the wall.\n" +
                    "4. Push back to the starting position.",
            R.drawable.wall_push_balance
        ),
        ExerciseStep(
            "Leg Raises",
            "1. Lie flat on your back with legs extended.\n" +
                    "2. Place hands under your hips for support.\n" +
                    "3. Lift both legs until they are about 90 degrees from the floor.\n" +
                    "4. Slowly lower them back down without touching the ground.",
            R.drawable.leg_raises
        ),
        ExerciseStep(
            "Step-Ups",
            "1. Stand in front of a sturdy step or platform.\n" +
                    "2. Place your right foot firmly on the step.\n" +
                    "3. Push through your right heel to bring your left foot up.\n" +
                    "4. Step back down with your left foot, then right, and repeat.",
            R.drawable.step_ups
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
        setContentView(R.layout.activity_strength_exercise_steps)

        stepTitleText = findViewById(R.id.benefits_exercising)
        stepDescriptionText = findViewById(R.id.exerciseInstructionsText1)
        stepImageView = findViewById(R.id.streching1)
        backButton = findViewById(R.id.backButton2)
        nextButton = findViewById(R.id.nextButton2)

        // First load
        updateStep()

        // Back button click
        backButton.setOnClickListener {
            if (currentStepIndex == 0) {
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
                val sharedPreferences = getSharedPreferences("exercise_prefs", MODE_PRIVATE)
                sharedPreferences.edit()
                    .putBoolean("completed_strength", true)
                    .putBoolean("saved_strength", false)
                    .apply()

                Toast.makeText(this, "Strength exercise marked as complete", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun updateStep() {
        val step = steps[currentStepIndex]
        stepTitleText.text = step.stepTitle
        stepDescriptionText.text = step.stepDescription
        stepImageView.setImageResource(step.imageResId)

        backButton.text = if (currentStepIndex == 0) "Back to Portal" else "Back"
        nextButton.text = if (currentStepIndex == steps.size - 1) "Mark as Completed" else "Next"
    }
}
