package tk.lorddarthart.accurateweathertestapp.util.converter

import android.content.Context
import tk.lorddarthart.accurateweathertestapp.util.translator.YandexTranslate
import java.text.SimpleDateFormat
import java.util.*

object MainConverter {
    fun getDayOfWeek(mContext: Context, day: Int): String {
        val sdf = SimpleDateFormat("EEEE", Locale("ru", "RU"))
        when (day) {
            Calendar.MONDAY -> {
                return YandexTranslate().translateToLocale(mContext, sdf.format(345600001))
            }

            Calendar.TUESDAY -> {
                return YandexTranslate().translateToLocale(mContext, sdf.format(432000001))
            }

            Calendar.WEDNESDAY -> {
                return YandexTranslate().translateToLocale(mContext, sdf.format(518400001))
            }

            Calendar.THURSDAY -> {
                return YandexTranslate().translateToLocale(mContext, sdf.format(1))
            }

            Calendar.FRIDAY -> {
                return YandexTranslate().translateToLocale(mContext, sdf.format(86400001))
            }

            Calendar.SATURDAY -> {
                return YandexTranslate().translateToLocale(mContext, sdf.format(172800001))
            }

            Calendar.SUNDAY -> {
                return YandexTranslate().translateToLocale(mContext, sdf.format(259200001))
            }
        }
        return YandexTranslate().translateToLocale(mContext, day.toString())
    }
}