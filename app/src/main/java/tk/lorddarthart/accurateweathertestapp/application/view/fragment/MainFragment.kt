package tk.lorddarthart.accurateweathertestapp.application.view.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import kotlinx.coroutines.*
import org.jetbrains.anko.design.longSnackbar
import org.json.JSONException
import tk.lorddarthart.accurateweathertestapp.R
import tk.lorddarthart.accurateweathertestapp.application.model.CityModel
import tk.lorddarthart.accurateweathertestapp.application.model.WeatherModel
import tk.lorddarthart.accurateweathertestapp.application.view.base.BaseFragment
import tk.lorddarthart.accurateweathertestapp.util.ModelViewPresenter
import tk.lorddarthart.accurateweathertestapp.util.adapter.RecyclerViewAdapter
import tk.lorddarthart.accurateweathertestapp.util.tools.NetworkHelper
import tk.lorddarthart.accurateweathertestapp.util.tools.OnItemTouchListener
import tk.lorddarthart.accurateweathertestapp.util.tools.WeatherDatabaseHelper
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val SQL_SELECT = "SELECT "
private const val SQL_COMMA = ", "
private const val SQL_FROM = " FROM "

class MainFragment : BaseFragment(), ModelViewPresenter.FragmentView {

    private lateinit var mSqLiteDatabase: SQLiteDatabase
    private lateinit var mDatabaseHelper: WeatherDatabaseHelper
    private lateinit var mGeocoder: Geocoder
    private lateinit var httpServiceHelper: NetworkHelper
    private lateinit var weather: MutableList<WeatherModel>
    private var cursor: Cursor? = null
    private var cursor2: Cursor? = null
    private lateinit var mSharedPreferences: SharedPreferences
    internal var opening = 0
    internal var opening2 = 0
    private lateinit var mAdresses: List<Address>
    private lateinit var mCities: MutableList<CityModel>
    private var isOpen = false
    var responseCode: Int = 0

    // Visual
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var dialog: ProgressDialog? = null
    private lateinit var consLayText: ConstraintLayout
    private lateinit var constraintLayout: ImageView
    private lateinit var fab: FloatingActionButton
    private lateinit var editText: EditText
    private lateinit var consLayOpen: Animation
    private lateinit var consLayClose: Animation
    private lateinit var rotateForward: Animation
    private lateinit var rotateBackward: Animation
    private lateinit var tvOpen: Animation
    private lateinit var tvClose: Animation

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_main, container, false)

        initialization()
        setContent()

        return mView
    }

    override fun initViews() {
        with(mView) {
            constraintLayout = findViewById(R.id.consLayHide)
            consLayText = findViewById(R.id.consLayText)
            fab = findViewById(R.id.floatingActionButton)
            editText = findViewById(R.id.editText)
            mRecyclerView = findViewById(R.id.recyclerView)
        }
    }

    override fun initAnimations() {
            rotateForward = AnimationUtils.loadAnimation(mActivity, R.anim.rotate_forward)
            rotateBackward = AnimationUtils.loadAnimation(mActivity, R.anim.rotate_backward)
            consLayOpen = AnimationUtils.loadAnimation(mActivity, R.anim.conslay_open)
            consLayClose = AnimationUtils.loadAnimation(mActivity, R.anim.conslay_close)
            tvOpen = AnimationUtils.loadAnimation(mActivity, R.anim.tv_open)
            tvClose = AnimationUtils.loadAnimation(mActivity, R.anim.tv_close)
    }

    override fun initLists() {
        mCities = mutableListOf()
        weather = mutableListOf()
    }

    override fun initialization() {
        initViews()
        initAnimations()
        initLists()
    }

    override fun setContent() {
        mGeocoder = Geocoder(mActivity)
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity)
        mDatabaseHelper = WeatherDatabaseHelper(mActivity, WeatherDatabaseHelper.DATABASE_NAME, null, WeatherDatabaseHelper.DATABASE_VERSION)
        mSqLiteDatabase = mDatabaseHelper.writableDatabase
        constraintLayout.visibility = View.VISIBLE
        cursor = mSqLiteDatabase.rawQuery(getQuery(), arrayOfNulls(0))
        layoutManager = LinearLayoutManager(mActivity)
        mRecyclerView.layoutManager = layoutManager
        fab.setOnClickListener { onClick() }
        rotateForward = AnimationUtils.loadAnimation(mActivity, R.anim.rotate_forward)
        rotateBackward = AnimationUtils.loadAnimation(mActivity, R.anim.rotate_backward)
        consLayOpen = AnimationUtils.loadAnimation(mActivity, R.anim.conslay_open)
        consLayClose = AnimationUtils.loadAnimation(mActivity, R.anim.conslay_close)
        tvOpen = AnimationUtils.loadAnimation(mActivity, R.anim.tv_open)
        tvClose = AnimationUtils.loadAnimation(mActivity, R.anim.tv_close)
        if (mSharedPreferences.getBoolean("mCities", false)) {
            val citiesQuery = "SELECT * FROM " + WeatherDatabaseHelper.DATABASE_WEATHER_CITY
            cursor2 = mSqLiteDatabase.rawQuery(citiesQuery, arrayOfNulls(0))
            cursor2!!.moveToFirst()
            cursor2!!.moveToPrevious()
            mCities.clear()
            while (cursor2!!.moveToNext()) {
                mCities.add(
                        CityModel(cursor2!!.getInt(
                                cursor2!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_CITY_ID)),
                                cursor2!!.getString(
                                        cursor2!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_CITY_FILTERNAME)
                                ),
                                cursor2!!.getString(cursor2!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_CITY_LATITUDE)),
                                cursor2!!.getString(cursor2!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_CITY_LONGITUDE))
                        )
                )
            }
        } else {
            try {
                mAdresses = mGeocoder.getFromLocationName("Санкт-Петербург", 1)
                if (mAdresses.isNotEmpty()) {
                    val latitude = mAdresses[0].latitude
                    val longitude = mAdresses[0].longitude
                    val addCitiesQuery = "INSERT INTO " +
                            WeatherDatabaseHelper.DATABASE_WEATHER_CITY + " (" +
                            WeatherDatabaseHelper.WEATHER_CITY_FILTERNAME + ", " +
                            WeatherDatabaseHelper.WEATHER_CITY_LATITUDE + ", " +
                            WeatherDatabaseHelper.WEATHER_CITY_LONGITUDE + ") VALUES ('Санкт-Петербург'," +
                            latitude.toString() + ", " + longitude.toString() + ")"
                    mSqLiteDatabase.execSQL(addCitiesQuery)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                mAdresses = mGeocoder.getFromLocationName("Москва", 1)
                if (mAdresses.isNotEmpty()) {
                    val latitude = mAdresses[0].latitude
                    val longitude = mAdresses[0].longitude
                    val addCitiesQuery = "INSERT INTO " + WeatherDatabaseHelper.DATABASE_WEATHER_CITY +
                            " (" + WeatherDatabaseHelper.WEATHER_CITY_FILTERNAME + ", " +
                            WeatherDatabaseHelper.WEATHER_CITY_LATITUDE + ", " +
                            WeatherDatabaseHelper.WEATHER_CITY_LONGITUDE + ") VALUES ('Москва'," +
                            latitude.toString() + ", " +
                            longitude.toString() + ")"
                    mSqLiteDatabase.execSQL(addCitiesQuery)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            mSharedPreferences.edit().putBoolean("mCities", true).apply()
            val citiesQuery = "SELECT * FROM " + WeatherDatabaseHelper.DATABASE_WEATHER_CITY
            cursor2 = mSqLiteDatabase.rawQuery(citiesQuery, arrayOfNulls(0))
            cursor2!!.moveToFirst()
            cursor2!!.moveToPrevious()
            mCities.clear()
            while (cursor2!!.moveToNext()) {
                mCities.add(
                        CityModel(cursor2!!.getInt(
                                cursor2!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_CITY_ID)),
                                cursor2!!.getString(
                                        cursor2!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_CITY_FILTERNAME)
                                ),
                                cursor2!!.getString(
                                        cursor2!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_CITY_LATITUDE)
                                ),
                                cursor2!!.getString(
                                        cursor2!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_CITY_LONGITUDE)
                                )
                        )
                )
            }
        }
        httpServiceHelper = NetworkHelper()
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                // При вводе текста в поле нового города проверяется длина строки (?=0)
                if (editText.text.isNotEmpty() && fab.rotation != -45f) {
                    fab.setImageResource(android.R.drawable.ic_menu_send)
                    fab.rotation = -45f
                    fab.setOnClickListener {
                        fab.rotation = 0f
                        fab.setImageResource(R.drawable.ic_baseline_plus_24px)
                        animateFab()
                        fab.setOnClickListener { onClick() }
                        try {
                            mAdresses = mGeocoder.getFromLocationName(editText.text.toString(), 1)
                            if (mAdresses.isNotEmpty()) {
                                val latitude = mAdresses[0].latitude.toString()
                                val longitude = mAdresses[0].longitude.toString()
                                val sql = "Insert into " + WeatherDatabaseHelper.DATABASE_WEATHER_CITY +
                                        " (" + WeatherDatabaseHelper.WEATHER_CITY_FILTERNAME + ", " +
                                        WeatherDatabaseHelper.WEATHER_CITY_LATITUDE + ", " +
                                        WeatherDatabaseHelper.WEATHER_CITY_LONGITUDE + ") VALUES ('" +
                                        editText.text.toString() + "'," +
                                        latitude + ", " + longitude + ")"
                                mSqLiteDatabase.execSQL(sql)
                            } else {
                                throw IOException()
                            }
                        } catch (e: IOException) {
                            Snackbar.make(mActivity.findViewById(android.R.id.content), "Произошла ошибка, повторите попытку ввода", Snackbar.LENGTH_LONG).show()
                        }

                        editText.setText("")
                        hideSoftKeyboard()
                        val citiesQuery = "SELECT * FROM " + WeatherDatabaseHelper.DATABASE_WEATHER_CITY
                        cursor2 = mSqLiteDatabase.rawQuery(citiesQuery, arrayOfNulls(0))
                        cursor2!!.moveToFirst()
                        cursor2!!.moveToPrevious()
                        while (cursor2!!.moveToNext()) {
                            mCities.add(
                                    CityModel(cursor2!!.getInt(cursor2!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_CITY_ID)),
                                            cursor2!!.getString(cursor2!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_CITY_FILTERNAME)),
                                            cursor2!!.getString(cursor2!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_CITY_LATITUDE)),
                                            cursor2!!.getString(cursor2!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_CITY_LONGITUDE))
                                    )
                            )
                        }
                        opening = 0
                        opening2 = 0
                        mRecyclerView.visibility = View.INVISIBLE
                        try {
                            for (cycle in 0 until mCities.size) {
                                showLoading()
                                runBlocking {
                                    netOps(mCities[i].mCityName!!, mCities[i].mLatitude!!, mCities[i].mLongitude!!)
                                    syncResult()
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } else if (editText.text.isEmpty()) {
                    fab.setImageResource(R.drawable.ic_baseline_plus_24px)
                    fab.rotation = 0f
                    fab.setOnClickListener { onClick() }
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
        try {
            for (i in mCities.indices) {
                GlobalScope.launch(Dispatchers.Main) {
                    showLoading()
                    netOps(mCities[i].mCityName!!, mCities[i].mLatitude!!, mCities[i].mLongitude!!)
                    syncResult()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun animateFab() {
        if (isOpen) {
            fab.startAnimation(rotateForward)
            consLayText.startAnimation(tvClose)
            consLayText.isClickable = false
            consLayText.visibility = View.GONE
            constraintLayout.startAnimation(consLayClose)
            constraintLayout.isClickable = false
            constraintLayout.visibility = View.GONE
            isOpen = false
        } else {
            fab.startAnimation(rotateBackward)
            consLayText.startAnimation(tvOpen)
            consLayText.isClickable = true
            consLayText.visibility = View.VISIBLE
            constraintLayout.isClickable = true
            constraintLayout.setColorFilter(Color.argb(150, 155, 155, 155), PorterDuff.Mode.DARKEN)
            constraintLayout.startAnimation(consLayOpen)
            isOpen = true
        }
    }

    fun getCurrentForecast() {
        // Получаем объект погоды на текущий день из бд
        cursor?.let {
            with(it) {
                moveToFirst()
                moveToPrevious()
                weather.clear()
                while (moveToNext()) {
                    val weathers = WeatherModel()
                    weathers.weatherCity = getString(getColumnIndex(WeatherDatabaseHelper.WEATHER_CITY))
                    weathers.weatherNow = getDouble(getColumnIndex(WeatherDatabaseHelper.WEATHER_NOW))
                    weathers.weatherDate = getLong(getColumnIndex(WeatherDatabaseHelper.WEATHER_DATE))
                    weathers.weatherHigh = getDouble(getColumnIndex(WeatherDatabaseHelper.WEATHER_HIGH))
                    weathers.weatherLow = getDouble(getColumnIndex(WeatherDatabaseHelper.WEATHER_LOW))
                    weathers.mWeatherSunrise = getString(getColumnIndex(WeatherDatabaseHelper.WEATHER_SUNRISE))
                    weathers.mWeatherSunset = getString(getColumnIndex(WeatherDatabaseHelper.WEATHER_SUNSET))
                    weathers.weatherText = getString(getColumnIndex(WeatherDatabaseHelper.WEATHER_TEXT))
                    weathers.weatherDescription = getString(getColumnIndex(WeatherDatabaseHelper.WEATHER_DESCRIPTION))
                    weathers.mWeatherHumidity = getDouble(getColumnIndex(WeatherDatabaseHelper.WEATHER_HUMIDITY))
                    weathers.mWeatherPressure = getDouble(getColumnIndex(WeatherDatabaseHelper.WEATHER_PRESSURE))
                    weathers.mWeatherDay1 = getString(getColumnIndex(WeatherDatabaseHelper.WEATHER_D1))
                    weathers.mWeatherDay2 = getString(getColumnIndex(WeatherDatabaseHelper.WEATHER_D2))
                    weathers.mWeatherDay3 = getString(getColumnIndex(WeatherDatabaseHelper.WEATHER_D3))
                    weathers.mWeatherDay4 = getString(getColumnIndex(WeatherDatabaseHelper.WEATHER_D4))
                    weathers.mWeatherDay5 = getString(getColumnIndex(WeatherDatabaseHelper.WEATHER_D5))
                    weathers.mWeatherDay6 = getString(getColumnIndex(WeatherDatabaseHelper.WEATHER_D6))
                    weathers.mWeatherDay7 = getString(getColumnIndex(WeatherDatabaseHelper.WEATHER_D7))
                    weather.add(weathers)
                }
            }
        }
        initializeAdapter()
    }

    private fun initializeAdapter() {
        val itemTouchListener = object : OnItemTouchListener {
            @SuppressLint("SimpleDateFormat")
            override fun onCardViewTap(view: View, position: Int) {
                // При тапе на карточку с городом открывается фрагмент с информацией, заранее сохранённой в базе
                val weathers = weather[position]
                val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm")
                val date = Date(weathers.weatherDate)
                val dateText = sdf.format(date)
                val mainFragment = R.id.mainFragment
                val startExtendedFragment = ExtendedFragment.newInstance(dateText, weathers.weatherText!!,
                        weathers.weatherNow, weathers.weatherCity!!, weathers.mWeatherHumidity,
                        weathers.mWeatherPressure, weathers.weatherHigh, weathers.weatherLow,
                        weathers.mWeatherSunrise!!, weathers.mWeatherSunset!!, weathers.weatherDescription!!,
                        weathers.mWeatherDay1!!, weathers.mWeatherDay2!!, weathers.mWeatherDay3!!,
                        weathers.mWeatherDay4!!, weathers.mWeatherDay5!!, weathers.mWeatherDay6!!,
                        weathers.mWeatherDay7!!)
                mActivity.supportFragmentManager
                        .beginTransaction()
                        .add(mainFragment, startExtendedFragment)
                        .addToBackStack(null).commit()
            }

            override fun onButtonCvMenuClick(view: View, position: Int) {

            }
        }
        val recyclerViewAdapter = RecyclerViewAdapter(mActivity, weather, itemTouchListener)
        mRecyclerView.adapter = recyclerViewAdapter
    }

    override fun hideSoftKeyboard() {
        try {
            val inputMethodManager = mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(fab.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick() {
        animateFab()
    }

    override fun getQuery(): String {
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

    override fun showLoading() {
        if (opening == 0) {
            dialog = ProgressDialog(mActivity)
            dialog?.let {
                with(it) {
                    setMessage("Синхронизация…")
                    setProgressStyle(ProgressDialog.STYLE_SPINNER)
                    setCancelable(false)
                    show()
                }
            }
            opening++
        }
    }

    override suspend fun netOps(mCity: String, latitude: String, longitude: String) {
        //TaskLoader.setTask(mActivity)
        try {
            responseCode = withContext(Dispatchers.IO) {
                httpServiceHelper.getForecast(mSqLiteDatabase, mActivity, mCity,
                        latitude, longitude)
            }
        } catch (e: IOException) {
            longSnackbar(mActivity.findViewById(android.R.id.content), e.message.toString())
        } catch (e: JSONException) {
            longSnackbar(mActivity.findViewById(android.R.id.content), e.message.toString())
        }
    }

    override fun syncResult() {
        opening2++
        if (opening2 == mCities.size) {
            cursor = mSqLiteDatabase.rawQuery(getQuery(), arrayOfNulls(0))
            getCurrentForecast()
            dialog!!.dismiss()
            mRecyclerView.visibility = View.VISIBLE
            constraintLayout.visibility = View.GONE
        }
    }

    @SuppressLint("Recycle", "InflateParams")
    override fun optionItemSelected(item: MenuItem) {
        val container = LinearLayout(mActivity)
        container.orientation = LinearLayout.VERTICAL
        val changes = arrayOf(0)
        val citiesQuery = "SELECT * FROM " + WeatherDatabaseHelper.DATABASE_WEATHER_CITY
        cursor2 = mSqLiteDatabase.rawQuery(citiesQuery, arrayOfNulls(0))
        cursor2!!.moveToFirst()
        cursor2!!.moveToPrevious()
        mCities.clear()
        while (cursor2!!.moveToNext()) {
            mCities.add(
                    CityModel(cursor2!!.getInt(
                            cursor2!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_CITY_ID)),
                            cursor2!!.getString(cursor2!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_CITY_FILTERNAME))
                    )
            )
        }
        for (i in mCities.indices) {
            val holder = layoutInflater.inflate(R.layout.settings_city, null, false)
            val textViewCity = holder.findViewById<TextView>(R.id.tvCity)
            textViewCity.text = mCities[i].mCityName
            val img = holder.findViewById<ImageView>(R.id.ivDelCity)
            img.setOnClickListener {
                try {
                    val query = "DELETE from " + WeatherDatabaseHelper.DATABASE_WEATHER_CITY + " WHERE " + WeatherDatabaseHelper.WEATHER_FILTERNAME + " = \"" + textViewCity.text.toString() + "\""
                    val query2 = "DELETE from " + WeatherDatabaseHelper.DATABASE_WEATHER_CITY + " WHERE " + WeatherDatabaseHelper.WEATHER_CITY_FILTERNAME + " = \"" + textViewCity.text.toString() + "\""
                    mSqLiteDatabase.execSQL(query)
                    mSqLiteDatabase.execSQL(query2)
                    textViewCity.visibility = View.GONE
                    img.visibility = View.GONE
                    changes[0]++
                } catch (e: Exception) {
                    e.message?.let { errorMessage ->
                        longSnackbar(mActivity.findViewById(android.R.id.content), errorMessage).show()
                    }
                }
            }
            container.addView(holder)
        }
        val builder = AlertDialog.Builder(mActivity)
                .setTitle("Текущие города")
                .setPositiveButton(android.R.string.ok, null)
                .setCancelable(false)
                .setView(container)
                .create()
        when (item.itemId) {
            R.id.action_setcity -> {
                builder.setOnShowListener {
                    val button = builder.getButton(AlertDialog.BUTTON_POSITIVE)
                    button.setOnClickListener {
                        try {
                            builder.dismiss()
                            if (changes[0] > 0) {
                                mActivity.recreate()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                builder.setOnDismissListener { }
            }
        }
        builder.show()
    }

    companion object {
        const val TAG = "MainFragment"

        @JvmStatic
        fun newInstance() = MainFragment
    }
}