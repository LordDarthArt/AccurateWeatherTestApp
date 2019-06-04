package tk.lorddarthart.accurateweathertestapp.util.tools

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase

import com.google.gson.Gson

import org.json.JSONException
import org.json.JSONObject
import tk.lorddarthart.accurateweathertestapp.R
import tk.lorddarthart.accurateweathertestapp.util.converter.MainConverter
import tk.lorddarthart.accurateweathertestapp.application.model.WeatherModel
import tk.lorddarthart.accurateweathertestapp.application.model.WeatherDayModel

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.LinkedList

class NetworkHelper: Network {

    @Throws(IOException::class, JSONException::class)
    override fun getForecast(mSqLiteDatabase: SQLiteDatabase, context: Context,
                             city: String, latitude: String, longitude: String): Int {
        val url =
                "https://api.weather.yandex.ru/v1/forecast?lat=$latitude&lon=$longitude&lang=ru_RU"

        val obj = URL(url)
        val con = obj.openConnection() as HttpURLConnection
        con.requestMethod = "GET"

        con.setRequestProperty("connection",
                context.resources.getString(R.string.requestPropertyConnection)
        )
        con.setRequestProperty("content-type",
                context.resources.getString(R.string.requestPropertyContentType)

        )
        con.setRequestProperty("X-Yandex-API-Key",
                context.resources.getString(R.string.requestPropertyYandexKey)
        )
        con.connectTimeout = 10000
        con.readTimeout = 10000

        val responseCode = con.responseCode

        if (responseCode != 401 && responseCode != 403) {
            val inputStream = con.inputStream
            val stringResponse = inputStreamToString(inputStream)

            val weathers = readWeatherArray(stringResponse, city)
            println(weathers)
            for (weather in weathers) {
                WeatherDatabaseHelper.addWeather(mSqLiteDatabase, weather.weatherDate,
                        weather.weatherFilterName!!, weather.weatherNow, weather.weatherCity!!,
                        weather.weatherHigh, weather.weatherLow, weather.weatherText!!,
                        weather.weatherDescription!!, weather.mWeatherHumidity, weather.mWeatherPressure,
                        weather.mWeatherSunrise!!, weather.mWeatherSunset!!, weather.mWeatherDay1!!,
                        weather.mWeatherDay2!!, weather.mWeatherDay3!!, weather.mWeatherDay4!!,
                        weather.mWeatherDay5!!, weather.mWeatherDay6!!, weather.mWeatherDay7!!)
            }
        }
        return responseCode
    }

    @Throws(JSONException::class)
    override fun readWeather(stringResponse: String, filterName: String): WeatherModel {
        val mCalendar = Calendar.getInstance()
        val mDay = mCalendar.get(Calendar.DAY_OF_WEEK)
        val mDayOfWeek = MainConverter.getDayOfWeek(mDay)
        val mWeatherDate = ((JSONObject(stringResponse).get("now") as Int).toLong()) * 1000
        val mWeatherNow = (JSONObject(stringResponse)
                .getJSONObject("fact").get("temp").toString()).toDouble()
        val mWeatherDescription = JSONObject(stringResponse)
                .getJSONObject("fact").get("condition") as String
        val mWeatherHumidity = ((JSONObject(stringResponse)
                .getJSONObject("fact").get("humidity") as Int)).toDouble()
        val mWeatherPressure = ((JSONObject(stringResponse)
                .getJSONObject("fact").get("pressure_mm") as Int)).toDouble()
        val mWeatherHight = ((JSONObject(stringResponse)
                .getJSONArray("forecasts").getJSONObject(0).getJSONObject("parts")
                .getJSONObject("day").get("temp_max") as Int)).toDouble()
        val mWeatherLow = ((JSONObject(stringResponse)
                .getJSONArray("forecasts").getJSONObject(0).getJSONObject("parts")
                .getJSONObject("day").get("temp_min") as Int)).toDouble()
        val mWeatherSunrise = JSONObject(stringResponse).getJSONArray("forecasts")
                .getJSONObject(0).get("sunrise") as String
        val mWeatherSunset = JSONObject(stringResponse).getJSONArray("forecasts")
                .getJSONObject(0).get("sunset") as String
        val mWeatherDay1 = addWeatherDay(stringResponse, 0)
        val mWeatherDay2 = addWeatherDay(stringResponse, 1)
        val mWeatherDay3 = addWeatherDay(stringResponse, 2)
        val mWeatherDay4 = addWeatherDay(stringResponse, 3)
        val mWeatherDay5 = addWeatherDay(stringResponse, 4)
        val mWeatherDay6 = addWeatherDay(stringResponse, 5)
        val mWeatherDay7 = addWeatherDay(stringResponse, 6)

        return WeatherModel(mWeatherDate, filterName, mWeatherNow, filterName, mWeatherHight, mWeatherLow,
                mDayOfWeek, mWeatherDescription, mWeatherHumidity, mWeatherPressure, mWeatherSunrise,
                mWeatherSunset, mWeatherDay1, mWeatherDay2, mWeatherDay3, mWeatherDay4, mWeatherDay5,
                mWeatherDay6, mWeatherDay7)
    }

    @Throws(IOException::class)
    override fun inputStreamToString(inputStream: InputStream): String {
        ByteArrayOutputStream().use { result ->
            val buffer = ByteArray(1024)
            val length = inputStream.read(buffer)
            while (length != -1) {
                result.write(buffer, 0, length)
            }
            return result.toString("UTF-8")
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun addWeatherDay(stringResponse: String, i: Int): String {
        try {
            val d = Date(JSONObject(stringResponse).getJSONArray("forecasts").
                    getJSONObject(i).get("date_ts") as Long * 1000)
            val sdf2 = SimpleDateFormat("EEE")
            val dayOfTheWeek = sdf2.format(d)
            val mWeatherDayList = LinkedList<WeatherDayModel>()
            mWeatherDayList.add(WeatherDayModel(dayOfTheWeek,
                    ((JSONObject(stringResponse).getJSONArray("forecasts")
                            .getJSONObject(i).getJSONObject("parts").getJSONObject("day")
                            .get("temp_max") as Int).toString()).toDouble(),
                    ((JSONObject(stringResponse).getJSONArray("forecasts")
                            .getJSONObject(i).getJSONObject("parts").getJSONObject("day")
                            .get("temp_min") as Int).toString()).toDouble(),
                    JSONObject(stringResponse).getJSONArray("forecasts").getJSONObject(i)
                            .getJSONObject("parts").getJSONObject("day")
                            .get("condition") as String))
            val gson = Gson()
            return gson.toJson(mWeatherDayList)
        } catch (e: Exception) {
            println()
        }

        return ""
    }

    @Throws(JSONException::class)
    override fun readWeatherArray(array: String, city: String): List<WeatherModel> {
        val tasks = ArrayList<WeatherModel>()

        tasks.add(readWeather(array, city))
        return tasks
    }
}
