package tk.lorddarthart.accurateweathertestapp.view.fragment

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tk.lorddarthart.accurateweathertestapp.R
import tk.lorddarthart.accurateweathertestapp.controller.HorizontalRecyclerViewAdapter
import tk.lorddarthart.accurateweathertestapp.model.WeatherDay
import tk.lorddarthart.accurateweathertestapp.view.base.BaseFragment
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

private const val WEATHER_DATE = "weatherDate"
private const val WEATHER_TEXT = "weatherText"
private const val WEATHER_NOW = "weatherNow"
private const val WEATHER_CITY = "weatherCity"
private const val WEATHER_HUMIDITY = "weatherHumidity"
private const val WEATHER_PRESSURE = "weatherPressure"
private const val WEATHER_D = "weatherD"

class ExtendedFragment: BaseFragment() {
    private lateinit var txtDay: TextView
    private lateinit  var txtMonthYear: TextView
    private lateinit  var txtText: TextView
    private lateinit  var txtTemp: TextView
    private lateinit  var txtTitle: TextView
    private lateinit  var txtHumidity: TextView
    private lateinit  var txtPressure: TextView
    private lateinit  var txt3days: TextView
    private lateinit  var txt7days: TextView
    private lateinit  var finalOutputString: LinkedList<WeatherDay>
    private lateinit  var recyclerView: RecyclerView
    private lateinit  var recyclerViewAdapter: HorizontalRecyclerViewAdapter
    private lateinit  var layoutManager: RecyclerView.LayoutManager
    private var futureForecasts: Int? = null // Days-to-display value
    private lateinit  var sharedPreferences: SharedPreferences
    private lateinit  var editor: SharedPreferences.Editor

    private var mWeatherDate: String? = null
    private var mWeatherText: String? = null
    private var mWeatherNow: Double? = null
    private var mWeatherCity: String? = null
    private var mWeatherHumidity: Double? = null
    private var mWeatherPressure: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mWeatherDate = it.getString(WEATHER_DATE)
            mWeatherText = it.getString(WEATHER_TEXT)
            mWeatherNow = it.getDouble(WEATHER_NOW)
            mWeatherCity = it.getString(WEATHER_CITY)
            mWeatherHumidity = it.getDouble(WEATHER_HUMIDITY)
            mWeatherPressure = it.getDouble(WEATHER_PRESSURE)
        }
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.activity_podrobno, container, false)

        initViews()

        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm")
        val type = object : TypeToken<MutableList<WeatherDay>>() {}.type
        val gson = Gson()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity)
        editor = sharedPreferences.edit()
        if (!sharedPreferences.contains("futureForecast")) {  // Checking for "days" in preferences
            editor.putString("futureForecast", "7days")
            futureForecasts = 7
            editor.apply()
            txt7days.setTextColor(resources.getColor(R.color.colorPrimary))
            txt3days.setTextColor(Color.parseColor("#808080"))
        } else { // If configured then...
            if (sharedPreferences.getString("futureForecast", "7days") == "3days") {
                // ...Highlight "3-days" tab
                futureForecasts = 3
                txt3days.setTextColor(resources.getColor(R.color.colorPrimary))
                txt7days.setTextColor(Color.parseColor("#808080"))
            } else if (sharedPreferences.getString("futureForecast", "7days") == "7days") {
                // ...Highlight "7-days" tab
                futureForecasts = 7
                txt7days.setTextColor(resources.getColor(R.color.colorPrimary))
                txt3days.setTextColor(Color.parseColor("#808080"))
            }
        }
        layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        try {
            txtDay.text = SimpleDateFormat("dd").format(sdf.parse(mWeatherDate))
            txtMonthYear.text = SimpleDateFormat("MMMM, yyyy").format(sdf.parse(mWeatherDate))
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        txtText.text = mWeatherText
        if (mWeatherNow!! > 0.0) {
            txtTemp.text = "+$mWeatherNow"
        } else {
            txtTemp.text = mWeatherNow.toString()
        }
        txtTitle.text = mWeatherCity
        txtHumidity.text = "$mWeatherHumidity%"
        txtPressure.text = "$mWeatherPressure mb"

        finalOutputString = LinkedList()

        txt3days.setOnClickListener { // "3 days" tab click
            if (futureForecasts != 3) { // Checks if click was not performed on itself
                futureForecasts = 3
                editor.putString("futureForecast", "3days")
                editor.commit()
                txt3days.setTextColor(resources.getColor(R.color.colorPrimary))
                txt7days.setTextColor(Color.parseColor("#808080"))
                //this@PodrobnoActivity.recreate()
            }
        }

        txt7days.setOnClickListener { // "7 days" tab click
            if (futureForecasts != 7) { // Checks if click was not performed on itself
                futureForecasts = 7
                editor.putString("futureForecast", "7days")
                editor.commit()
                txt7days.setTextColor(resources.getColor(R.color.colorPrimary))
                txt3days.setTextColor(Color.parseColor("#808080"))
                //this@PodrobnoActivity.recreate()

            }
        }
        futureForecasts?.let {
            for (i in 0 until it)
                finalOutputString.add(
                        (gson.fromJson<Any>(arguments?.getString(
                                "weatherD" + (i + 1)), type) as MutableList<WeatherDay>)[0]
                )
            // Adding data to List
        }
        recyclerViewAdapter = HorizontalRecyclerViewAdapter(mActivity, finalOutputString)
        recyclerView.adapter = recyclerViewAdapter
        return mView
    }

    private fun initViews() {
        try {
            with(mView) {
                txt3days = findViewById(R.id.textView3days)
                txt7days = findViewById(R.id.textView7days)
                recyclerView = findViewById(R.id.horizontalRecyclerView)
                txtDay = findViewById(R.id.txtDay)
                txtMonthYear = findViewById(R.id.txtMonthYear)
                txtText = findViewById(R.id.txtText)
                txtTemp = findViewById(R.id.txtTemp)
                txtTitle = findViewById(R.id.txtTitle)
                txtHumidity = findViewById(R.id.txtHumidity)
                txtPressure = findViewById(R.id.txtPressure)
            }
            Log.d(TAG, "Views was initialized")
        } catch (e: Exception) {
            Log.d(TAG, "mView was not initialized")
        }
    }

    companion object {
        const val TAG = "ExtendedFragment"

        @JvmStatic
        fun newInstance(mWeatherDate: String, mWeatherText: String,
                        mWeatherNow: Double, mWeatherCity: String,
                        mWeatherHumidity: Double, mWeatherPressure: Double,
                        mWeatherDay1: String, mWeatherDay2: String, mWeatherDay3: String,
                        mWeatherDay4: String, mWeatherDay5: String, mWeatherDay6: String,
                        mWeatherDay7: String) =
                ExtendedFragment().apply {
                    val mWeatherDayArray = mutableListOf<String>()
                    mWeatherDayArray.add(0, mWeatherDay1)
                    mWeatherDayArray.add(1, mWeatherDay2)
                    mWeatherDayArray.add(2, mWeatherDay3)
                    mWeatherDayArray.add(3, mWeatherDay4)
                    mWeatherDayArray.add(4, mWeatherDay5)
                    mWeatherDayArray.add(5, mWeatherDay6)
                    mWeatherDayArray.add(6, mWeatherDay7)
                    arguments = Bundle().apply {
                        putString(WEATHER_DATE, mWeatherDate)
                        putString(WEATHER_TEXT, mWeatherText)
                        putDouble(WEATHER_NOW, mWeatherNow)
                        putString(WEATHER_CITY, mWeatherCity)
                        putDouble(WEATHER_HUMIDITY, mWeatherHumidity)
                        putDouble(WEATHER_PRESSURE, mWeatherPressure)
                        for (i in 0 until 7) {
                            putString("$WEATHER_D$i", mWeatherDayArray[i])
                        }
                    }
                }
    }
}