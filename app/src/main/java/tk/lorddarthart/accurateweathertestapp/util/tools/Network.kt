package tk.lorddarthart.accurateweathertestapp.util.tools

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.json.JSONException
import tk.lorddarthart.accurateweathertestapp.application.model.WeatherModel
import java.io.IOException
import java.io.InputStream

interface Network {
    @Throws(IOException::class, JSONException::class)
    // Getting current forecast
    fun getForecast(mSqLiteDatabase: SQLiteDatabase, context: Context, city: String,
                    latitude: String, longitude: String): Int

    @Throws(JSONException::class)
    fun readWeather(stringResponse: String, filterName: String): WeatherModel // Getting weather

    @Throws(IOException::class)
    fun inputStreamToString(inputStream: InputStream): String // Decode

    @SuppressLint("SimpleDateFormat")
    fun addWeatherDay(stringResponse: String, i: Int): String // Adding day to forecasts

    @Throws(JSONException::class)
    // Getting forecasts package
    fun readWeatherArray(array: String, city: String): List<WeatherModel>
}