package deakin.gopher.guardian.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object DateUtils {
    val patterns =
        listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
        )

    fun getRelativeDayWithTime(isoDateString: String): String {
        val parsedDate: Date =
            patterns.firstNotNullOfOrNull { pattern ->
                try {
                    SimpleDateFormat(pattern, Locale.getDefault()).apply {
                        timeZone = TimeZone.getDefault()
                    }.parse(isoDateString)
                } catch (e: Exception) {
                    null
                }
            } ?: return "Invalid date"

        val now = Calendar.getInstance()
        val inputCal = Calendar.getInstance().apply { time = parsedDate }

        val nowStart = now.clone() as Calendar
        nowStart.set(Calendar.HOUR_OF_DAY, 0)
        nowStart.set(Calendar.MINUTE, 0)
        nowStart.set(Calendar.SECOND, 0)
        nowStart.set(Calendar.MILLISECOND, 0)

        val inputStart = inputCal.clone() as Calendar
        inputStart.set(Calendar.HOUR_OF_DAY, 0)
        inputStart.set(Calendar.MINUTE, 0)
        inputStart.set(Calendar.SECOND, 0)
        inputStart.set(Calendar.MILLISECOND, 0)

        val diffMillis = nowStart.timeInMillis - inputStart.timeInMillis
        val daysDiff = TimeUnit.MILLISECONDS.toDays(diffMillis).toInt()

        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val timePart = timeFormat.format(parsedDate)

        val label =
            when (daysDiff) {
                0 -> "Today"
                1 -> "Yesterday"
                in 2..6 -> {
                    val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                    dayFormat.format(parsedDate)
                }

                in 7..364 -> {
                    val recentFormat = SimpleDateFormat("d MMM", Locale.getDefault())
                    recentFormat.format(parsedDate)
                }

                else -> {
                    val yearFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
                    yearFormat.format(parsedDate)
                }
            }

        return "$label, $timePart"
    }
}
