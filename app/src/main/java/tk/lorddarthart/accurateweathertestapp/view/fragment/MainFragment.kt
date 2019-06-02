package tk.lorddarthart.accurateweathertestapp.view.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import org.json.JSONException
import tk.lorddarthart.accurateweathertestapp.R
import tk.lorddarthart.accurateweathertestapp.adapter.RecyclerViewAdapter
import tk.lorddarthart.accurateweathertestapp.model.CityModel
import tk.lorddarthart.accurateweathertestapp.model.WeatherModel
import tk.lorddarthart.accurateweathertestapp.util.NetworkHelper
import tk.lorddarthart.accurateweathertestapp.util.OnItemTouchListener
import tk.lorddarthart.accurateweathertestapp.util.TaskLoader
import tk.lorddarthart.accurateweathertestapp.util.WeatherDatabaseHelper
import tk.lorddarthart.accurateweathertestapp.view.base.BaseFragment
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainFragment: BaseFragment() {

    private lateinit var mSqLiteDatabase: SQLiteDatabase
    private lateinit var geocoder: Geocoder
    private lateinit var mDatabaseHelper: WeatherDatabaseHelper
    private lateinit var httpServiceHelper: NetworkHelper
    private lateinit var weather: MutableList<WeatherModel>
    private var cursor: Cursor? = null
    private var cursor2: Cursor? = null
    private var dialog: ProgressDialog? = null
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var ed: SharedPreferences.Editor
    internal var opening = 0
    internal var opening2 = 0
    private lateinit var addresses: List<Address>
    private lateinit var cities: MutableList<CityModel>
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
    private var isOpen = false

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_main, container, false)

        mDatabaseHelper = WeatherDatabaseHelper(mActivity)
        geocoder = Geocoder(mActivity)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity)
        ed = sharedPreferences.edit()

        initViews()
        setContent()

        return mView
    }

    fun initViews() {
        with(mView) {
            constraintLayout = findViewById(R.id.consLayHide)
            consLayText = findViewById(R.id.consLayText)
            editText = findViewById(R.id.editText)
            fab = findViewById(R.id.floatingActionButton)
            mRecyclerView = findViewById(R.id.recyclerView)
        }
    }

    fun setContent() {


        constraintLayout.visibility = View.VISIBLE
        mDatabaseHelper = WeatherDatabaseHelper(mActivity, WeatherDatabaseHelper.DATABASE_NAME, null, WeatherDatabaseHelper.DATABASE_VERSION)
        mSqLiteDatabase = mDatabaseHelper.writableDatabase
        val query = ("SELECT " + WeatherDatabaseHelper.WEATHER_FILTERNAME + ", " + WeatherDatabaseHelper.WEATHER_DATE + ", " + WeatherDatabaseHelper.WEATHER_CITY + ", " + WeatherDatabaseHelper.WEATHER_NOW + ", "
                + WeatherDatabaseHelper.WEATHER_HIGH + " , " + WeatherDatabaseHelper.WEATHER_LOW + ", " + WeatherDatabaseHelper.WEATHER_TEXT + ", " + WeatherDatabaseHelper.WEATHER_DESCRIPTION + ", "
                + WeatherDatabaseHelper.WEATHER_HUMIDITY + ", " + WeatherDatabaseHelper.WEATHER_PRESSURE + ", "
                + WeatherDatabaseHelper.WEATHER_SUNRISE + ", " + WeatherDatabaseHelper.WEATHER_SUNSET + ", " + WeatherDatabaseHelper.WEATHER_D1 + ", " + WeatherDatabaseHelper.WEATHER_D2 + ", " + WeatherDatabaseHelper.WEATHER_D3
                + ", " + WeatherDatabaseHelper.WEATHER_D4 + ", " + WeatherDatabaseHelper.WEATHER_D5 + ", " + WeatherDatabaseHelper.WEATHER_D6 + ", " + WeatherDatabaseHelper.WEATHER_D7 + " FROM " + WeatherDatabaseHelper.DATABASE_WEATHER)
        cursor = mSqLiteDatabase.rawQuery(query, arrayOfNulls(0))
        layoutManager = LinearLayoutManager(mActivity)
        mRecyclerView.layoutManager = layoutManager
        fab.setOnClickListener { animateFab() }
        rotateForward = AnimationUtils.loadAnimation(mActivity, R.anim.rotate_forward)
        rotateBackward = AnimationUtils.loadAnimation(mActivity, R.anim.rotate_backward)
        consLayOpen = AnimationUtils.loadAnimation(mActivity, R.anim.conslay_open)
        consLayClose = AnimationUtils.loadAnimation(mActivity, R.anim.conslay_close)
        tvOpen = AnimationUtils.loadAnimation(mActivity, R.anim.tv_open)
        tvClose = AnimationUtils.loadAnimation(mActivity, R.anim.tv_close)
        if (sharedPreferences.getBoolean("cities", false)) {
            val citiesQuery = "SELECT * FROM " + WeatherDatabaseHelper.DATABASE_WEATHER_CITY
            cursor2 = mSqLiteDatabase.rawQuery(citiesQuery, arrayOfNulls(0))
            cursor2!!.moveToFirst()
            cursor2!!.moveToPrevious()
            cities.clear()
            while (cursor2!!.moveToNext()) {
                cities.add(
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
                addresses = geocoder.getFromLocationName("Санкт-Петербург", 1)
                if (!addresses.isEmpty()) {
                    val latitude = addresses[0].latitude
                    val longitude = addresses[0].longitude
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
                addresses = geocoder.getFromLocationName("Москва", 1)
                if (!addresses.isEmpty()) {
                    val latitude = addresses[0].latitude
                    val longitude = addresses[0].longitude
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

            ed.putBoolean("cities", true)
            ed.apply()
            val citiesQuery = "SELECT * FROM " + WeatherDatabaseHelper.DATABASE_WEATHER_CITY
            cursor2 = mSqLiteDatabase.rawQuery(citiesQuery, arrayOfNulls(0))
            cursor2!!.moveToFirst()
            cursor2!!.moveToPrevious()
            cities.clear()
            while (cursor2!!.moveToNext()) {
                cities.add(
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
                if (editText.text.length > 0 && fab.rotation != -45f) {
                    fab.setImageResource(android.R.drawable.ic_menu_send)
                    fab.rotation = -45f
                    fab.setOnClickListener {
                        fab.rotation = 0f
                        fab.setImageResource(R.drawable.ic_baseline_plus_24px)
                        animateFab()
                        fab.setOnClickListener { animateFab() }
                        try {
                            addresses = geocoder.getFromLocationName(editText.text.toString(), 1)
                            if (addresses.size > 0) {
                                val latitude = addresses[0].latitude.toString()
                                val longitude = addresses[0].longitude.toString()
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
                            cities.add(
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
                            for (i in 0 until cities.size) {
                                UpdateForecast(cities[i].cityName!!, cities[i].latitude!!, cities[i].longitude!!).execute()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } else if (editText.text.isEmpty()) {
                    fab.setImageResource(R.drawable.ic_baseline_plus_24px)
                    fab.rotation = 0f
                    fab.setOnClickListener { animateFab() }
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
        try {
            for (i in cities.indices) {
                UpdateForecast(cities[i].cityName!!, cities[i].latitude!!, cities[i].longitude!!).execute()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun animateFab() {
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

    override fun onStop() {
        super.onStop()
        TaskLoader.cancel()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val container = LinearLayout(mActivity)
        container.orientation = LinearLayout.VERTICAL
        val changes = arrayOf(0)
        val citiesQuery = "SELECT * FROM " + WeatherDatabaseHelper.DATABASE_WEATHER_CITY
        cursor2 = mSqLiteDatabase.rawQuery(citiesQuery, arrayOfNulls(0))
        cursor2!!.moveToFirst()
        cursor2!!.moveToPrevious()
        cities.clear()
        while (cursor2!!.moveToNext()) {
            cities.add(
                    CityModel(cursor2!!.getInt(
                            cursor2!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_CITY_ID)),
                            cursor2!!.getString(cursor2!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_CITY_FILTERNAME))
                    )
            )
        }
        for (i in cities.indices) {
            val holder = layoutInflater.inflate(R.layout.settings_city, null, false)
            val textViewCity = holder.findViewById<TextView>(R.id.tvCity)
            textViewCity.text = cities[i].cityName
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
                        Snackbar.make(mActivity.findViewById(android.R.id.content), errorMessage, Toast.LENGTH_LONG).show()
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
        return super.onOptionsItemSelected(item)
    }

    fun getCurrentForecast() {
        // Получаем объект погоды на текущий день из бд
        cursor!!.moveToFirst()
        cursor!!.moveToPrevious()
        weather.clear()
        while (cursor!!.moveToNext()) {
            val weathers = WeatherModel()
            weathers.weatherCity = cursor!!.getString(cursor!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_CITY))
            weathers.weatherNow = cursor!!.getDouble(cursor!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_NOW))
            weathers.weatherDate = cursor!!.getLong(cursor!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_DATE))
            weathers.weatherHigh = cursor!!.getDouble(cursor!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_HIGH))
            weathers.weatherLow = cursor!!.getDouble(cursor!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_LOW))
            weathers.mWeatherSunrise = cursor!!.getString(cursor!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_SUNRISE))
            weathers.mWeatherSunset = cursor!!.getString(cursor!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_SUNSET))
            weathers.weatherText = cursor!!.getString(cursor!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_TEXT))
            weathers.weatherDescription = cursor!!.getString(cursor!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_DESCRIPTION))
            weathers.mWeatherHumidity = cursor!!.getDouble(cursor!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_HUMIDITY))
            weathers.mWeatherPressure = cursor!!.getDouble(cursor!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_PRESSURE))
            weathers.mWeatherDay1 = cursor!!.getString(cursor!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_D1))
            weathers.mWeatherDay2 = cursor!!.getString(cursor!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_D2))
            weathers.mWeatherDay3 = cursor!!.getString(cursor!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_D3))
            weathers.mWeatherDay4 = cursor!!.getString(cursor!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_D4))
            weathers.mWeatherDay5 = cursor!!.getString(cursor!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_D5))
            weathers.mWeatherDay6 = cursor!!.getString(cursor!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_D6))
            weathers.mWeatherDay7 = cursor!!.getString(cursor!!.getColumnIndex(WeatherDatabaseHelper.WEATHER_D7))
            weather.add(weathers)
        }
        initializeAdapter()
    }

    private fun initializeAdapter() {
        val itemTouchListener = object : OnItemTouchListener {
            @SuppressLint("SimpleDateFormat")
            override fun onCardViewTap(view: View, position: Int) {
                // При тапе на карточку с городом создаётся интент с информацией, заранее сохранённой в базе
                val weathers = weather[position]
                val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm")
                val date = Date(weathers.weatherDate)
                val dateText = sdf.format(date)
                val startExtendedFragment = ExtendedFragment.newInstance(dateText, weathers.weatherText!!,
                        weathers.weatherNow, weathers.weatherCity!!, weathers.mWeatherHumidity,
                        weathers.mWeatherPressure, weathers.weatherHigh, weathers.weatherLow,
                        weathers.mWeatherSunrise!!, weathers.mWeatherSunset!!, weathers.weatherDescription!!,
                        weathers.mWeatherDay1!!, weathers.mWeatherDay2!!, weathers.mWeatherDay3!!,
                        weathers.mWeatherDay4!!, weathers.mWeatherDay5!!, weathers.mWeatherDay6!!,
                        weathers.mWeatherDay7!!)
                mActivity.supportFragmentManager
                        .beginTransaction()
                        .add(R.id.mainFragment, startExtendedFragment)
                        .addToBackStack(null).commit()
            }

            override fun onButtonCvMenuClick(view: View, position: Int) {

            }
        }
        val recyclerViewAdapter = RecyclerViewAdapter(mActivity, weather, itemTouchListener)
        mRecyclerView.adapter = recyclerViewAdapter
    }

    internal inner class UpdateForecast(var city: String, var latitude: String, var longitude: String) : AsyncTask<String, Void, Void>() {
        var responseCode: Int = 0

        override fun onPreExecute() {
            if (opening == 0) {
                dialog = ProgressDialog(mActivity)
                dialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                dialog!!.setMessage("Синхронизация…")
                dialog!!.setCancelable(false)
                dialog!!.show()
                opening++
            }
        }

        override fun doInBackground(vararg strings: String): Void? {
            TaskLoader.setTask(this)
            try {
                responseCode = httpServiceHelper.getForecast(mSqLiteDatabase, city, latitude, longitude)
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(result: Void) {
            opening2++
            if (opening2 == cities.size) {
                val query = ("SELECT " + WeatherDatabaseHelper.WEATHER_FILTERNAME + ", " + WeatherDatabaseHelper.WEATHER_DATE + ", " + WeatherDatabaseHelper.WEATHER_CITY + ", " + WeatherDatabaseHelper.WEATHER_NOW + ", "
                        + WeatherDatabaseHelper.WEATHER_HIGH + " , " + WeatherDatabaseHelper.WEATHER_LOW + ", " + WeatherDatabaseHelper.WEATHER_TEXT + ", " + WeatherDatabaseHelper.WEATHER_DESCRIPTION + ", "
                        + WeatherDatabaseHelper.WEATHER_HUMIDITY + ", " + WeatherDatabaseHelper.WEATHER_PRESSURE + ", "
                        + WeatherDatabaseHelper.WEATHER_SUNRISE + ", " + WeatherDatabaseHelper.WEATHER_SUNSET + ", " + WeatherDatabaseHelper.WEATHER_D1 + ", " + WeatherDatabaseHelper.WEATHER_D2 + ", " + WeatherDatabaseHelper.WEATHER_D3
                        + ", " + WeatherDatabaseHelper.WEATHER_D4 + ", " + WeatherDatabaseHelper.WEATHER_D5 + ", " + WeatherDatabaseHelper.WEATHER_D6 + ", " + WeatherDatabaseHelper.WEATHER_D7 + " FROM " + WeatherDatabaseHelper.DATABASE_WEATHER)
                cursor = mSqLiteDatabase.rawQuery(query, arrayOfNulls(0))
                getCurrentForecast()
                dialog!!.dismiss()
                mRecyclerView.visibility = View.VISIBLE
                constraintLayout.visibility = View.GONE
            }
        }
    }

    fun hideSoftKeyboard() { // Спрятать вручную софтовую клавиатуру Android.
        try {
            val inputMethodManager = mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(fab.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}