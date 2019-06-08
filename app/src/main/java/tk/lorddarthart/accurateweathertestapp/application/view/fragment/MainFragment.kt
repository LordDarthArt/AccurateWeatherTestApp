package tk.lorddarthart.accurateweathertestapp.application.view.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.database.Cursor
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import tk.lorddarthart.accurateweathertestapp.R
import tk.lorddarthart.accurateweathertestapp.application.model.CityModel
import tk.lorddarthart.accurateweathertestapp.application.model.WeatherModel
import tk.lorddarthart.accurateweathertestapp.application.view.base.BaseFragment
import tk.lorddarthart.accurateweathertestapp.util.adapter.CitiesForecastsListAdapter
import tk.lorddarthart.accurateweathertestapp.util.tools.NetworkHelper
import tk.lorddarthart.accurateweathertestapp.util.tools.OnItemTouchListener
import tk.lorddarthart.accurateweathertestapp.util.tools.WeatherDatabaseHelper
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : BaseFragment() {
    private lateinit var mGeocoder: Geocoder
    private lateinit var mHttpServiceHelper: NetworkHelper
    private lateinit var mWeather: MutableList<WeatherModel>
    private lateinit var mForecastsCursor: Cursor
    private lateinit var mCitiesCursor: Cursor
    private var opening = 0
    private var opening2 = 0
    private lateinit var mAdresses: List<Address>
    private var isFloatingTextfieldOpen = false
    var mResponseCode: Int = 0

    // Visual
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private lateinit var mDialog: ProgressDialog
    private lateinit var mConsLayText: ConstraintLayout
    private lateinit var mConstraintLayout: ImageView
    private lateinit var mFab: FloatingActionButton
    private lateinit var mEditText: EditText
    private lateinit var mConsLayOpen: Animation
    private lateinit var mConsLayClose: Animation
    private lateinit var mRotateForward: Animation
    private lateinit var mRotateBackward: Animation
    private lateinit var mEditTextOpen: Animation
    private lateinit var mEditTextClose: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentFragmentTag = TAG
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_main, container, false)

        initialization()
        setContent()

        return mView
    }

    override fun initViews() {
        super.initViews()
        with(mView) {
            mConstraintLayout = findViewById(R.id.consLayHide)
            mConsLayText = findViewById(R.id.consLayText)
            mFab = findViewById(R.id.floatingActionButton)
            mEditText = findViewById(R.id.editText)
            mRecyclerView = findViewById(R.id.recyclerView)
        }
    }

    private fun initAnimations() {
        Log.d(TAG, getString(R.string.initanims_log))

        mRotateForward = AnimationUtils.loadAnimation(mActivity, R.anim.rotate_forward)
        mRotateBackward = AnimationUtils.loadAnimation(mActivity, R.anim.rotate_backward)
        mConsLayOpen = AnimationUtils.loadAnimation(mActivity, R.anim.conslay_open)
        mConsLayClose = AnimationUtils.loadAnimation(mActivity, R.anim.conslay_close)
        mEditTextOpen = AnimationUtils.loadAnimation(mActivity, R.anim.tv_open)
        mEditTextClose = AnimationUtils.loadAnimation(mActivity, R.anim.tv_close)
    }

    private fun initLists() {
        Log.d(TAG, getString(R.string.initlists_log))

        mCitiesList = mutableListOf()
        mWeather = mutableListOf()
    }

    override fun initTools() {
        super.initTools()
        initAnimations()
        initLists()
        mGeocoder = Geocoder(mActivity)
        mDatabaseHelper = WeatherDatabaseHelper(
                mActivity,
                WeatherDatabaseHelper.DATABASE_NAME,
                null,
                WeatherDatabaseHelper.DATABASE_VERSION
        )
        mConstraintLayout.visibility = View.GONE
        mForecastsCursor = mSqLiteDatabase.rawQuery(getQuery(), arrayOfNulls(0))
        mLayoutManager = LinearLayoutManager(mActivity)
        mRecyclerView.layoutManager = mLayoutManager
        mHttpServiceHelper = NetworkHelper()
    }

    override fun initListeners() {
        mFab.setOnClickListener { onClick("\"Add city\" Button") }
        mEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                checkOnTextChanged()
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
    }

    override fun checkSharedPreferences() {
        if (mSharedPreferences.getBoolean("mCitiesList", false)) {
            val citiesQuery = "SELECT * FROM " + WeatherDatabaseHelper.DATABASE_WEATHER_CITY
            mCitiesCursor = mSqLiteDatabase.rawQuery(citiesQuery, arrayOfNulls(0))
            mCitiesCursor.moveToFirst()
            mCitiesCursor.moveToPrevious()
            mCitiesList.clear()
            while (mCitiesCursor.moveToNext()) {
                mCitiesList.add(
                        CityModel(
                                mCitiesCursor.getInt(
                                        mCitiesCursor.getColumnIndex(
                                                WeatherDatabaseHelper.WEATHER_CITY_ID
                                        )
                                ),
                                mCitiesCursor.getString(
                                        mCitiesCursor.getColumnIndex(
                                                WeatherDatabaseHelper.WEATHER_CITY_FILTERNAME
                                        )
                                ),
                                mCitiesCursor.getString(
                                        mCitiesCursor.getColumnIndex(
                                                WeatherDatabaseHelper.WEATHER_CITY_LATITUDE
                                        )
                                ),
                                mCitiesCursor.getString(
                                        mCitiesCursor.getColumnIndex(
                                                WeatherDatabaseHelper.WEATHER_CITY_LONGITUDE
                                        )
                                )
                        )
                )
            }
        } else {
            try {
                mAdresses = mGeocoder.getFromLocationName("Санкт-Петербург",
                        1)
                if (mAdresses.isNotEmpty()) {
                    val latitude = mAdresses[0].latitude
                    val longitude = mAdresses[0].longitude
                    val addCitiesQuery = "INSERT INTO " +
                            WeatherDatabaseHelper.DATABASE_WEATHER_CITY + " (" +
                            WeatherDatabaseHelper.WEATHER_CITY_FILTERNAME + ", " +
                            WeatherDatabaseHelper.WEATHER_CITY_LATITUDE + ", " +
                            WeatherDatabaseHelper.WEATHER_CITY_LONGITUDE + ") VALUES " +
                            "('Санкт-Петербург'," + latitude.toString() + ", " +
                            longitude.toString() + ")"
                    mSqLiteDatabase.execSQL(addCitiesQuery)
                }
            } catch (e: IOException) {
                longSnackbar(mActivity.findViewById(android.R.id.content), e.message.toString())
            }

            try {
                mAdresses = mGeocoder.getFromLocationName("Москва", 1)
                if (mAdresses.isNotEmpty()) {
                    val latitude = mAdresses[0].latitude
                    val longitude = mAdresses[0].longitude
                    val addCitiesQuery = "INSERT INTO " +
                            WeatherDatabaseHelper.DATABASE_WEATHER_CITY +
                            " (" + WeatherDatabaseHelper.WEATHER_CITY_FILTERNAME + ", " +
                            WeatherDatabaseHelper.WEATHER_CITY_LATITUDE + ", " +
                            WeatherDatabaseHelper.WEATHER_CITY_LONGITUDE + ") VALUES ('Москва'," +
                            latitude.toString() + ", " +
                            longitude.toString() + ")"
                    mSqLiteDatabase.execSQL(addCitiesQuery)
                }
            } catch (e: IOException) {
                longSnackbar(mActivity.findViewById(android.R.id.content), e.message.toString())
            }

            mSharedPreferences.edit().putBoolean("mCitiesList", true).apply()
            val citiesQuery = "SELECT * FROM " + WeatherDatabaseHelper.DATABASE_WEATHER_CITY
            mCitiesCursor = mSqLiteDatabase.rawQuery(citiesQuery, arrayOfNulls(0))
            mCitiesCursor.moveToFirst()
            mCitiesCursor.moveToPrevious()
            mCitiesList.clear()
            while (mCitiesCursor.moveToNext()) {
                mCitiesList.add(
                        CityModel(mCitiesCursor.getInt(
                                mCitiesCursor.getColumnIndex(
                                        WeatherDatabaseHelper.WEATHER_CITY_ID)),
                                mCitiesCursor.getString(
                                        mCitiesCursor.getColumnIndex(
                                                WeatherDatabaseHelper.WEATHER_CITY_FILTERNAME
                                        )
                                ),
                                mCitiesCursor.getString(
                                        mCitiesCursor.getColumnIndex(
                                                WeatherDatabaseHelper.WEATHER_CITY_LATITUDE
                                        )
                                ),
                                mCitiesCursor.getString(
                                        mCitiesCursor.getColumnIndex(
                                                WeatherDatabaseHelper.WEATHER_CITY_LONGITUDE
                                        )
                                )
                        )
                )
            }
        }
    }

    override fun finishingSetContent() {
        super.finishingSetContent()
        getNetworkForecasts()
    }

    private fun checkOnTextChanged() {
        // Line length is checked on every attempt to edit text (?=0)
        if (mEditText.text.isNotEmpty() && mFab.rotation != -45f) {
            textChanged()
        } else if (mEditText.text.isEmpty()) {
            mFab.setImageResource(R.drawable.ic_baseline_plus_24px)
            mFab.rotation = 0f
            mFab.setOnClickListener { onClick("\"Add city\" Button") }
        }
    }

    private fun textChanged() {
        mFab.setImageResource(android.R.drawable.ic_menu_send)
        mFab.rotation = -45f
        mFab.setOnClickListener {
            mFab.rotation = 0f
            mFab.setImageResource(R.drawable.ic_baseline_plus_24px)
            animateFab()
            mFab.setOnClickListener { onClick("\"Add city\" Button") }
            try {
                mAdresses = mGeocoder.getFromLocationName(
                        mEditText.text.toString(), 1)
                if (mAdresses.isNotEmpty()) {
                    val latitude = mAdresses[0].latitude.toString()
                    val longitude = mAdresses[0].longitude.toString()
                    val sql =
                            "Insert into " +
                                    WeatherDatabaseHelper.DATABASE_WEATHER_CITY + " (" +
                                    WeatherDatabaseHelper.WEATHER_CITY_FILTERNAME +
                                    ", " + WeatherDatabaseHelper.WEATHER_CITY_LATITUDE +
                                    ", " +
                                    WeatherDatabaseHelper.WEATHER_CITY_LONGITUDE +
                                    ") VALUES ('" + mEditText.text.toString() + "'," +
                                    latitude + ", " + longitude + ")"
                    mSqLiteDatabase.execSQL(sql)
                } else {
                    throw IOException()
                }
            } catch (e: IOException) {
                longSnackbar(
                        mActivity.findViewById(android.R.id.content),
                        "Произошла ошибка, повторите попытку ввода"
                ).show()
            }

            mEditText.setText("")
            refreshRecycler()
        }
    }

    private fun refreshRecycler() {
        hideSoftKeyboard()
        val citiesQuery = "SELECT * FROM " +
                WeatherDatabaseHelper.DATABASE_WEATHER_CITY
        mCitiesCursor = mSqLiteDatabase.rawQuery(citiesQuery, arrayOfNulls(0))
        mCitiesCursor.moveToFirst()
        mCitiesCursor.moveToPrevious()
        mCitiesList.clear()
        while (mCitiesCursor.moveToNext()) {
            mCitiesList.add(
                    CityModel(
                            mCitiesCursor.getInt(
                                    mCitiesCursor.getColumnIndex(
                                            WeatherDatabaseHelper.WEATHER_CITY_ID
                                    )
                            ),
                            mCitiesCursor.getString(
                                    mCitiesCursor.getColumnIndex(
                                            WeatherDatabaseHelper.WEATHER_CITY_FILTERNAME
                                    )
                            ),
                            mCitiesCursor.getString(
                                    mCitiesCursor.getColumnIndex(
                                            WeatherDatabaseHelper.WEATHER_CITY_LATITUDE
                                    )
                            ),
                            mCitiesCursor.getString(
                                    mCitiesCursor.getColumnIndex(
                                            WeatherDatabaseHelper.WEATHER_CITY_LONGITUDE
                                    )
                            )
                    )
            )
        }
        opening = 0
        opening2 = 0
        mRecyclerView.visibility = View.INVISIBLE
        try {
            for (cycle in 0 until mCitiesList.size) {
                GlobalScope.launch(Dispatchers.Main) {
                    showLoading()
                    netOps(mCitiesList[cycle].mCityName!!,
                            mCitiesList[cycle].mLatitude!!,
                            mCitiesList[cycle].mLongitude!!)
                    opening2 = mCitiesList.size - 1
                    syncResult()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getNetworkForecasts() {
        try {
            for (i in mCitiesList.indices) {
                GlobalScope.launch(Dispatchers.Main) {
                    showLoading()
                    netOps(mCitiesList[i].mCityName!!,
                            mCitiesList[i].mLatitude!!,
                            mCitiesList[i].mLongitude!!)
                    syncResult()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun fabState(open: Boolean): String {
        return if (open) {
            getString(R.string.fabclose_log)
        } else {
            getString(R.string.fabopen_log)
        }
    }

    private fun animateFab() {
        Log.d(TAG, fabState(isFloatingTextfieldOpen))
        if (isFloatingTextfieldOpen) {
            mFab.startAnimation(mRotateForward)
            mConsLayText.startAnimation(mEditTextClose)
            mConsLayText.isClickable = false
            mConsLayText.visibility = View.GONE
            mConstraintLayout.startAnimation(mConsLayClose)
            mConstraintLayout.isClickable = false
            mConstraintLayout.visibility = View.GONE
            isFloatingTextfieldOpen = false
        } else {
            mConstraintLayout.visibility = View.VISIBLE
            mFab.startAnimation(mRotateBackward)
            mConsLayText.startAnimation(mEditTextOpen)
            mConsLayText.isClickable = true
            mConsLayText.visibility = View.VISIBLE
            mConstraintLayout.isClickable = true
            mConstraintLayout.setColorFilter(Color.argb(150, 155, 155, 155),
                    PorterDuff.Mode.DARKEN)
            mConstraintLayout.startAnimation(mConsLayOpen)
            isFloatingTextfieldOpen = true
        }
    }

    private fun getCurrentForecast() {
        Log.d(TAG, getString(R.string.gettingforecast_log))
        with(mForecastsCursor) {
            moveToFirst()
            moveToPrevious()
            mWeather.clear()
            while (moveToNext()) {
                val weathers = WeatherModel()
                weathers.weatherCity = getString(getColumnIndex(WeatherDatabaseHelper.WEATHER_CITY))
                weathers.weatherNow = getDouble(getColumnIndex(WeatherDatabaseHelper.WEATHER_NOW))
                weathers.weatherDate = getLong(getColumnIndex(WeatherDatabaseHelper.WEATHER_DATE))
                weathers.weatherHigh = getDouble(getColumnIndex(WeatherDatabaseHelper.WEATHER_HIGH))
                weathers.weatherLow = getDouble(getColumnIndex(WeatherDatabaseHelper.WEATHER_LOW))
                weathers.mWeatherSunrise = getString(
                        getColumnIndex(WeatherDatabaseHelper.WEATHER_SUNRISE)
                )
                weathers.mWeatherSunset = getString(
                        getColumnIndex(WeatherDatabaseHelper.WEATHER_SUNSET)
                )
                weathers.weatherText = getString(getColumnIndex(WeatherDatabaseHelper.WEATHER_TEXT))
                weathers.weatherDescription = getString(
                        getColumnIndex(WeatherDatabaseHelper.WEATHER_DESCRIPTION)
                )
                weathers.mWeatherHumidity = getDouble(
                        getColumnIndex(WeatherDatabaseHelper.WEATHER_HUMIDITY)
                )
                weathers.mWeatherPressure = getDouble(
                        getColumnIndex(WeatherDatabaseHelper.WEATHER_PRESSURE)
                )
                weathers.mWeatherDay1 = getString(getColumnIndex(WeatherDatabaseHelper.WEATHER_D1))
                weathers.mWeatherDay2 = getString(getColumnIndex(WeatherDatabaseHelper.WEATHER_D2))
                weathers.mWeatherDay3 = getString(getColumnIndex(WeatherDatabaseHelper.WEATHER_D3))
                weathers.mWeatherDay4 = getString(getColumnIndex(WeatherDatabaseHelper.WEATHER_D4))
                weathers.mWeatherDay5 = getString(getColumnIndex(WeatherDatabaseHelper.WEATHER_D5))
                weathers.mWeatherDay6 = getString(getColumnIndex(WeatherDatabaseHelper.WEATHER_D6))
                weathers.mWeatherDay7 = getString(getColumnIndex(WeatherDatabaseHelper.WEATHER_D7))
                mWeather.add(weathers)
            }
        }
        initAdapter()
    }

    private fun initAdapter() {
        Log.d(TAG, getString(R.string.initadapter_log))
        val itemTouchListener = object : OnItemTouchListener {
            @SuppressLint("SimpleDateFormat")
            override fun onCardViewTap(view: View, position: Int) {
                // Card tap opens the extended info fragment with forecast,
                // which was already saved to db
                val weathers = mWeather[position]
                val sdf = SimpleDateFormat(TXT_FULL_SDF_PATTERN)
                val date = Date(weathers.weatherDate)
                val dateText = sdf.format(date)
                val mainFragment = R.id.mainFragment
                val startExtendedFragment = ExtendedFragment.newInstance(dateText,
                        weathers.weatherText!!, weathers.weatherNow, weathers.weatherCity!!,
                        weathers.mWeatherHumidity, weathers.mWeatherPressure, weathers.weatherHigh,
                        weathers.weatherLow, weathers.mWeatherSunrise!!, weathers.mWeatherSunset!!,
                        weathers.weatherDescription!!, weathers.mWeatherDay1!!,
                        weathers.mWeatherDay2!!, weathers.mWeatherDay3!!, weathers.mWeatherDay4!!,
                        weathers.mWeatherDay5!!, weathers.mWeatherDay6!!, weathers.mWeatherDay7!!)
                mActivity.supportFragmentManager
                        .beginTransaction()
                        .add(mainFragment, startExtendedFragment)
                        .addToBackStack(null).commit()
            }

            override fun onButtonCvMenuClick(view: View, position: Int) {

            }
        }
        val recyclerViewAdapter = CitiesForecastsListAdapter(mActivity, mWeather, itemTouchListener)
        mRecyclerView.adapter = recyclerViewAdapter
    }

    private fun hideSoftKeyboard() {
        try {
            val inputMethodManager =
                    mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                    mFab.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
            )
        } catch (e: Exception) {
            longSnackbar(mActivity.findViewById(android.R.id.content), e.message.toString())
        }
    }

    override fun onClick(btn: String) {
        super.onClick(btn)
        animateFab()
    }

    private fun getQuery(): String {
        return SQL_SELECT +
                "${WeatherDatabaseHelper.WEATHER_FILTERNAME}$SQL_COMMA" +
                "${WeatherDatabaseHelper.WEATHER_DATE}$SQL_COMMA" +
                "${WeatherDatabaseHelper.WEATHER_CITY}$SQL_COMMA" +
                "${WeatherDatabaseHelper.WEATHER_NOW}$SQL_COMMA" +
                "${WeatherDatabaseHelper.WEATHER_HIGH}$SQL_COMMA" +
                "${WeatherDatabaseHelper.WEATHER_LOW}$SQL_COMMA" +
                "${WeatherDatabaseHelper.WEATHER_TEXT}$SQL_COMMA" +
                "${WeatherDatabaseHelper.WEATHER_DESCRIPTION}$SQL_COMMA" +
                "${WeatherDatabaseHelper.WEATHER_HUMIDITY}$SQL_COMMA" +
                "${WeatherDatabaseHelper.WEATHER_PRESSURE}$SQL_COMMA" +
                "${WeatherDatabaseHelper.WEATHER_SUNRISE}$SQL_COMMA" +
                "${WeatherDatabaseHelper.WEATHER_SUNSET}$SQL_COMMA" +
                "${WeatherDatabaseHelper.WEATHER_D1}$SQL_COMMA" +
                "${WeatherDatabaseHelper.WEATHER_D2}$SQL_COMMA" +
                "${WeatherDatabaseHelper.WEATHER_D3}$SQL_COMMA" +
                "${WeatherDatabaseHelper.WEATHER_D4}$SQL_COMMA" +
                "${WeatherDatabaseHelper.WEATHER_D5}$SQL_COMMA" +
                "${WeatherDatabaseHelper.WEATHER_D6}$SQL_COMMA" +
                "${WeatherDatabaseHelper.WEATHER_D7}$SQL_FROM" +
                WeatherDatabaseHelper.DATABASE_WEATHER
    }

    private fun showLoading() {
        Log.d(TAG, getString(R.string.spinner_loading))
        if (opening == 0) {
            mDialog = indeterminateProgressDialog(getString(R.string.sync)).apply {
                setCancelable(false)
            }
            mDialog.show()
            opening++
        }
    }

    private suspend fun netOps(mCity: String, latitude: String, longitude: String) {
        //TaskLoader.setTask(mActivity) - useless for now
        Log.d(TAG, getString(R.string.netops_log))
        try {
            mResponseCode = withContext(Dispatchers.IO) {
                mHttpServiceHelper.getForecast(mSqLiteDatabase, mActivity, mCity,
                        latitude, longitude)
            }
        } catch (e: Exception) {
            longSnackbar(
                    mActivity.findViewById(android.R.id.content),
                    e.message.toString()
            )
        }
    }

    private fun syncResult() {
        Log.d(TAG, getString(R.string.syncresult_log))
        opening2++
        if (opening2 == mCitiesList.size) {
            mForecastsCursor = mSqLiteDatabase.rawQuery(getQuery(), arrayOfNulls(0))
            getCurrentForecast()
            mDialog.dismiss()
            mRecyclerView.visibility = View.VISIBLE
            mConstraintLayout.visibility = View.GONE
            //refreshFragment()
        }
    }

    @SuppressLint("Recycle", "InflateParams")
    fun optionItemSelected(item: MenuItem) {
        mActivity.supportFragmentManager.beginTransaction()
                .add(R.id.mainFragment, CitiesListFragment.newInstance()).addToBackStack(null)
                .commitAllowingStateLoss()
    }

    companion object {
        const val TAG = "MainFragment"
        const val SQL_SELECT = "SELECT "
        const val SQL_COMMA = ", "
        const val SQL_FROM = " FROM "
        const val TXT_FULL_SDF_PATTERN = "dd-MM-yyyy HH:mm"

        @JvmStatic
        fun newInstance() = MainFragment
    }
}