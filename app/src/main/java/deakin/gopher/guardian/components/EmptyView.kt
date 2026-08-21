package deakin.gopher.guardian.components
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import deakin.gopher.guardian.R

class EmptyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val message: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_empty, this, true)
        visibility = GONE
        message = findViewById(R.id.emptyMessage)
    }

    fun setMessage(text: String) {
        message.text = text
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