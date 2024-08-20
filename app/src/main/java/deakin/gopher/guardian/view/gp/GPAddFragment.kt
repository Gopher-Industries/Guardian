package deakin.gopher.guardian.view.gp

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
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

  private lateinit var first_name_input: EditText
  private lateinit var middle_name_input: EditText
  private lateinit var last_name_input: EditText
  private lateinit var clinic_address_input: EditText
  private lateinit var phone_number_input: EditText
  private lateinit var email_input: EditText
  private lateinit var fax_input: EditText

  private var firstName: String? = null
  private var middleName: String? = null
  private var lastName: String? = null
  private var clinicAddress: String? = null
  private var phoneNumber: String? = null
  private var email: String? = null
  private var fax: String? = null

  override fun onCreateView(
          inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
          viewPager2 = requireActivity().findViewById(R.id.dataForViewViewPager)
          val rootView = inflater.inflate(R.layout.fragment_gp_add, container, false)

          val left_button: Button = rootView.findViewById(R.id.gp_add_polygon_left)
  val right_button: Button = rootView.findViewById(R.id.gp_add_polygon_right)
  val next_button: Button = rootView.findViewById(R.id.gp_add_NextButton)
  val prev_button: Button = rootView.findViewById(R.id.gp_add_PrevButton)

  first_name_input = rootView.findViewById(R.id.input_gp_FirstName)
  middle_name_input = rootView.findViewById(R.id.input_gp_MiddleName)
  last_name_input = rootView.findViewById(R.id.input_gp_LastName)
  clinic_address_input = rootView.findViewById(R.id.input_gp_ClinicAddress)
  phone_number_input = rootView.findViewById(R.id.input_gp_Phone)
  email_input = rootView.findViewById(R.id.input_gp_Email)
  fax_input = rootView.findViewById(R.id.input_gp_Fax)

  val step1_button: Button = rootView.findViewById(R.id.step1)
  val step2_button: Button = rootView.findViewById(R.id.step2)
  val step3_button: Button = rootView.findViewById(R.id.step3)
  val step4_button: Button = rootView.findViewById(R.id.step4)
  val step5_button: Button = rootView.findViewById(R.id.step5)

  if (status == 1) {
    step4_button.setBackgroundResource(R.drawable.roundshapeseletebtn)
    step5_button.setBackgroundResource(R.drawable.roundshapebtn)
  }
  if (status == 2) {
    step5_button.setBackgroundResource(R.drawable.roundshapeseletebtn)
    step4_button.setBackgroundResource(R.drawable.roundshapebtn)
  }

  step1_button.setOnClickListener {
    if (dataChecker()) {
      saveGp()
      viewPager2.setCurrentItem(0, true)
    }
  }

  step2_button.setOnClickListener {
    if (dataChecker()) {
      saveGp()
      viewPager2.setCurrentItem(1, true)
    }
  }

  step3_button.setOnClickListener {
    if (dataChecker()) {
      saveGp()
      viewPager2.setCurrentItem(2, true)
    }
  }

  step4_button.setOnClickListener {
    if (dataChecker()) {
      saveGp()
      viewPager2.setCurrentItem(3, true)
    }
  }

  step5_button.setOnClickListener {
    if (dataChecker()) {
      saveGp()
      viewPager2.setCurrentItem(4, true)
    }
  }

  if (status == 2) {
    left_button.setBackgroundResource(R.drawable.polygon_3)
    right_button.setBackgroundResource(R.drawable.polygon_4)
  } else {
    next_button.setText(R.string.next)
  }

  right_button.setOnClickListener {
    if (status == 1) {
      if (dataChecker()) {
        saveGp()
        scrollPage(true)
      }
    }
  }

  left_button.setOnClickListener {
    if (status == 2) {
      if (dataChecker()) {
        saveGp()
        scrollPage(false)
      }
    }
  }

  next_button.setOnClickListener {
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

  prev_button.setOnClickListener {
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
        firstName ?: "",  // Default to empty string if null
        middleName ?: "",
        lastName ?: "",
        phoneNumber ?: "",
        email ?: "",
        fax ?: "",
          null.toString()  // Assuming photo can be null
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
    firstName = first_name_input.text.toString()
    middleName = middle_name_input.text.toString()
    lastName = last_name_input.text.toString()
    clinicAddress = clinic_address_input.text.toString()
    phoneNumber = phone_number_input.text.toString()
    email = email_input.text.toString()
    fax = fax_input.text.toString()

    return when {
      firstName.isNullOrEmpty() -> {
        setErrorAndReturn(first_name_input, "First name is Required.")
        false
      }
      lastName.isNullOrEmpty() -> {
        setErrorAndReturn(last_name_input, "Last name is Required.")
        false
      }
      clinicAddress.isNullOrEmpty() -> {
        setErrorAndReturn(clinic_address_input, "Clinic address is Required.")
        false
      }
      phoneNumber.isNullOrEmpty() -> {
        setErrorAndReturn(phone_number_input, "Phone number is Required.")
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

