// package deakin.gopher.guardian

// import android.os.Bundle
// import androidx.activity.enableEdgeToEdge
// import androidx.appcompat.app.AppCompatActivity
// import androidx.core.view.ViewCompat
// import androidx.core.view.WindowInsetsCompat

// class TrainingActivity : AppCompatActivity() {
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

class TrainingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training)
        enableEdgeToEdge()

        // Adjust padding for system bars (status bar, navigation bar)
        findViewById<View>(R.id.main)?.let { mainView ->
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

        // Example: Add logic for button click listeners (if buttons are present in the layout)
        // val exampleButton = findViewById<Button>(R.id.example_button)
        // exampleButton.setOnClickListener {
        //     // Handle button click logic here
        // }
    }

    /**
     * Enables edge-to-edge display for a more immersive experience.
     */
    private fun enableEdgeToEdge() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}

