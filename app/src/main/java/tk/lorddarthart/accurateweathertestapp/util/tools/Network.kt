package tk.lorddarthart.accurateweathertestapp.util.tools

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.json.JSONException
import tk.lorddarthart.accurateweathertestapp.application.model.weather.WeatherModel
import java.io.IOException
import java.io.InputStream

interface Network {
    @Throws(IOException::class, JSONException::class)
    suspend fun getForecast(mSqLiteDatabase: SQLiteDatabase, context: Context, city: String,
                    latitude: String, longitude: String): Int // Getting current forecast

    @Throws(JSONException::class)
    suspend fun readWeather(mStringResponse: String, mFilterName: String,
                            mContext: Context): WeatherModel // Getting weather

    @Throws(IOException::class)
    fun inputStreamToString(inputStream: InputStream): String // Decode

    @SuppressLint("SimpleDateFormat")
    suspend fun addWeatherDay(stringResponse: String, i: Int,
                              context: Context): String // Adding day to forecasts

    @Throws(JSONException::class)
    suspend fun readWeatherArray(array: String, city: String,
                                 context: Context): List<WeatherModel> // Getting forecasts package
}