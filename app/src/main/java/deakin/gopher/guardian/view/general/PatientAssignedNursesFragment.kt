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

    // Backing field for the nurses list
    private var assignedNurses: List<User> = emptyList()

    private val nurseListAdapter = NurseListAdapter(emptyList()) { nurse ->
        // Handle nurse click if needed
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPatientAssignedNursesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewNurses.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewNurses.adapter = nurseListAdapter

        // If assignedNurses was set before view creation, show them
        updateNurseList(assignedNurses)
    }

    // Public method to update nurses list dynamically
    fun setAssignedNurses(nurses: List<User>) {
        assignedNurses = nurses
        if (isAdded) {
            updateNurseList(nurses)
        }
    }

    private fun updateNurseList(nurses: List<User>) {
        if (nurses.isNotEmpty()) {
            nurseListAdapter.updateData(nurses)
            binding.tvEmptyMessage.visibility = View.GONE
        } else {
            binding.tvEmptyMessage.visibility = View.VISIBLE
        }
    }
}

