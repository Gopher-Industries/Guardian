package deakin.gopher.guardian.view.general

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import deakin.gopher.guardian.R

class CustomHeader : FrameLayout {
    var menuButton: ImageView? = null
    private var headerLogo: ImageView? = null
    private var headerProfileIcon: ImageView? = null
    private var headerTopImage: ImageView? = null
    private var headerTextView: TextView? = null
    private var headerCard: View? = null

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.custom_header, this, true)
        headerLogo = findViewById(R.id.headerGuardiansLogo)
        menuButton = findViewById(R.id.headerMenuIcon)
        headerProfileIcon = findViewById(R.id.headerProfileIcon)
        headerTextView = findViewById(R.id.headerTextView)
        headerCard = findViewById(R.id.headerCard)
        headerTopImage = findViewById(R.id.headerTopImage)
    }

    fun setHeaderTopImage(resource: Int) {
        headerTopImage!!.setImageResource(resource)
    }

    fun setHeaderTopImageVisibility(visible: Int) {
        headerTopImage!!.visibility = visible
    }

    fun setHeaderText(text: String?) {
        headerTextView!!.text = text
    }

    fun setHeaderHeight(height: Int) {
        headerCard!!.layoutParams.height = height
        headerCard!!.requestLayout()
    }
}