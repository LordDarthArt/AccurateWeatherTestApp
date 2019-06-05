package tk.lorddarthart.accurateweathertestapp.util.translator

import android.content.Context
import java.io.InputStream
import java.util.*

interface TranslatorNetwork {
    fun translateToLocale(context: Context, text: String): String
    fun translateUrl(context: Context, text: String): String
    fun getCurrentLocale(context: Context): Locale
    fun inputStreamToString(inputStream: InputStream): String
    fun readTranslation(stringResponse: String): String
}