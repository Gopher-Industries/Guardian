package deakin.gopher.guardian.view

import android.os.Bundle
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R
import java.util.Locale

class LanguageSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.language_preference)

        val radioGroup = findViewById<RadioGroup>(R.id.language_radio_group)

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_english -> setLocale("en")
                R.id.radio_spanish -> setLocale("es")
                R.id.radio_hindi -> setLocale("hi")
            }
        }
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)

        resources.updateConfiguration(config, resources.displayMetrics)

        // Save preference if needed
        val editor = getSharedPreferences("settings", MODE_PRIVATE).edit()
        editor.putString("app_lang", languageCode)
        editor.apply()

        // Restart the activity to apply
        recreate()
    }
}
