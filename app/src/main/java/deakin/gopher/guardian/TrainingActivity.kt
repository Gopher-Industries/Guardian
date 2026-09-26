package deakin.gopher.guardian

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TrainingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_training2)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->

            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        val btnBack = findViewById<TextView>(R.id.btnBack)

        val btnModule1 = findViewById<TextView>(R.id.btnModule1)
        val btnModule2 = findViewById<TextView>(R.id.btnModule2)
        val btnModule3 = findViewById<TextView>(R.id.btnModule3)

        val cardModule1 = findViewById<android.view.View>(R.id.cardModule1)
        val cardModule2 = findViewById<android.view.View>(R.id.cardModule2)
        val cardModule3 = findViewById<android.view.View>(R.id.cardModule3)

        btnBack.setOnClickListener {
            finish()
        }

        btnModule1.setOnClickListener {
            openModule1()
        }

        cardModule1.setOnClickListener {
            openModule1()
        }

        btnModule2.setOnClickListener {
            openModule2()
        }

        cardModule2.setOnClickListener {
            openModule2()
        }

        btnModule3.setOnClickListener {
            openModule3()
        }

        cardModule3.setOnClickListener {
            openModule3()
        }
    }

    private fun openModule1() {
        val intent = Intent(
            this,
            Module1CoursesActivity::class.java
        )

        startActivity(intent)
    }

    private fun openModule2() {
        val intent = Intent(
            this,
            Module2CoursesActivity::class.java
        )

        startActivity(intent)
    }

    private fun openModule3() {
        val intent = Intent(
            this,
            Module3CoursesActivity::class.java
        )

        startActivity(intent)
    }
}