package deakin.gopher.guardian.view.general

import android.content.Context
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment

class HealthDataForViewFragment1 : Fragment() {
    var mButtonClickedListener: NextButtonClickedListener? = null
    fun onAttach(context: Context?) {
        super.onAttach(context)
        mButtonClickedListener = try {
            getParentFragment() as NextButtonClickedListener?
        } catch (e: ClassCastException) {
            throw ClassCastException(
                getParentFragment().toString() + " must implement NextButtonClickedListener"
            )
        }
    }

    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_health_data_for_view1, container, false)
        val nextButton = view.findViewById<Button>(R.id.nextButtonHealthData)
        nextButton.setOnClickListener { v: View? -> mButtonClickedListener!!.onNextButtonClicked() }
        return view
    }

    interface NextButtonClickedListener {
        fun onNextButtonClicked()
    }
}