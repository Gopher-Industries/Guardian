package deakin.gopher.guardian.util

import java.text.SimpleDateFormat
import java.util.*

object CalendarUtils {

    fun getWeekDays(centerDate: Date = Date()): List<Date> {
        val days = mutableListOf<Date>()
        val calendar = Calendar.getInstance().apply {
            time = centerDate
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }

        repeat(7) {
            days.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return days
    }

    fun getWeekStart(date: Date): Date {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.time
    }

    fun getWeekEnd(date: Date): Date {
        val calendar = Calendar.getInstance().apply {
            time = getWeekStart(date)
            add(Calendar.DAY_OF_YEAR, 6)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return calendar.time
    }

    fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun isToday(date: Date): Boolean {
        return isSameDay(date, Date())
    }

    fun formatDateHeader(date: Date): String {
        val format = when {
            isToday(date) -> SimpleDateFormat("'Today', MMM dd", Locale.getDefault())
            isTomorrow(date) -> SimpleDateFormat("'Tomorrow', MMM dd", Locale.getDefault())
            isYesterday(date) -> SimpleDateFormat("'Yesterday', MMM dd", Locale.getDefault())
            else -> SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
        }
        return format.format(date)
    }

    fun formatTime(date: Date): String {
        val format = SimpleDateFormat("h:mm a", Locale.getDefault())
        return format.format(date).lowercase()
    }

    private fun isTomorrow(date: Date): Boolean {
        val tomorrow = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
        }.time
        return isSameDay(date, tomorrow)
    }

    private fun isYesterday(date: Date): Boolean {
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }.time
        return isSameDay(date, yesterday)
    }

    fun parseDateString(dateString: String): Date {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("UTC")
            format.parse(dateString) ?: Date()
        } catch (e: Exception) {
            Date()
        }
    }

    fun formatDateForApi(date: Date): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        return format.format(date)
    }
}

// Extension functions
fun Date.isToday(): Boolean = CalendarUtils.isToday(this)
fun Date.formatTime(): String = CalendarUtils.formatTime(this)
fun Date.formatDateHeader(): String = CalendarUtils.formatDateHeader(this)