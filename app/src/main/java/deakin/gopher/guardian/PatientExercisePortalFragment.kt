package deakin.gopher.guardian

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

/**
 * Fragment that handles both the exercise portal and detail views
 */
class PatientExercisePortalFragment : Fragment() {
    private var currentExerciseType: String? = null
    private var currentTabState: TabState = TabState.TO_DO

    enum class TabState {
        TO_DO,
        IN_PROGRESS,
        COMPLETED,
    }

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
            val layoutResId =
                when (currentExerciseType) {
                    "flexibility" -> R.layout.activity_exercise_flexibility_module
                    "strength" -> R.layout.activity_exercise_strength_module
                    // "breathing" -> R.layout.activity_exercise_breathing_module //PR not yet approved
                    // "balance" -> R.layout.activity_exercise_balance_module //PR not yet approved
                    "cardio" -> R.layout.activity_exercise_cardio_module
                    else -> R.layout.activity_exercise_flexibility_module
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
        try {
            currentTabState = TabState.TO_DO

            view.findViewById<CardView>(R.id.flexibilityCard)?.setOnClickListener {
                navigateToExercise("flexibility")
            }

            view.findViewById<CardView>(R.id.strengthCard)?.setOnClickListener {
                navigateToExercise("strength")
            }

            view.findViewById<CardView>(R.id.breathingCard)?.setOnClickListener {
                navigateToExercise("breathing")
            }

            view.findViewById<CardView>(R.id.balanceCard)?.setOnClickListener {
                navigateToExercise("balance")
            }

            view.findViewById<CardView>(R.id.cardioCard)?.setOnClickListener {
                navigateToExercise("cardio")
            }

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

            view.findViewById<Button>(R.id.backButton)?.setOnClickListener {
                activity?.finish()
            }

            setActiveTab(view, view.findViewById(R.id.tabToDo)!!)
            updateExerciseVisibility(view, TabState.TO_DO)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateExerciseVisibility(
        view: View,
        tabState: TabState,
    ) {
        val sharedPreferences = requireActivity().getSharedPreferences("exercise_prefs", 0)

        val flexibilityCard = view.findViewById<CardView>(R.id.flexibilityCard)
        val strengthCard = view.findViewById<CardView>(R.id.strengthCard)
        val breathingCard = view.findViewById<CardView>(R.id.breathingCard)
        val balanceCard = view.findViewById<CardView>(R.id.balanceCard)
        val cardioCard = view.findViewById<CardView>(R.id.cardioCard)

        fun updateCardVisibility(
            card: CardView?,
            exerciseType: String,
        ) {
            val isCompleted = sharedPreferences.getBoolean("completed_$exerciseType", false)
            val isSaved = sharedPreferences.getBoolean("saved_$exerciseType", false)

            card?.visibility =
                when (tabState) {
                    TabState.TO_DO -> if (!isCompleted && !isSaved) View.VISIBLE else View.GONE
                    TabState.IN_PROGRESS -> if (isSaved && !isCompleted) View.VISIBLE else View.GONE
                    TabState.COMPLETED -> if (isCompleted) View.VISIBLE else View.GONE
                }
        }

        updateCardVisibility(flexibilityCard, "flexibility")
        updateCardVisibility(strengthCard, "strength")
        updateCardVisibility(breathingCard, "breathing")
        updateCardVisibility(balanceCard, "balance")
        updateCardVisibility(cardioCard, "cardio")
    }

    private fun setupExerciseDetailView(view: View) {
        try {
            view.findViewById<Button>(R.id.saveForLaterButton)?.setOnClickListener {
                val exerciseName = view.findViewById<TextView>(R.id.benefits_exercising)?.text.toString() ?: ""

                Toast.makeText(
                    requireContext(),
                    "$exerciseName saved for later",
                    Toast.LENGTH_SHORT,
                ).show()

                val sharedPreferences =
                    requireActivity().getSharedPreferences(
                        "exercise_prefs",
                        0,
                    )
                sharedPreferences.edit()
                    .putBoolean("saved_$currentExerciseType", true)
                    .putBoolean("completed_$currentExerciseType", false) // Ensure it's not marked as completed
                    .apply()

                navigateBackToPortal()
            }

            view.findViewById<Button>(R.id.markCompleteButton)?.setOnClickListener {
                val exerciseName = view.findViewById<TextView>(R.id.benefits_exercising)?.text.toString() ?: ""

                Toast.makeText(
                    requireContext(),
                    "$exerciseName marked as complete",
                    Toast.LENGTH_SHORT,
                ).show()

                val sharedPreferences =
                    requireActivity().getSharedPreferences(
                        "exercise_prefs",
                        0,
                    )
                sharedPreferences.edit()
                    .putBoolean("completed_$currentExerciseType", true)
                    .putBoolean("saved_$currentExerciseType", false)
                    .apply()

                // Go back to portal
                navigateBackToPortal()
            }

            view.findViewById<Button>(R.id.backButton)?.setOnClickListener {
                // Reset the exercise to "To Do" when back button is pressed
                val sharedPreferences =
                    requireActivity().getSharedPreferences(
                        "exercise_prefs",
                        0,
                    )
                sharedPreferences.edit()
                    .putBoolean("saved_$currentExerciseType", false)
                    .putBoolean("completed_$currentExerciseType", false)
                    .apply()

                navigateBackToPortal()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun navigateToExercise(exerciseType: String) {
        val fragment =
            PatientExercisePortalFragment().apply {
                arguments =
                    Bundle().apply {
                        putString("exercise_type", exerciseType)
                    }
            }

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateBackToPortal() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun setActiveTab(
        view: View,
        activeTab: TextView,
    ) {
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
