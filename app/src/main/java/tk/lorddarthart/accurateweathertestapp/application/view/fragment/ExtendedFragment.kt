package tk.lorddarthart.accurateweathertestapp.application.view.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.design.longSnackbar
import tk.lorddarthart.accurateweathertestapp.R
import tk.lorddarthart.accurateweathertestapp.R.*
import tk.lorddarthart.accurateweathertestapp.application.model.weather.WeatherDayModel
import tk.lorddarthart.accurateweathertestapp.application.view.base.BaseFragment
import tk.lorddarthart.accurateweathertestapp.util.IOnBackPressed
import tk.lorddarthart.accurateweathertestapp.util.adapter.FutureForecastsAdapter
import tk.lorddarthart.accurateweathertestapp.util.constants.SharedPreferencesKeys.SHARED_PREFERENCES_KEY_FUTURE_FORECAST
import tk.lorddarthart.accurateweathertestapp.util.constants.SharedPreferencesValues.SHARED_PREFERENCES_3DAYS
import tk.lorddarthart.accurateweathertestapp.util.constants.SharedPreferencesValues.SHARED_PREFERENCES_7DAYS
import tk.lorddarthart.accurateweathertestapp.util.constants.SimpleDateFormatPatterns.TXT_DAY_PATTERN
import tk.lorddarthart.accurateweathertestapp.util.constants.SimpleDateFormatPatterns.TXT_FULL_SDF_PATTERN
import tk.lorddarthart.accurateweathertestapp.util.constants.SimpleDateFormatPatterns.TXT_MONTH_YEAR_PATTERN
import tk.lorddarthart.accurateweathertestapp.util.constants.SqlColumns.WEATHER_CITY
import tk.lorddarthart.accurateweathertestapp.util.constants.SqlColumns.WEATHER_D
import tk.lorddarthart.accurateweathertestapp.util.constants.SqlColumns.WEATHER_DATE
import tk.lorddarthart.accurateweathertestapp.util.constants.SqlColumns.WEATHER_DESCRIPTION
import tk.lorddarthart.accurateweathertestapp.util.constants.SqlColumns.WEATHER_HIGH
import tk.lorddarthart.accurateweathertestapp.util.constants.SqlColumns.WEATHER_HUMIDITY
import tk.lorddarthart.accurateweathertestapp.util.constants.SqlColumns.WEATHER_LOW
import tk.lorddarthart.accurateweathertestapp.util.constants.SqlColumns.WEATHER_NOW
import tk.lorddarthart.accurateweathertestapp.util.constants.SqlColumns.WEATHER_PRESSURE
import tk.lorddarthart.accurateweathertestapp.util.constants.SqlColumns.WEATHER_SUNRISE
import tk.lorddarthart.accurateweathertestapp.util.constants.SqlColumns.WEATHER_SUNSET
import tk.lorddarthart.accurateweathertestapp.util.constants.SqlColumns.WEATHER_TEXT
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat

class ExtendedFragment : BaseFragment(), IOnBackPressed {
    // Tools
    private lateinit var futureForecastsList: MutableList<WeatherDayModel>
    private lateinit var recyclerViewAdapter: FutureForecastsAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var futureForecasts: Int? = null // Days-to-display value
    private lateinit var sdf: SimpleDateFormat
    private lateinit var type: Type
    private lateinit var gson: Gson
    private var btnClickedId = 0

    // Model
    private var mWeatherDate: String? = null
    private var mWeatherText: String? = null
    private var mWeatherNow: Double? = null
    private var mWeatherCity: String? = null
    private var mWeatherHigh: Double? = null
    private var mWeatherLow: Double? = null
    private var mWeatherSunrise: String? = null
    private var mWeatherSunset: String? = null
    private var mWeatherDescription: String? = null
    private var mWeatherHumidity: Double? = null
    private var mWeatherPressure: Double? = null
    private var mWeatherDay1: String? = null
    private var mWeatherDay2: String? = null
    private var mWeatherDay3: String? = null
    private var mWeatherDay4: String? = null
    private var mWeatherDay5: String? = null
    private var mWeatherDay6: String? = null
    private var mWeatherDay7: String? = null
    private var mWeatherDayArray = mutableListOf<String>()

    // Views
    private lateinit var txtDay: TextView
    private lateinit var txtMonthYear: TextView
    private lateinit var txtText: TextView
    private lateinit var txtTemp: TextView
    private lateinit var txtTitle: TextView
    private lateinit var txtHumidity: TextView
    private lateinit var txtPressure: TextView
    private lateinit var txtSunrise: TextView
    private lateinit var txtSunset: TextView
    private lateinit var txt3days: TextView
    private lateinit var txt7days: TextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mWeatherDate = it.getString(WEATHER_DATE)
            mWeatherText = it.getString(WEATHER_TEXT)
            mWeatherNow = it.getDouble(WEATHER_NOW)
            mWeatherCity = it.getString(WEATHER_CITY)
            mWeatherHigh = it.getDouble(WEATHER_HIGH)
            mWeatherLow = it.getDouble(WEATHER_LOW)
            mWeatherSunrise = it.getString(WEATHER_SUNRISE)
            mWeatherSunset = it.getString(WEATHER_SUNSET)
            mWeatherDescription = it.getString(WEATHER_DESCRIPTION)
            mWeatherHumidity = it.getDouble(WEATHER_HUMIDITY)
            mWeatherPressure = it.getDouble(WEATHER_PRESSURE)
            mWeatherDay1 = it.getString("$WEATHER_D${1}")
            mWeatherDay2 = it.getString("$WEATHER_D${2}")
            mWeatherDay3 = it.getString("$WEATHER_D${3}")
            mWeatherDay4 = it.getString("$WEATHER_D${4}")
            mWeatherDay5 = it.getString("$WEATHER_D${5}")
            mWeatherDay6 = it.getString("$WEATHER_D${6}")
            mWeatherDay7 = it.getString("$WEATHER_D${7}")
            mWeatherDayArray.add(0, mWeatherDay1!!)
            mWeatherDayArray.add(1, mWeatherDay2!!)
            mWeatherDayArray.add(2, mWeatherDay3!!)
            mWeatherDayArray.add(3, mWeatherDay4!!)
            mWeatherDayArray.add(4, mWeatherDay5!!)
            mWeatherDayArray.add(5, mWeatherDay6!!)
            mWeatherDayArray.add(6, mWeatherDay7!!)
        }

        currentFragmentTag = TAG
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n", "RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(layout.fragment_extended, container, false)

        mActivity.mSetCity.isVisible = false
        mActivity.displayHomeAsUpEnabled(true)
        mActivity.setFabVisibility(false)

        initialization()
        setContent()

        return mView
    }

    override fun initViews() {
        super.initViews()
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
                txtSunrise = findViewById(R.id.txtSunrise)
                txtSunset = findViewById(R.id.txtSunset)
            }
            Log.d(TAG, "Views was initialized")
        } catch (e: Exception) {
            Log.d(TAG, "mView was not initialized")
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun initTools() {
        super.initTools()
        sdf = SimpleDateFormat(TXT_FULL_SDF_PATTERN)
        type = object : TypeToken<MutableList<WeatherDayModel>>() {}.type
        gson = Gson()
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity)
        layoutManager = LinearLayoutManager(
                mActivity,
                LinearLayoutManager.HORIZONTAL,
                false
        )
        recyclerView.layoutManager = layoutManager
        try {
            txtDay.text = SimpleDateFormat(TXT_DAY_PATTERN).format(
                    sdf.parse(mWeatherDate)
            )
            txtMonthYear.text =
                    SimpleDateFormat(TXT_MONTH_YEAR_PATTERN).format(
                            sdf.parse(mWeatherDate)
                    )
        } catch (e: ParseException) {
            mView.longSnackbar(
                    e.message.toString()
            ).show()
        }
        futureForecastsList = mutableListOf()
        mActivity.setActionBarTitle(mWeatherCity!!)
    }

    override fun onClick(btn: String) {
        super.onClick(btn)
        if (btnClickedId != txt3days.id) {
            futureForecasts = 7
            mSharedPreferences.edit().putString(
                    SHARED_PREFERENCES_KEY_FUTURE_FORECAST,
                    SHARED_PREFERENCES_7DAYS
            ).apply()
            txt7days.setTextColor(ContextCompat.getColor(mActivity, color.colorPrimary))
            txt3days.setTextColor(ContextCompat.getColor(mActivity, color.notSelected))
            refreshFragment()
        } else if (btnClickedId != txt7days.id) {
            futureForecasts = 3
            mSharedPreferences.edit().putString(
                    SHARED_PREFERENCES_KEY_FUTURE_FORECAST,
                    SHARED_PREFERENCES_3DAYS
            ).apply()
            txt3days.setTextColor(ContextCompat.getColor(mActivity, color.colorPrimary))
            txt7days.setTextColor(ContextCompat.getColor(mActivity, color.notSelected))
            refreshFragment()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setText() {
        txtText.text = mWeatherText
        if (mWeatherNow!! > 0.0) {
            txtTemp.text = "+$mWeatherNow"
        } else {
            txtTemp.text = mWeatherNow.toString()
        }
        txtTitle.text = mWeatherCity
        txtHumidity.text = "$mWeatherHumidity%"
        txtPressure.text = "$mWeatherPressure ${resources.getString(string.pressure_unit)}"
        txtSunrise.text = "$mWeatherSunrise"
        txtSunset.text = "$mWeatherSunset"
    }

    override fun checkSharedPreferences() {
        super.checkSharedPreferences()

        mActivity.mSetCity.isVisible = false
        mActivity.setFabVisibility(false)

        setText()
        // Checking for "futureForecast" in preferences
        if (!mSharedPreferences.contains(SHARED_PREFERENCES_KEY_FUTURE_FORECAST)) {
            mSharedPreferences.edit().putString(SHARED_PREFERENCES_KEY_FUTURE_FORECAST,
                    SHARED_PREFERENCES_7DAYS).apply()
        }
        // If configured then...
        if (
                mSharedPreferences.getString(
                        SHARED_PREFERENCES_KEY_FUTURE_FORECAST,
                        SHARED_PREFERENCES_7DAYS
                ) == SHARED_PREFERENCES_3DAYS
        ) {
            // ...Highlight "3-days" btn
            futureForecasts = 3
            btnClickedId = txt3days.id
            txt3days.setTextColor(ContextCompat.getColor(mActivity, color.colorPrimary))
            txt7days.setTextColor(ContextCompat.getColor(mActivity, color.notSelected))
        } else if (
                mSharedPreferences.getString(
                        SHARED_PREFERENCES_KEY_FUTURE_FORECAST,
                        SHARED_PREFERENCES_7DAYS
                ) == SHARED_PREFERENCES_7DAYS
        ) {
            // ...Highlight "7-days" btn
            futureForecasts = 7
            btnClickedId = txt7days.id
            txt7days.setTextColor(ContextCompat.getColor(mActivity, color.colorPrimary))
            txt3days.setTextColor(ContextCompat.getColor(mActivity, color.notSelected))
        }
    }

    override fun initListeners() {
        super.initListeners()
        txt3days.setOnClickListener {
            // "3 days" btn click
            btnClickedId = R.id.textView3days
            onClick("txt3days Button")
        }

        txt7days.setOnClickListener {
            // "7 days" btn click
            btnClickedId = R.id.textView7days
            onClick("txt7days Button")
        }
    }

    private fun initAdapter() {
        futureForecasts?.let {
            for (i in 0 until it) {
                futureForecastsList.add(
                        (gson.fromJson<Any>(
                                mWeatherDayArray[i],
                                type
                        ) as MutableList<*>
                                )[0] as WeatherDayModel
                )
                // Adding data to List
            }
        }
        recyclerViewAdapter = FutureForecastsAdapter(mActivity, futureForecastsList)
        recyclerView.adapter = recyclerViewAdapter
    }

    override fun refreshFragment() {
        super.refreshFragment()
        val ft = fragmentManager!!.beginTransaction()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ft.setReorderingAllowed(false)
        }
        ft.detach(this).attach(this).commit()
    }

    override fun finishingSetContent() {
        super.finishingSetContent()
        initAdapter()
    }

    @SuppressLint("RestrictedApi")
    override fun onBackPressed(): Boolean {
        mActivity.supportFragmentManager.popBackStack()
        mActivity.setActionBarTitle(getString(string.app_name))
        mActivity.mSetCity.isVisible = true
        mActivity.displayHomeAsUpEnabled(false)
        mActivity.setFabVisibility(true)
        return true
    }

    companion object {
        const val TAG = "ExtendedFragment"

        @JvmStatic
        fun newInstance(mWeatherDate: String, mWeatherText: String,
                        mWeatherNow: Double, mWeatherCity: String,
                        mWeatherHumidity: Double, mWeatherPressure: Double,
                        mWeatherHigh: Double, mWeatherLow: Double, mWeatherSunrise: String,
                        mWeatherSunset: String, mWeatherDescription: String,
                        mWeatherDay1: String, mWeatherDay2: String, mWeatherDay3: String,
                        mWeatherDay4: String, mWeatherDay5: String, mWeatherDay6: String,
                        mWeatherDay7: String) =
                ExtendedFragment().apply {
                    arguments = Bundle().apply {
                        putString(WEATHER_DATE, mWeatherDate)
                        putString(WEATHER_TEXT, mWeatherText)
                        putDouble(WEATHER_NOW, mWeatherNow)
                        putString(WEATHER_CITY, mWeatherCity)
                        putDouble(WEATHER_HIGH, mWeatherHigh)
                        putDouble(WEATHER_LOW, mWeatherLow)
                        putString(WEATHER_SUNRISE, mWeatherSunrise)
                        putString(WEATHER_SUNSET, mWeatherSunset)
                        putString(WEATHER_DESCRIPTION, mWeatherDescription)
                        putDouble(WEATHER_HUMIDITY, mWeatherHumidity)
                        putDouble(WEATHER_PRESSURE, mWeatherPressure)
                        putString("$WEATHER_D${1}", mWeatherDay1)
                        putString("$WEATHER_D${2}", mWeatherDay2)
                        putString("$WEATHER_D${3}", mWeatherDay3)
                        putString("$WEATHER_D${4}", mWeatherDay4)
                        putString("$WEATHER_D${5}", mWeatherDay5)
                        putString("$WEATHER_D${6}", mWeatherDay6)
                        putString("$WEATHER_D${7}", mWeatherDay7)
                    }
                }
    }
}