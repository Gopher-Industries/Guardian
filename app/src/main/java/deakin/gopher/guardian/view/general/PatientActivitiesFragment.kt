package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import deakin.gopher.guardian.adapter.PatientActivityAdapter
import deakin.gopher.guardian.databinding.FragmentPatientActivitiesBinding
import deakin.gopher.guardian.model.ApiErrorResponse
import deakin.gopher.guardian.model.login.Role
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.api.ApiClient
import deakin.gopher.guardian.view.hide
import deakin.gopher.guardian.view.show
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PatientActivitiesFragment : Fragment() {
    private lateinit var binding: FragmentPatientActivitiesBinding
    private val activitiesAdapter = PatientActivityAdapter(emptyList())

    private val patientId: String by lazy {
        arguments?.getString("patientId") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPatientActivitiesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewActivities.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerViewActivities.adapter = activitiesAdapter

        if (SessionManager.getCurrentUser().role == Role.Nurse) {
            binding.fabAddActivity.show()
            binding.fabAddActivity.setOnClickListener {
                val intent = Intent(this.context, AddPatientLogActivity::class.java)
                intent.putExtra("patientId", patientId)
                startActivity(intent)
            }
        } else {
            binding.fabAddActivity.hide()
        }
    }

    override fun onResume() {
        super.onResume()
        fetchPatientActivities()
    }

    private fun fetchPatientActivities() {
        val token = "Bearer ${SessionManager.getToken()}"
        CoroutineScope(Dispatchers.IO).launch {
            if (activitiesAdapter.itemCount <= 0) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.show()
                }
            }
            val response = ApiClient.apiService.getPatientActivities(token, patientId)
            withContext(Dispatchers.Main) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.hide()
                }
                if (response.isSuccessful) {
                    if (!response.body().isNullOrEmpty()) {
                        activitiesAdapter.updateData(response.body()!!)
                        withContext(Dispatchers.Main) {
                            binding.tvEmptyMessage.visibility = View.GONE
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            binding.tvEmptyMessage.visibility = View.VISIBLE
                        }
                    }
                } else {
                    // Handle error
                    val errorResponse =
                        Gson().fromJson(
                            response.errorBody()?.string(),
                            ApiErrorResponse::class.java,
                        )
                    showMessage(errorResponse.apiError ?: response.message())
                }
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance(patientId: String): PatientActivitiesFragment {
            val fragment = PatientActivitiesFragment()
            val args = Bundle()
            args.putSerializable("patientId", patientId)
            fragment.arguments = args
            return fragment
        }
    }
}
