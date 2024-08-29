package deakin.gopher.guardian.view.general

import android.content.Context
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment

class HealthDataForViewFragment2 : Fragment() {
    var mButtonClickedListener: PrevButtonClickedListener? = null
    fun onAttach(context: Context?) {
        super.onAttach(context)
        mButtonClickedListener = try {
            getParentFragment() as PrevButtonClickedListener?
        } catch (e: ClassCastException) {
            throw ClassCastException(
                getParentFragment().toString() + " must implement PrevButtonClickedListener"
            )
        }
    }

    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_health_data_for_view2, container, false)
        val prevButton = view.findViewById<Button>(R.id.prevButtonHealthData)
        prevButton.setOnClickListener { v: View? -> mButtonClickedListener!!.onPrevButtonClicked() }
        return view
    }

    interface PrevButtonClickedListener {
        fun onPrevButtonClicked()
    }
}