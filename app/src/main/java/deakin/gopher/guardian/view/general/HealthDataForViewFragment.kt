package deakin.gopher.guardian.view.general

import android.view.View
import androidx.fragment.app.Fragment

class HealthDataForViewFragment : Fragment(), NextButtonClickedListener, PrevButtonClickedListener {
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_health_data_for_view, container, false)
        val childFragmentManager: FragmentManager = getChildFragmentManager()
        val fragmentTransaction: FragmentTransaction = childFragmentManager.beginTransaction()
        val fragment1 = HealthDataForViewFragment1()
        fragmentTransaction.add(R.id.child_fragment_container, fragment1)
        fragmentTransaction.addToBackStack("A")
        fragmentTransaction.commit()
        return view
    }

    override fun onNextButtonClicked() {
        val childFragmentManager: FragmentManager = getChildFragmentManager()
        val fragmentTransaction: FragmentTransaction = childFragmentManager.beginTransaction()
        val fragment2 = HealthDataForViewFragment2()
        fragmentTransaction.replace(R.id.child_fragment_container, fragment2)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onPrevButtonClicked() {
        val childFragmentManager: FragmentManager = getChildFragmentManager()
        val fragmentTransaction: FragmentTransaction = childFragmentManager.beginTransaction()
        val fragment1 = HealthDataForViewFragment1()
        fragmentTransaction.replace(R.id.child_fragment_container, fragment1)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}