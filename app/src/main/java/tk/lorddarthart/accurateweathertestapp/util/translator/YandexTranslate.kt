package tk.lorddarthart.accurateweathertestapp.util.translator

import android.content.Context
import android.os.Build
import org.json.JSONException
import org.json.JSONObject
import tk.lorddarthart.accurateweathertestapp.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class YandexTranslate: TranslatorNetwork {

    override fun translateToLocale(context: Context, text: String): String {
        val urlString = translateUrl(context, text)

        val obj = URL(urlString)
        val con = obj.openConnection() as HttpURLConnection
        con.requestMethod = "GET"

        con.setRequestProperty("connection",
                context.resources.getString(R.string.requestPropertyConnection)
        )
        con.setRequestProperty("content-type",
                context.resources.getString(R.string.requestPropertyContentType)

        )
        con.connectTimeout = 5000
        con.readTimeout = 5000

        val responseCode = con.responseCode

        return if (responseCode != 401 && responseCode != 403) {
            val inputStream = con.inputStream
            val stringResponse = inputStreamToString(inputStream)

            readTranslation(stringResponse)
        } else {
            "ERROR"
        }
    }

    override fun translateUrl(context: Context, text: String): String {
        return "https://translate.yandex.net/api/v1.5/tr.json/translate?key=$YANDEX_API_KEY&text=$text&lang=${getCurrentLocale(context).language}"
    }

    override fun getCurrentLocale(context: Context): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0)
        } else {
            context.resources.configuration.locale
        }
    }

    @Throws(IOException::class)
    override fun inputStreamToString(inputStream: InputStream): String {
        val reader = BufferedReader(inputStream.reader())
        val content = StringBuilder()
        try {
            var line = reader.readLine()
            while (line != null) {
                content.append(line)
                line = reader.readLine()
            }
        } finally {
            reader.close()
        }
        return content.toString()
    }

    @Throws(JSONException::class)
    override fun readTranslation(stringResponse: String): String {
            return JSONObject(stringResponse).getJSONArray("text")
                    .get(0) as String
    }

    companion object {
        const val YANDEX_API_KEY = "trnsl.1.1.20190605T100208Z.23cac5092dc349e5.105d609ed126f004c10e3733486a26ae606c1e66"
    }
}