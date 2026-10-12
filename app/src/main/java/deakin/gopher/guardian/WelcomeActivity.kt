package deakin.gopher.guardian

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import deakin.gopher.guardian.components.EmptyView
import deakin.gopher.guardian.components.ErrorView
import deakin.gopher.guardian.components.LoadingView
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {

    private lateinit var loadingView: LoadingView
    private lateinit var emptyView: EmptyView
    private lateinit var errorView: ErrorView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome) // This links to your activity_welcome.xml

        loadingView = findViewById(R.id.loadingView)
        emptyView = findViewById(R.id.emptyView)
        errorView = findViewById(R.id.errorView)

        val btnShowLoading = findViewById<Button>(R.id.btnShowLoading)
        val btnShowEmpty = findViewById<Button>(R.id.btnShowEmpty)
        val btnShowError = findViewById<Button>(R.id.btnShowError)
        val btnHideAll = findViewById<Button>(R.id.btnHideAll)

        btnShowLoading.setOnClickListener {
            hideAllStateViews()
            loadingView.show()
        }

        btnShowEmpty.setOnClickListener {
            hideAllStateViews()
            emptyView.show("No records found")
        }

        btnShowError.setOnClickListener {
            hideAllStateViews()
            errorView.show("Failed to load data")
        }

        btnHideAll.setOnClickListener {
            hideAllStateViews()
        }

        errorView.setOnRetryClick {
            hideAllStateViews()
            loadingView.show()

            loadingView.postDelayed({
                loadingView.hide()
                emptyView.show("Retry completed, but no data available")
            }, 2000)
        }
    }

    private fun hideAllStateViews() {
        loadingView.hide()
        emptyView.hide()
        errorView.hide()
    }
}