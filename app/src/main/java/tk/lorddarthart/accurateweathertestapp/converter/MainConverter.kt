package tk.lorddarthart.accurateweathertestapp.converter

import java.text.SimpleDateFormat
import java.util.*

object MainConverter {

    fun getDayOfWeek(day: Int): String {
        val sdf = SimpleDateFormat("EEEE", Locale("ru", "RU"))
        when (day) {
            Calendar.MONDAY -> {
                return sdf.format(345600001)
            }

            Calendar.TUESDAY -> {
                return sdf.format(432000001)
            }

            Calendar.WEDNESDAY -> {
                return sdf.format(518400001)
            }

            Calendar.THURSDAY -> {
                return sdf.format(1)
            }

            Calendar.FRIDAY -> {
                return sdf.format(86400001)
            }

            Calendar.SATURDAY -> {
                return sdf.format(172800001)
            }

            Calendar.SUNDAY -> {
                return sdf.format(259200001)
            }
        }
        return day.toString()
    }
}