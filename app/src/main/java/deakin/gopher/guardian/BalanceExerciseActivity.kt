package deakin.gopher.guardian

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BalanceExerciseActivity : AppCompatActivity() {

    data class ExerciseStep(
        val stepTitle: String,
        val stepDescription: String,
        val imageResId: Int
    )

    private val steps = listOf(
        ExerciseStep(
            "Step 1: Single Leg Raise",
            "Stand tall with feet hip-width apart.\n" +
                    "Slowly lift your right leg forward without bending your knee.\n" +
                    "Hold for 10–15 seconds, then lower it slowly.\n" +
                    "Switch legs and repeat 2–3 times per leg.",
            R.drawable.single_leg_stand
        ),
        ExerciseStep(
            "Step 2: Side Leg Raise",
            "Stand tall holding onto a chair or wall for support.\n" +
                    "Lift your right leg out to the side, keeping it straight.\n" +
                    "Hold for 10–15 seconds, then lower it slowly.\n" +
                    "Switch legs and repeat 2–3 times per leg.",
            R.drawable.side_leg_raise
        ),
        ExerciseStep(
            "Step 3: Heel-to-Toe Walk",
            "Stand upright and place the heel of your right foot directly in front of the toes of your left foot.\n" +
                    "Walk in a straight line, placing one foot directly in front of the other.\n" +
                    "Take 10–15 steps, turn around, and repeat back.\n" +
                    "Focus on keeping your balance and looking forward.",
            R.drawable.heel_to_toe
        ),
        ExerciseStep(
            "Step 4: Wall Push Balance",
            "Stand facing a wall with feet shoulder-width apart.\n" +
                    "Place your hands on the wall at chest height.\n" +
                    "Lean slightly forward while keeping your back straight and hold for 10–15 seconds.\n" +
                    "Step back, relax, and repeat 2–3 times.",
            R.drawable.wall_push_balance
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
        setContentView(R.layout.activity_balance_exercise_steps)

        stepTitleText = findViewById(R.id.benefits_exercising)
        stepDescriptionText = findViewById(R.id.exerciseInstructionsText)
        stepImageView = findViewById(R.id.streching1)
        backButton = findViewById(R.id.backButton3)
        nextButton = findViewById(R.id.nextButton3)

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
                    .putBoolean("completed_balance", true)
                    .putBoolean("saved_balance", false)
                    .apply()

                Toast.makeText(this, "Balance exercise marked as complete", Toast.LENGTH_SHORT).show()
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
