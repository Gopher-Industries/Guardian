package deakin.gopher.guardian.view.gp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.GP
import deakin.gopher.guardian.util.DataListener

class GPAddFragment(private var status: Int = 0) : Fragment() {
    private lateinit var viewPager2: ViewPager2
    private var gp: GP? = null
    private var dataListener: DataListener? = null

    private lateinit var firstNameInput: EditText
    private lateinit var middleNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var clinicAddressInput: EditText
    private lateinit var phoneNumberInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var faxInput: EditText

    private var firstName: String? = null
    private var middleName: String? = null
    private var lastName: String? = null
    private var clinicAddress: String? = null
    private var phoneNumber: String? = null
    private var email: String? = null
    private var fax: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        viewPager2 = requireActivity().findViewById(R.id.dataForViewViewPager)
        val rootView = inflater.inflate(R.layout.fragment_gp_add, container, false)

        val leftButton: Button = rootView.findViewById(R.id.gp_add_polygon_left)
        val rightButton: Button = rootView.findViewById(R.id.gp_add_polygon_right)
        val nextButton: Button = rootView.findViewById(R.id.gp_add_NextButton)
        val prevButton: Button = rootView.findViewById(R.id.gp_add_PrevButton)

        firstNameInput = rootView.findViewById(R.id.input_gp_FirstName)
        middleNameInput = rootView.findViewById(R.id.input_gp_MiddleName)
        lastNameInput = rootView.findViewById(R.id.input_gp_LastName)
        clinicAddressInput = rootView.findViewById(R.id.input_gp_ClinicAddress)
        phoneNumberInput = rootView.findViewById(R.id.input_gp_Phone)
        emailInput = rootView.findViewById(R.id.input_gp_Email)
        faxInput = rootView.findViewById(R.id.input_gp_Fax)

        val step1Button: Button = rootView.findViewById(R.id.step1)
        val step2Button: Button = rootView.findViewById(R.id.step2)
        val step3Button: Button = rootView.findViewById(R.id.step3)
        val step4Button: Button = rootView.findViewById(R.id.step4)
        val step5Button: Button = rootView.findViewById(R.id.step5)

        if (status == 1) {
            step4Button.setBackgroundResource(R.drawable.roundshapeseletebtn)
            step5Button.setBackgroundResource(R.drawable.roundshapebtn)
        }
        if (status == 2) {
            step5Button.setBackgroundResource(R.drawable.roundshapeseletebtn)
            step4Button.setBackgroundResource(R.drawable.roundshapebtn)
        }

        step1Button.setOnClickListener {
            if (dataChecker()) {
                saveGp()
                viewPager2.setCurrentItem(0, true)
            }
        }

        step2Button.setOnClickListener {
            if (dataChecker()) {
                saveGp()
                viewPager2.setCurrentItem(1, true)
            }
        }

        step3Button.setOnClickListener {
            if (dataChecker()) {
                saveGp()
                viewPager2.setCurrentItem(2, true)
            }
        }

        step4Button.setOnClickListener {
            if (dataChecker()) {
                saveGp()
                viewPager2.setCurrentItem(3, true)
            }
        }

        step5Button.setOnClickListener {
            if (dataChecker()) {
                saveGp()
                viewPager2.setCurrentItem(4, true)
            }
        }

        if (status == 2) {
            leftButton.setBackgroundResource(R.drawable.polygon_3)
            rightButton.setBackgroundResource(R.drawable.polygon_4)
        } else {
            nextButton.setText(R.string.next)
        }

        rightButton.setOnClickListener {
            if (status == 1) {
                if (dataChecker()) {
                    saveGp()
                    scrollPage(true)
                }
            }
        }

        leftButton.setOnClickListener {
            if (status == 2) {
                if (dataChecker()) {
                    saveGp()
                    scrollPage(false)
                }
            }
        }

        nextButton.setOnClickListener {
            if (dataChecker()) {
                saveGp()
                if (status == 1) {
                    scrollPage(true)
                } else {
                    val builder = AlertDialog.Builder(it.context)
                    builder.setTitle("Saving Changes?")
                    builder.setPositiveButton("YES") { _, _ ->
                        dataListener?.onDataFinished(true)
                    }
                    builder.setNegativeButton("No", null)

                    val dialog = builder.create()
                    dialog.setOnShowListener {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setTextColor(resources.getColor(R.color.colorGreen))
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                            .setTextColor(resources.getColor(R.color.colorRed))
                    }
                    dialog.show()
                }
            }
        }

        prevButton.setOnClickListener {
            if (dataChecker()) {
                saveGp()
                scrollPage(false)
            }
        }

        return rootView
    }

    private fun scrollPage(isNextPage: Boolean) {
        val nextPage = if (isNextPage) viewPager2.currentItem + 1 else viewPager2.currentItem - 1
        viewPager2.setCurrentItem(nextPage, true)
    }

    private fun saveGp() {
        if (gp == null) {
            gp = GP(
                firstName ?: "",
                middleName ?: "",
                lastName ?: "",
                phoneNumber ?: "",
                email ?: "",
                fax ?: "",
                null.toString()
            )
        } else {
            gp?.apply {
                firstName = this@GPAddFragment.firstName ?: ""
                middleName = this@GPAddFragment.middleName ?: ""
                lastName = this@GPAddFragment.lastName ?: ""
                phone = this@GPAddFragment.phoneNumber ?: ""
                email = this@GPAddFragment.email ?: ""
                fax = this@GPAddFragment.fax ?: ""
            }
        }
        if (status == 1) {
            dataListener?.onDataFilled(null, null, null, gp, null)
        } else {
            dataListener?.onDataFilled(null, null, null, null, gp)
        }
    }

    private fun dataChecker(): Boolean {
        firstName = firstNameInput.text.toString()
        middleName = middleNameInput.text.toString()
        lastName = lastNameInput.text.toString()
        clinicAddress = clinicAddressInput.text.toString()
        phoneNumber = phoneNumberInput.text.toString()
        email = emailInput.text.toString()
        fax = faxInput.text.toString()

        return when {
            firstName.isNullOrEmpty() -> {
                setErrorAndReturn(firstNameInput, "First name is Required.")
                false
            }

            lastName.isNullOrEmpty() -> {
                setErrorAndReturn(lastNameInput, "Last name is Required.")
                false
            }

            clinicAddress.isNullOrEmpty() -> {
                setErrorAndReturn(clinicAddressInput, "Clinic address is Required.")
                false
            }

            phoneNumber.isNullOrEmpty() -> {
                setErrorAndReturn(phoneNumberInput, "Phone number is Required.")
                false
            }

            else -> true
        }
    }

    private fun setErrorAndReturn(editText: EditText, message: CharSequence) {
        editText.error = message
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataListener = try {
            context as DataListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement DataListener")
        }
    }
}

