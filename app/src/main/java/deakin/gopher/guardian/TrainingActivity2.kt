// package deakin.gopher.guardian

// import android.os.Bundle
// import androidx.activity.enableEdgeToEdge
// import androidx.appcompat.app.AppCompatActivity
// import androidx.core.view.ViewCompat
// import androidx.core.view.WindowInsetsCompat

// class Training2 : AppCompatActivity() {
//     override fun onCreate(savedInstanceState: Bundle?) {
//         super.onCreate(savedInstanceState)
//         enableEdgeToEdge()
//         setContentView(R.layout.activity_training2)
//         ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//             val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//             v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//             insets
//         }
//     }
// }

package deakin.gopher.guardian

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

class TrainingActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training2)

        // Adjust padding for system bars
        findViewById<android.view.View>(R.id.main)?.let { mainView ->
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.updatePadding(
                    left = systemBars.left,
                    top = systemBars.top,
                    right = systemBars.right,
                    bottom = systemBars.bottom
                )
                insets
            }
        }

        // Initialize buttons and set up click listeners
        val module1Button: Button = findViewById(R.id.module1Button)
        val module2Button: Button = findViewById(R.id.module2Button)
        val module3Button: Button = findViewById(R.id.module3Button)

        module1Button.setOnClickListener {
            showToast("Module 1 Started")
        }

        module2Button.setOnClickListener {
            showToast("Module 2 Started")
        }

        module3Button.setOnClickListener {
            showToast("Module 3 Started")
        }
    }

    /**
     * Utility function to show a Toast message.
     * @param message The message to display.
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
