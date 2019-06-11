package tk.lorddarthart.accurateweathertestapp.util.tools

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.json.JSONException
import org.json.JSONObject
import tk.lorddarthart.accurateweathertestapp.R
import tk.lorddarthart.accurateweathertestapp.application.model.weather.WeatherDayModel
import tk.lorddarthart.accurateweathertestapp.application.model.weather.WeatherModel
import tk.lorddarthart.accurateweathertestapp.util.converter.MainConverter
import tk.lorddarthart.accurateweathertestapp.util.translator.YandexTranslate
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class NetworkHelper : Network {

    @Throws(IOException::class, JSONException::class)
    override suspend fun getForecast(mSqLiteDatabase: SQLiteDatabase, context: Context,
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

            val weathers = readWeatherArray(stringResponse, city, context)
            println(weathers)
            for (weather in weathers) {
                WeatherDatabaseHelper.addWeather(mSqLiteDatabase, weather.weatherDate,
                        weather.weatherFilterName!!, weather.weatherNow, weather.weatherCity!!,
                        weather.weatherHigh, weather.weatherLow, weather.weatherText!!,
                        weather.weatherDescription!!, weather.mWeatherHumidity,
                        weather.mWeatherPressure,
                        weather.mWeatherSunrise!!, weather.mWeatherSunset!!, weather.mWeatherDay1!!,
                        weather.mWeatherDay2!!, weather.mWeatherDay3!!, weather.mWeatherDay4!!,
                        weather.mWeatherDay5!!, weather.mWeatherDay6!!, weather.mWeatherDay7!!)
            }
        }
        return responseCode
    }

    @Throws(JSONException::class)
    override suspend fun readWeather(
            mStringResponse: String,
            mFilterName: String,
            mContext: Context
    ): WeatherModel {
        val mCalendar = Calendar.getInstance()
        val mDay = mCalendar.get(Calendar.DAY_OF_WEEK)
        val mDayOfWeek = MainConverter.getDayOfWeek(mContext, mDay)
        val mWeatherDate = ((JSONObject(mStringResponse).get("now") as Int).toLong()) * 1000
        val mWeatherNow = (JSONObject(mStringResponse)
                .getJSONObject("fact").get("temp").toString()).toDouble()
        val mWeatherDescription = JSONObject(mStringResponse)
                .getJSONObject("fact").get("condition") as String
        val mWeatherHumidity = ((JSONObject(mStringResponse)
                .getJSONObject("fact").get("humidity") as Int)).toDouble()
        val mWeatherPressure = ((JSONObject(mStringResponse)
                .getJSONObject("fact").get("pressure_mm") as Int)).toDouble()
        val mWeatherHight = ((JSONObject(mStringResponse)
                .getJSONArray("forecasts").getJSONObject(0).getJSONObject("parts")
                .getJSONObject("day").get("temp_max") as Int)).toDouble()
        val mWeatherLow = ((JSONObject(mStringResponse)
                .getJSONArray("forecasts").getJSONObject(0).getJSONObject("parts")
                .getJSONObject("day").get("temp_min") as Int)).toDouble()
        val mWeatherSunrise = JSONObject(mStringResponse).getJSONArray("forecasts")
                .getJSONObject(0).get("sunrise") as String
        val mWeatherSunset = JSONObject(mStringResponse).getJSONArray("forecasts")
                .getJSONObject(0).get("sunset") as String
        val mWeatherDay1 = addWeatherDay(mStringResponse, 0, mContext)
        val mWeatherDay2 = addWeatherDay(mStringResponse, 1, mContext)
        val mWeatherDay3 = addWeatherDay(mStringResponse, 2, mContext)
        val mWeatherDay4 = addWeatherDay(mStringResponse, 3, mContext)
        val mWeatherDay5 = addWeatherDay(mStringResponse, 4, mContext)
        val mWeatherDay6 = addWeatherDay(mStringResponse, 5, mContext)
        val mWeatherDay7 = addWeatherDay(mStringResponse, 6, mContext)

        return WeatherModel(mWeatherDate, mFilterName, mWeatherNow, mFilterName, mWeatherHight,
                mWeatherLow, mDayOfWeek, mWeatherDescription, mWeatherHumidity, mWeatherPressure,
                mWeatherSunrise, mWeatherSunset, mWeatherDay1, mWeatherDay2, mWeatherDay3,
                mWeatherDay4, mWeatherDay5, mWeatherDay6, mWeatherDay7)
    }

    @Throws(IOException::class)
    override fun inputStreamToString(inputStream: InputStream): String {
        val mReader = BufferedReader(inputStream.reader())
        val mContent = StringBuilder()
        mReader.use { ireader ->
            var line = ireader.readLine()
            while (line != null) {
                mContent.append(line)
                line = ireader.readLine()
            }
        }
        return mContent.toString()
    }

    @SuppressLint("SimpleDateFormat")
    override suspend fun addWeatherDay(
            stringResponse: String,
            i: Int,
            context: Context
    ): String {
        try {
            val d = Date((JSONObject(stringResponse).getJSONArray("forecasts")
                    .getJSONObject(i).get("date_ts") as Int).toLong() * 1000)
            val sdf2 = SimpleDateFormat("EEE")
            val dayOfTheWeek = sdf2.format(d)
            val mWeatherDayList = LinkedList<WeatherDayModel>()
            val translator = YandexTranslate()
            mWeatherDayList.add(
                    WeatherDayModel(
                            dayOfTheWeek,
                            (
                                    (JSONObject(stringResponse).getJSONArray("forecasts")
                                            .getJSONObject(i).getJSONObject("parts")
                                            .getJSONObject("day")
                                            .get("temp_max") as Int).toString())
                                    .toDouble(),
                            (
                                    (JSONObject(stringResponse).getJSONArray("forecasts")
                                            .getJSONObject(i).getJSONObject("parts")
                                            .getJSONObject("day")
                                            .get("temp_min") as Int).toString())
                                    .toDouble(),
                            runBlocking(Dispatchers.IO) {
                                translator
                                        .translateToLocale(
                                                context,
                                                JSONObject(stringResponse)
                                                        .getJSONArray("forecasts")
                                                        .getJSONObject(i)
                                                        .getJSONObject("parts")
                                                        .getJSONObject("day")
                                                        .get("condition") as String
                                        )
                            }
                    )
            )
            val gson = Gson()
            return gson.toJson(mWeatherDayList)
        } catch (e: Exception) {
            println()
        }

        return ""
    }

    @Throws(JSONException::class)
    override suspend fun readWeatherArray(
            array: String,
            city: String,
            context: Context
    ): List<WeatherModel> {
        val tasks = ArrayList<WeatherModel>()

        tasks.add(readWeather(array, city, context))
        return tasks
    }
}
