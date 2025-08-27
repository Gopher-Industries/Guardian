package deakin.gopher.guardian

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

class PatientExercisePortalFragment : Fragment() {
    private var currentExerciseType: String? = null
    private var currentTabState: TabState = TabState.TO_DO
    private var currentStepIndex = 0

    data class ExerciseStep(
        val title: String,
        val description: String,
        val imageResId: Int
    )

    enum class TabState { TO_DO, IN_PROGRESS, COMPLETED }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentExerciseType = it.getString("exercise_type")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return if (currentExerciseType == null) {
            val view = inflater.inflate(R.layout.activity_exercise_main_portal, container, false)
            setupPortalView(view)
            view
        } else {
            val layoutResId = when (currentExerciseType) {
                "flexibility" -> R.layout.activity_flexibility_exercise_steps
                "strength" -> R.layout.activity_strength_exercise_steps
                "cardio" -> R.layout.activity_cardio_exercise_steps
                "breathing" -> R.layout.activity_breathing_exercise_steps
                else -> R.layout.activity_balance_exercise_steps
            }
            val view = inflater.inflate(layoutResId, container, false)
            setupExerciseDetailView(view)
            view
        }
    }

    override fun onResume() {
        super.onResume()
        if (currentExerciseType == null) {
            view?.let { updateExerciseVisibility(it, currentTabState) }
        }
    }

    private fun setupPortalView(view: View) {
        currentTabState = TabState.TO_DO

        view.findViewById<CardView>(R.id.flexibilityCard)?.setOnClickListener { navigateToExercise("flexibility") }
        view.findViewById<CardView>(R.id.strengthCard)?.setOnClickListener { navigateToExercise("strength") }
        view.findViewById<CardView>(R.id.breathingCard)?.setOnClickListener { navigateToExercise("breathing") }
        view.findViewById<CardView>(R.id.balanceCard)?.setOnClickListener { navigateToExercise("balance") }
        view.findViewById<CardView>(R.id.cardioCard)?.setOnClickListener { navigateToExercise("cardio") }

        view.findViewById<TextView>(R.id.tabToDo)?.setOnClickListener {
            setActiveTab(view, it as TextView)
            currentTabState = TabState.TO_DO
            updateExerciseVisibility(view, TabState.TO_DO)
        }

        view.findViewById<TextView>(R.id.tabInProgress)?.setOnClickListener {
            setActiveTab(view, it as TextView)
            currentTabState = TabState.IN_PROGRESS
            updateExerciseVisibility(view, TabState.IN_PROGRESS)
        }

        view.findViewById<TextView>(R.id.tabCompleted)?.setOnClickListener {
            setActiveTab(view, it as TextView)
            currentTabState = TabState.COMPLETED
            updateExerciseVisibility(view, TabState.COMPLETED)
        }

        view.findViewById<Button>(R.id.backButton)?.setOnClickListener { activity?.finish() }

        setActiveTab(view, view.findViewById(R.id.tabToDo)!!)
        updateExerciseVisibility(view, TabState.TO_DO)
    }

    private fun updateExerciseVisibility(view: View, tabState: TabState) {
        val sharedPreferences = requireActivity().getSharedPreferences("exercise_prefs", 0)

        fun updateCardVisibility(card: CardView?, exerciseType: String) {
            val isCompleted = sharedPreferences.getBoolean("completed_$exerciseType", false)
            val isSaved = sharedPreferences.getBoolean("saved_$exerciseType", false)

            card?.visibility =
                when (tabState) {
                    TabState.TO_DO -> if (!isCompleted && !isSaved) View.VISIBLE else View.GONE
                    TabState.IN_PROGRESS -> if (isSaved && !isCompleted) View.VISIBLE else View.GONE
                    TabState.COMPLETED -> if (isCompleted) View.VISIBLE else View.GONE
                }
        }

        updateCardVisibility(view.findViewById(R.id.flexibilityCard), "flexibility")
        updateCardVisibility(view.findViewById(R.id.strengthCard), "strength")
        updateCardVisibility(view.findViewById(R.id.breathingCard), "breathing")
        updateCardVisibility(view.findViewById(R.id.balanceCard), "balance")
        updateCardVisibility(view.findViewById(R.id.cardioCard), "cardio")
    }

    private fun setupExerciseDetailView(view: View) {
        val steps = when (currentExerciseType) {
            "flexibility" -> listOf(
                ExerciseStep(
                    "Step 1",
                    "Neck Stretch:\n" +
                            "• Sit or stand tall with shoulders relaxed.\n" +
                            "• Slowly tilt your head toward your right shoulder (don’t raise the shoulder).\n" +
                            "• Hold for 15–20 seconds, feeling the stretch on the opposite side.\n" +
                            "• Switch sides and repeat 2–3 times.",
                    R.drawable.side_bend
                ),
                ExerciseStep(
                    "Step 2",
                    "Chest Opener:\n" +
                            "• Stand or sit upright.\n" +
                            "• Clasp your hands behind your back and straighten your arms.\n" +
                            "• Gently squeeze shoulder blades together and lift your chest upward.\n" +
                            "• Keep chin parallel to the floor; avoid arching lower back.\n" +
                            "• Hold for 15–20 seconds, repeat twice.",
                    R.drawable.chest_opener
                ),
                ExerciseStep(
                    "Step 3",
                    "Seated Spinal Twist:\n" +
                            "• Sit on the floor with both legs extended forward.\n" +
                            "• Cross your right foot over your left thigh and place it flat on the floor.\n" +
                            "• Place your right hand behind you for support.\n" +
                            "• Place your left elbow on the outside of your right knee.\n" +
                            "• Gently twist your torso to the right, keeping your spine tall.\n" +
                            "• Hold 15–20 seconds, then switch sides.",
                    R.drawable.spinal_twist
                )
            )

            "strength" -> listOf(
                    ExerciseStep(
                        "Step 1",
                        "Chair Squats:\n" +
                                "• Stand in front of a sturdy chair with feet shoulder-width apart.\n" +
                                "• Lower your hips back and down as if sitting, lightly touch the chair.\n" +
                                "• Keep knees over ankles and chest lifted.\n" +
                                "• Press through heels to stand back up.\n" +
                                "• Repeat 8–12 times.",
                        R.drawable.chair_squats
                    ),
                    ExerciseStep(
                        "Step 2",
                        "Wall Push-Ups:\n" +
                                "• Stand facing a wall at arm’s length.\n" +
                                "• Place palms flat on the wall at shoulder height and width.\n" +
                                "• Bend elbows and lean toward the wall, keeping body straight.\n" +
                                "• Push back to the starting position.\n" +
                                "• Repeat 8–12 times.",
                        R.drawable.wall_push_balance
                    ),
                    ExerciseStep(
                        "Step 3",
                        "Seated Leg Raises:\n" +
                                "• Sit tall in a sturdy chair with feet flat on the floor.\n" +
                                "• Straighten one leg and lift it until parallel to the floor.\n" +
                                "• Hold for 2–3 seconds, then lower slowly.\n" +
                                "• Switch legs and repeat 8–12 times each.",
                        R.drawable.leg_raises
                    ),
                    ExerciseStep(
                        "Step 4",
                        "Step-Ups:\n" +
                                "• Stand in front of a sturdy step or low platform.\n" +
                                "• Step up with your right foot, then bring your left foot up.\n" +
                                "• Step down with the right, then the left.\n" +
                                "• Repeat 8–12 times per leg.",
                        R.drawable.step_ups
                    )
                )

            "balance" -> listOf(
                    ExerciseStep(
                        "Step 1",
                        "Single-Leg Stand:\n" +
                                "• Stand tall behind a sturdy chair, hands resting lightly for support.\n" +
                                "• Lift one foot off the ground and balance on the other.\n" +
                                "• Hold for 10–15 seconds, then switch legs.\n" +
                                "• Repeat 2–3 times per side.",
                        R.drawable.single_leg_stand
                    ),
                    ExerciseStep(
                        "Step 2",
                        "Side Leg Raise:\n" +
                                "• Stand tall behind a chair for support.\n" +
                                "• Slowly lift one leg out to the side, keeping it straight and toes facing forward.\n" +
                                "• Hold for 2–3 seconds, then lower slowly.\n" +
                                "• Repeat 8–12 times per side.",
                        R.drawable.side_leg_raise
                    ),
                    ExerciseStep(
                        "Step 3",
                        "Heel-to-Toe Walk:\n" +
                                "• Stand tall and place one foot directly in front of the other, heel touching toe.\n" +
                                "• Walk forward slowly in a straight line for 10–20 steps.\n" +
                                "• Use a wall for support if needed.\n" +
                                "• Repeat 2–3 times.",
                        R.drawable.heel_to_toe
                    ),
                    ExerciseStep(
                        "Step 4",
                        "Wall Push Balance:\n" +
                                "• Stand facing a wall with feet hip-width apart.\n" +
                                "• Place hands lightly against the wall at chest height.\n" +
                                "• Shift weight slightly forward onto toes while keeping heels on the floor.\n" +
                                "• Hold for 10 seconds, then relax.\n" +
                                "• Repeat 5–8 times.",
                        R.drawable.wall_push_balance
                    )
                )

            "cardio" -> listOf(
                ExerciseStep(
                    "Step 1",
                    "March in Place:\n- Stand tall with feet hip-width apart.\n- Lift knees one at a time to waist level.\n- Swing arms naturally with each step.",
                    R.drawable.march_in_place
                ),
                ExerciseStep(
                    "Step 2",
                    "Step Touch:\n- Step your right foot to the side.\n- Bring your left foot to meet it.\n- Add arm movements, swinging or reaching out.\n- Repeat on both sides.",
                    R.drawable.step_touch
                ),
                ExerciseStep(
                    "Step 3",
                    "Seated Jacks:\n- Sit on a sturdy chair.\n- Open legs wide while raising arms overhead.\n- Bring legs together while lowering arms.\n- Repeat steadily like seated jumping jacks.",
                    R.drawable.jumping_jacks
                ),
                ExerciseStep(
                    "Step 4",
                    "Low-Impact Jog:\n- Stand tall and jog gently in place.\n- Land softly on the balls of your feet.\n- Keep a comfortable, light pace.\n- Swing arms naturally to maintain rhythm.",
                    R.drawable.low_jog
                )
            )

            "breathing" -> listOf(
                ExerciseStep(
                    "Step 1",
                    "Alternate-Nostril Breathing (Nadi Shodhana):\n" +
                            "• Sit tall and relax your shoulders.\n" +
                            "• With your right hand, rest index and middle fingers between the eyebrows.\n" +
                            "• Use your thumb to close the right nostril and ring finger to close the left.\n" +
                            "• Exhale completely.\n" +
                            "• Close right nostril; inhale slowly through the left.\n" +
                            "• Close left; open right; exhale through the right.\n" +
                            "• Inhale through the right.\n" +
                            "• Close right; open left; exhale through the left. (1 cycle)\n" +
                            "• Repeat 4–6 cycles, breathing smoothly.",
                    R.drawable.alternate_nose
                ),
                ExerciseStep(
                    "Step 2",
                    "Pursed-Lip Breathing:\n" +
                            "• Inhale gently through your nose for 2–3 seconds.\n" +
                            "• Purse your lips as if blowing out a candle.\n" +
                            "• Exhale slowly and steadily through pursed lips for 4–6 seconds.\n" +
                            "• Keep shoulders relaxed; repeat for 1–2 minutes.",
                    R.drawable.pursed_lip
                ),
                ExerciseStep(
                    "Step 3",
                    "Diaphragmatic (Belly) Breathing:\n" +
                            "• Sit or lie comfortably; one hand on chest, one on belly.\n" +
                            "• Inhale through your nose so your belly rises under your hand.\n" +
                            "• Keep your chest as still as possible.\n" +
                            "• Exhale slowly through pursed lips so your belly falls.\n" +
                            "• Continue for 5–10 breaths.",
                    R.drawable.diapragmatic_breathing
                ),
                ExerciseStep(
                    "Step 4",
                    "Lion’s Breath (Simhasana):\n" +
                            "• Sit or kneel comfortably with hands on knees or thighs.\n" +
                            "• Inhale through your nose.\n" +
                            "• Exhale forcefully through your mouth, opening it wide, tongue out toward your chin, making a loud “ha” sound from the throat.\n" +
                            "• Optionally gaze between the eyebrows or at the tip of the nose.\n" +
                            "• Relax and breathe normally; repeat 3–5 rounds.",
                    R.drawable.lion_breathing
                )
            )

            else -> emptyList()
        }

        if (steps.isEmpty()) return

        val stepTitleText = view.findViewById<TextView>(R.id.benefits_exercising)
        val stepDescriptionText = view.findViewById<TextView>(R.id.exerciseInstructionsText)
        val stepImageView = view.findViewById<ImageView>(R.id.streching1)
        val backButton1 = view.findViewById<Button>(R.id.backButton1)
        val nextButton1 = view.findViewById<Button>(R.id.nextButton1)

        fun updateStep() {
            val step = steps[currentStepIndex]
            stepTitleText.text = step.title
            stepDescriptionText.text = step.description
            stepImageView.setImageResource(step.imageResId)

            backButton1.text = if (currentStepIndex == 0) "Back to Portal" else "Back"
            nextButton1.text = if (currentStepIndex == steps.size - 1) "Mark as Completed" else "Next"
        }

        backButton1.setOnClickListener {
            if (currentStepIndex == 0) {
                navigateBackToPortal()
            } else {
                currentStepIndex--
                updateStep()
            }
        }

        nextButton1.setOnClickListener {
            if (currentStepIndex < steps.size - 1) {
                currentStepIndex++
                updateStep()
            } else {
                val sharedPreferences = requireActivity().getSharedPreferences("exercise_prefs", 0)
                sharedPreferences.edit()
                    .putBoolean("completed_$currentExerciseType", true)
                    .putBoolean("saved_$currentExerciseType", false)
                    .apply()

                Toast.makeText(requireContext(), "$currentExerciseType marked complete", Toast.LENGTH_SHORT).show()
                navigateBackToPortal()
            }
        }

        updateStep()
    }

    private fun navigateToExercise(exerciseType: String) {
        val fragment = PatientExercisePortalFragment().apply {
            arguments = Bundle().apply { putString("exercise_type", exerciseType) }
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateBackToPortal() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun setActiveTab(view: View, activeTab: TextView) {
        val tabToDo = view.findViewById<TextView>(R.id.tabToDo)
        val tabInProgress = view.findViewById<TextView>(R.id.tabInProgress)
        val tabCompleted = view.findViewById<TextView>(R.id.tabCompleted)

        tabToDo?.background = null
        tabInProgress?.background = null
        tabCompleted?.background = null

        tabToDo?.setTextColor(resources.getColor(android.R.color.black))
        tabInProgress?.setTextColor(resources.getColor(android.R.color.black))
        tabCompleted?.setTextColor(resources.getColor(android.R.color.black))

        activeTab.setBackgroundResource(R.drawable.tab_selected)
        activeTab.setTextColor(resources.getColor(android.R.color.white))
    }

    companion object {
        fun newInstance(exerciseType: String? = null): PatientExercisePortalFragment {
            val fragment = PatientExercisePortalFragment()
            if (exerciseType != null) {
                val args = Bundle()
                args.putString("exercise_type", exerciseType)
                fragment.arguments = args
            }
            return fragment
        }
    }
}
