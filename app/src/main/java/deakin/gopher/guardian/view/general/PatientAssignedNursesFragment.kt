package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import deakin.gopher.guardian.adapter.NurseListAdapter
import deakin.gopher.guardian.databinding.FragmentPatientAssignedNursesBinding
import deakin.gopher.guardian.model.register.User

class PatientAssignedNursesFragment : Fragment() {
    private lateinit var binding: FragmentPatientAssignedNursesBinding
    private val nurseListAdapter =
        NurseListAdapter(
            emptyList(),
            onNurseClick = { nurse ->
            },
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPatientAssignedNursesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewNurses.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerViewNurses.adapter = nurseListAdapter

        val assignedNurses = arguments?.getSerializable("nurses") as List<User>

        if (assignedNurses.isNotEmpty()) {
            nurseListAdapter.updateData(assignedNurses)
            binding.tvEmptyMessage.visibility = View.GONE
        } else {
            binding.tvEmptyMessage.visibility = View.VISIBLE
        }
    }

    companion object {
        fun newInstance(nurses: List<User>): PatientAssignedNursesFragment {
            val fragment = PatientAssignedNursesFragment()
            val args = Bundle()
            args.putSerializable("nurses", ArrayList(nurses))
            fragment.arguments = args
            return fragment
        }
    }
}
