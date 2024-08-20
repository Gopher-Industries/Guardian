package deakin.gopher.guardian.view.nextofkin

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.NextOfKin
import deakin.gopher.guardian.util.DataListener

class NoKAddFragment : Fragment {
    var viewPager2: ViewPager2? = null
    private var status = 0
    private var nextofKin: NextOfKin? = null
    private var dataListener: DataListener? = null
    private var firstNameInput: EditText? = null
    private var middleNameInput: EditText? = null
    private var lastNameInput: EditText? = null
    private var addressInput: EditText? = null
    private var phoneInput: EditText? = null
    private var emailInput: EditText? = null
    private var firstName: String? = null
    private var middleName: String? = null
    private var lastName: String? = null
    private var address: String? = null
    private var phoneNumber: String? = null
    private var email: String? = null

    constructor()

    constructor(status: Int) {
        this.status = status
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        viewPager2 = requireActivity().findViewById(R.id.dataForViewViewPager)
        val rootView = inflater.inflate(R.layout.fragment_nok_add, container, false)
        val leftButton = rootView.findViewById<Button>(R.id.nok_add_polygon_left)
        val rightButton = rootView.findViewById<Button>(R.id.nok_add_polygon_right)
        val nextButton = rootView.findViewById<Button>(R.id.nok_add_NextButton)
        val prevButton = rootView.findViewById<Button>(R.id.nok_add_PrevButton)
        firstNameInput = rootView.findViewById(R.id.input_nok_FirstName)
        middleNameInput = rootView.findViewById(R.id.input_nok_MiddleName)
        lastNameInput = rootView.findViewById(R.id.input_nok_LastName)
        addressInput = rootView.findViewById(R.id.input_nok_adress)
        phoneInput = rootView.findViewById(R.id.input_nok_PhoneNumber)
        emailInput = rootView.findViewById(R.id.input_nok_EmailAdress)

        val step1_button = rootView.findViewById<Button>(R.id.step1)
        val step2_button = rootView.findViewById<Button>(R.id.step2)
        val step3_button = rootView.findViewById<Button>(R.id.step3)
        val step4_button = rootView.findViewById<Button>(R.id.step4)
        val step5_button = rootView.findViewById<Button>(R.id.step5)

        if (1 == status) {
            step2_button.setBackgroundResource(R.drawable.roundshapeseletebtn)
            step3_button.setBackgroundResource(R.drawable.roundshapebtn)
        }
        if (2 == status) {
            step3_button.setBackgroundResource(R.drawable.roundshapeseletebtn)
            step2_button.setBackgroundResource(R.drawable.roundshapebtn)
        }

        step1_button.setOnClickListener { view: View? ->
            if (dataChecker()) {
                saveNextofKin()
                // int nextPage = viewPager2.getCurrentItem()-1;
                if (viewPager2 != null) {
                    viewPager2!!.setCurrentItem(0, true)
                }

            }
        }

        step2_button.setOnClickListener { view: View? ->
            if (dataChecker()) {
                saveNextofKin()

                if (viewPager2 != null) {
                    viewPager2!!.setCurrentItem(1, true)
                }

            }
        }
        step3_button.setOnClickListener { view: View? ->
            if (dataChecker()) {
                saveNextofKin()
                // int nextPage = viewPager2.getCurrentItem()+1;
                if (viewPager2 != null) {
                    viewPager2!!.setCurrentItem(2, true)
                }

            }
        }
        step4_button.setOnClickListener { view: View? ->
            if (dataChecker()) {
                saveNextofKin()
                // int nextPage = viewPager2.getCurrentItem()+2;
                if (viewPager2 != null) {
                    viewPager2!!.setCurrentItem(3, true)
                }

            }
        }
        step5_button.setOnClickListener { view: View? ->
            if (dataChecker()) {
                saveNextofKin()
                // int nextPage = viewPager2.getCurrentItem()+3;
                if (viewPager2 != null) {
                    viewPager2!!.setCurrentItem(4, true)
                }

            }
        }

        if (2 == status) {
            leftButton.setBackgroundResource(R.drawable.polygon_3)
            rightButton.setBackgroundResource(R.drawable.polygon_4)
        }
        rightButton.setOnClickListener { view: View? ->
            if (1 == status) {
                // for now I keep 2 next of kins and 2 gps waiting to be add
                // but defaultly adding 1 nok and 1gp, after alick right arrow the second one shows up
                // is better
                if (dataChecker()) {
                    saveNextofKin()
                    scrollPage(true)
                }
            }
        }

        leftButton.setOnClickListener { view: View? ->
            if (2 == status) {
                if (dataChecker()) {
                    saveNextofKin()
                    scrollPage(false)
                }
            }
        }

        nextButton.setOnClickListener { view: View? ->
            if (dataChecker()) {
                saveNextofKin()
                scrollPage(true)
            }
        }

        prevButton.setOnClickListener { view: View? ->
            if (dataChecker()) {
                saveNextofKin()
                scrollPage(false)
            }
        }

        return rootView
    }

    private fun scrollPage(isNextPage: Boolean) {
        val nextPage = if (isNextPage) {
            viewPager2!!.currentItem + 1
        } else {
            viewPager2!!.currentItem - 1
        }
        viewPager2!!.setCurrentItem(nextPage, true)
    }

    private fun saveNextofKin() {
        if (null == nextofKin) {
            nextofKin = NextOfKin(
                firstName!!,
                middleName!!,
                lastName!!,
                address!!,
                phoneNumber!!,
                email!!,
                null
            )
        } else {
            nextofKin!!.setFirstName(firstName!!)
            nextofKin!!.setMiddleName(middleName!!)
            nextofKin!!.setLastName(lastName!!)
            nextofKin!!.setHomeAddress(address!!)
            nextofKin!!.setMobilePhone(phoneNumber!!)
            nextofKin!!.setEmailAddress(email!!)
        }
        if (1 == status) {
            dataListener!!.onDataFilled(null, nextofKin, null, null, null)
        } else {
            dataListener!!.onDataFilled(null, null, nextofKin, null, null)
        }
    }

    private fun dataChecker(): Boolean {
        firstName = firstNameInput!!.text.toString()
        middleName = middleNameInput!!.text.toString()
        lastName = lastNameInput!!.text.toString()
        address = addressInput!!.text.toString()
        phoneNumber = phoneInput!!.text.toString()
        email = emailInput!!.text.toString()

        if (TextUtils.isEmpty(firstName)) {
            setErrorAndReturn(firstNameInput, "First name is Required.")
            return false
        }
        if (TextUtils.isEmpty(lastName)) {
            setErrorAndReturn(lastNameInput, "Last name is Required.")
            return false
        }
        if (TextUtils.isEmpty(address)) {
            setErrorAndReturn(addressInput, "Address is Required.")
            return false
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            setErrorAndReturn(phoneInput, "Phone number is Required.")
            return false
        }

        return true
    }

    private fun setErrorAndReturn(editText: EditText?, s: CharSequence) {
        editText!!.error = s
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            dataListener = context as DataListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement DataListener")
        }
    }
}
