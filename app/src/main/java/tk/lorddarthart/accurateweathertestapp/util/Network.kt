package tk.lorddarthart.accurateweathertestapp.util

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import org.json.JSONException
import tk.lorddarthart.accurateweathertestapp.model.WeatherModel
import java.io.IOException
import java.io.InputStream

interface Network {
    @Throws(IOException::class, JSONException::class)
    // Getting current forecast
    fun getForecast(mSqLiteDatabase: SQLiteDatabase, city: String, latitude: String, longitude: String): Int

    @Throws(JSONException::class)
    fun readWeather(stringResponse: String, filterName: String): WeatherModel // Getting weather

    @Throws(IOException::class)
    fun inputStreamToString(inputStream: InputStream): String // Decode

    @SuppressLint("SimpleDateFormat")
    fun addWeatherDay(stringResponse: String, i: Int): String // Adding day to forecasts

    @Throws(JSONException::class)
    fun readWeatherArray(array: String, city: String): List<WeatherModel> // Getting forecasts package
}