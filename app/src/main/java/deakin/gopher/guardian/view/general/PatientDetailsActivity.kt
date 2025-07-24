package deakin.gopher.guardian.view.general

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import deakin.gopher.guardian.R
import deakin.gopher.guardian.databinding.ActivityPatientDetailsBinding
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.login.Role
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.model.register.User
import java.util.Locale

class PatientDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityPatientDetailsBinding
    private lateinit var pagerAdapter: PatientDetailsPagerAdapter
    private val currentUser = SessionManager.getCurrentUser()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        if (currentUser.role == Role.Nurse) {
            binding.toolbar.setBackgroundColor(getColor(R.color.TG_blue))
            binding.containerPatientInfo.setBackgroundColor(getColor(R.color.TG_blue))
            binding.tabLayout.setSelectedTabIndicatorColor(getColor(R.color.TG_blue))
            binding.tabLayout.setTabTextColors(getColor(R.color.default_text), getColor(R.color.TG_blue))
        }

        val patient = intent.getSerializableExtra("patient") as Patient
        binding.tvName.text = patient.fullname
        binding.tvAge.text = "Age: ${patient.age}"
        binding.tvDob.text = "Date of Birth: ${patient.dateOfBirth?.substringBefore("T")}"
        binding.tvGender.text = "Gender: ${
            patient.gender.replaceFirstChar {
                if (it.isLowerCase()) {
                    it.titlecase(
                        Locale.getDefault(),
                    )
                } else {
                    it.toString()
                }
            }
        }"

        if (patient.healthConditions.isNotEmpty()) {
            val formattedConditions =
                patient.healthConditions.joinToString(separator = ", ") { condition ->
                    condition.split(" ").joinToString(" ") { word -> // Handle multi-word conditions
                        word.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase() else it.toString()
                        }
                    }
                }

            binding.tvHealthConditions.text = "Health Conditions: $formattedConditions"
        } else {
            binding.tvHealthConditions.text = "Health Conditions: No conditions listed"
        }

        Glide.with(this)
            .load(patient.photoUrl)
            .placeholder(R.drawable.profile)
            .circleCrop()
            .into(binding.imagePatient)

        // Instantiate the adapter
        pagerAdapter = PatientDetailsPagerAdapter(this, patient.id, patient.assignedNurses)
        binding.viewPager.adapter = pagerAdapter
        setupTabs()
    }

    private fun setupTabs() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text =
                when (position) {
                    0 -> "Activity Log" // Title for the first tab
                    1 -> "Assigned Nurses" // Title for the second tab
                    else -> null
                }
        }.attach()
    }

    class PatientDetailsPagerAdapter(
        fragmentActivity: FragmentActivity,
        private val patientId: String,
        private val assignedNurses: List<User>,
    ) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return NUM_TABS
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> PatientActivitiesFragment.newInstance(patientId) // First fragment
                1 -> PatientAssignedNursesFragment.newInstance(assignedNurses) // Second fragment
                else -> throw IllegalStateException("Invalid adapter position $position")
            }
        }
    }

    companion object {
        private val NUM_TABS = if (SessionManager.getCurrentUser().role == Role.Nurse) 1 else 2
    }
}
