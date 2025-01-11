package deakin.gopher.guardian.view.patient

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R

class MetricDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metric_detail)

        val title = intent.getStringExtra("METRIC_TITLE")
        val imageResId = intent.getIntExtra("METRIC_IMAGE", 0)
        val description = intent.getStringExtra("METRIC_DESCRIPTION")

        findViewById<ImageView>(R.id.metric_detail_image).setImageResource(imageResId)
        findViewById<TextView>(R.id.metric_detail_title).text = title
        findViewById<TextView>(R.id.metric_detail_description).text = description
    }
}
