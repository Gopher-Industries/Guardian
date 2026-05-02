package deakin.gopher.guardian.components
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import deakin.gopher.guardian.R

class ErrorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val message: TextView
    private val retryButton: Button

    init {
        LayoutInflater.from(context).inflate(R.layout.view_error, this, true)
        visibility = GONE

        message = findViewById(R.id.errorMessage)
        retryButton = findViewById(R.id.retryButton)
    }

    fun setMessage(text: String) {
        message.text = text
    }

    fun setOnRetryClick(listener: () -> Unit) {
        retryButton.setOnClickListener { listener() }
    }

    fun show(messageText: String? = null) {
        if (messageText != null) {
            message.text = messageText
        }
        visibility = VISIBLE
    }

    fun hide() {
        visibility = GONE
    }
}