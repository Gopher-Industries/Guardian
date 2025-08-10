package deakin.gopher.guardian

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.adapter.ExerciseAdapter
import deakin.gopher.guardian.model.ExerciseItem

/**
 * Fragment that handles both the exercise portal and detail views
 */
class PatientExercisePortalFragment : Fragment() {
    private var currentExerciseType: String? = null
    private var currentTabState: TabState = TabState.TO_DO
    private lateinit var adapter: ExerciseAdapter
    private lateinit var recyclerView: RecyclerView

    enum class TabState {
        TO_DO,
        IN_PROGRESS,
        COMPLETED,
    }

    private val allExercises =
        listOf(
            ExerciseItem("flexibility", "Flexibility", R.drawable.stretching),
            ExerciseItem("strength", "Strength", R.drawable.walking2),
            ExerciseItem("breathing", "Breathing", R.drawable.breathing),
            ExerciseItem("balance", "Balance", R.drawable.balance),
            ExerciseItem("cardio", "Cardio", R.drawable.walking2),
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentExerciseType = arguments?.getString("exercise_type")
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
                    "cardio" -> R.layout.activity_exercise_cardio_module
//              "breathing" -> R.layout.activity_exercise_breathing  //PR not yet approved
//              "balance" -> R.layout.activity_exercise_balance   //PR not yet approved
                    else -> R.layout.activity_exercise_flexibility_module
                }
            val view = inflater.inflate(layoutResId, container, false)
            setupExerciseDetailView(view)
            view
        }
    }

    private fun setupPortalView(view: View) {
        recyclerView = view.findViewById(R.id.exerciseRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        adapter =
            ExerciseAdapter(emptyList()) { item ->
                navigateToExercise(item.type)
            }
        recyclerView.adapter = adapter

        view.findViewById<TextView>(R.id.tabToDo)?.setOnClickListener {
            setActiveTab(view, it as TextView)
            currentTabState = TabState.TO_DO
            updateRecyclerView()
        }

        view.findViewById<TextView>(R.id.tabInProgress)?.setOnClickListener {
            setActiveTab(view, it as TextView)
            currentTabState = TabState.IN_PROGRESS
            updateRecyclerView()
        }

        view.findViewById<TextView>(R.id.tabCompleted)?.setOnClickListener {
            setActiveTab(view, it as TextView)
            currentTabState = TabState.COMPLETED
            updateRecyclerView()
        }

        view.findViewById<Button>(R.id.backButton)?.setOnClickListener {
            activity?.finish()
        }

        setActiveTab(view, view.findViewById(R.id.tabToDo))
        updateRecyclerView()
    }

    private fun setupExerciseDetailView(view: View) {
        try {
            view.findViewById<Button>(R.id.saveForLaterButton)?.setOnClickListener {
                val exerciseName =
                    view.findViewById<TextView>(R.id.benefits_exercising)?.text.toString()
                Toast.makeText(
                    requireContext(),
                    "$exerciseName saved for later",
                    Toast.LENGTH_SHORT,
                )
                    .show()

                val sharedPreferences = requireActivity().getSharedPreferences("exercise_prefs", 0)
                sharedPreferences.edit()
                    .putBoolean("saved_$currentExerciseType", true)
                    .putBoolean("completed_$currentExerciseType", false)
                    .apply()

                navigateBackToPortal()
            }

            view.findViewById<Button>(R.id.markCompleteButton)?.setOnClickListener {
                val exerciseName =
                    view.findViewById<TextView>(R.id.benefits_exercising)?.text.toString()
                Toast.makeText(
                    requireContext(),
                    "$exerciseName marked as complete",
                    Toast.LENGTH_SHORT,
                )
                    .show()

                val sharedPreferences = requireActivity().getSharedPreferences("exercise_prefs", 0)
                sharedPreferences.edit()
                    .putBoolean("completed_$currentExerciseType", true)
                    .putBoolean(
                        "saved_$currentExerciseType",
                        false,
                    ) // Ensure it's not marked as completed
                    .apply()

                // Go back to portal
                navigateBackToPortal()
            }

            view.findViewById<Button>(R.id.backButton)?.setOnClickListener {
                // Reset the exercise to "To Do" when back button is pressed
                val sharedPreferences = requireActivity().getSharedPreferences("exercise_prefs", 0)
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

    override fun onResume() {
        super.onResume()
        if (currentExerciseType == null && view != null) {
            // force reset to currentTabState
            when (currentTabState) {
                TabState.TO_DO -> {
                    setActiveTab(requireView(), requireView().findViewById(R.id.tabToDo))
                }

                TabState.IN_PROGRESS -> {
                    setActiveTab(requireView(), requireView().findViewById(R.id.tabInProgress))
                }

                TabState.COMPLETED -> {
                    setActiveTab(requireView(), requireView().findViewById(R.id.tabCompleted))
                }
            }
            updateRecyclerView()
        }
    }

    private fun updateRecyclerView() {
        val prefs = requireActivity().getSharedPreferences("exercise_prefs", 0)
        val filtered =
            allExercises.filter { item ->
                val isCompleted = prefs.getBoolean("completed_${item.type}", false)
                val isSaved = prefs.getBoolean("saved_${item.type}", false)
                when (currentTabState) {
                    TabState.TO_DO -> !isCompleted && !isSaved
                    TabState.IN_PROGRESS -> isSaved && !isCompleted
                    TabState.COMPLETED -> isCompleted
                }
            }
        adapter.updateList(filtered)
    }

    private fun navigateToExercise(type: String) {
        val fragment =
            PatientExercisePortalFragment().apply {
                arguments = Bundle().apply { putString("exercise_type", type) }
            }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateBackToPortal() {
        currentTabState = TabState.TO_DO
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
