package tk.lorddarthart.accurateweathertestapp.application.view.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.Geocoder
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.widget.*
import tk.lorddarthart.accurateweathertestapp.R
import tk.lorddarthart.accurateweathertestapp.application.model.CityModel
import tk.lorddarthart.accurateweathertestapp.application.view.base.BaseActivity
import tk.lorddarthart.accurateweathertestapp.application.view.fragment.MainFragment
import tk.lorddarthart.accurateweathertestapp.util.ModelViewPresenter
import tk.lorddarthart.accurateweathertestapp.util.tools.WeatherDatabaseHelper

class MainActivity: BaseActivity(), ModelViewPresenter.MainActivityView {

    lateinit var mSqLiteDatabase: SQLiteDatabase
    lateinit var mDatabaseHelper: WeatherDatabaseHelper
    lateinit var mGeocoder: Geocoder
    lateinit var mSharedPreferences: SharedPreferences
    var cursor2: Cursor? = null
    lateinit var mCities: MutableList<CityModel>
    var isOpen = false

    lateinit var consLayText: ConstraintLayout
    lateinit var constraintLayout: ImageView
    lateinit var fab: FloatingActionButton
    lateinit var consLayOpen: Animation
    lateinit var consLayClose: Animation
    lateinit var rotateForward: Animation
    lateinit var rotateBackward: Animation
    lateinit var tvOpen: Animation
    lateinit var tvClose: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //initViews()
        setContent()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_setcity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when (it.itemId) {
                R.id.action_setcity -> optionItemSelected(it)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun initViews() {

    }

    override fun initAnimations() {

    }

    override fun setContent() {
        val mainFragment = R.id.mainFragment

        mGeocoder = Geocoder(this)
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        //
        mDatabaseHelper = WeatherDatabaseHelper(this, WeatherDatabaseHelper.DATABASE_NAME, null, WeatherDatabaseHelper.DATABASE_VERSION)
        mSqLiteDatabase = mDatabaseHelper.writableDatabase

        supportFragmentManager.beginTransaction().replace(mainFragment, MainFragment()).commit()
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

    @SuppressLint("Recycle", "InflateParams")
    override fun optionItemSelected(item: MenuItem) {
        val container = LinearLayout(this)
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
                        Snackbar.make(findViewById(android.R.id.content), errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
            container.addView(holder)
        }
        val builder = AlertDialog.Builder(this)
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
                                recreate()
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
}
