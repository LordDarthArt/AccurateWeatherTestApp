package tk.lorddarthart.accurateweathertestapp.application.view.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearLayoutManager.VERTICAL
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import tk.lorddarthart.accurateweathertestapp.R
import tk.lorddarthart.accurateweathertestapp.application.model.city.CityModel
import tk.lorddarthart.accurateweathertestapp.application.view.base.BaseFragment
import tk.lorddarthart.accurateweathertestapp.util.IOnBackPressed
import tk.lorddarthart.accurateweathertestapp.util.adapter.CitiesListAdapter
import tk.lorddarthart.accurateweathertestapp.util.constants.SqlCommands.SQL_SELECT_ALL
import tk.lorddarthart.accurateweathertestapp.util.tools.RVClickHandler
import tk.lorddarthart.accurateweathertestapp.util.tools.WeatherDatabaseHelper

class CitiesListFragment : BaseFragment(), IOnBackPressed {
    lateinit var mButtonApply: TextView
    lateinit var mCitiesListRecycler: RecyclerView
    lateinit var mLayoutManager: RecyclerView.LayoutManager
    lateinit var mCitiesListAdapter: CitiesListAdapter
    lateinit var mCitiesListBackground: ImageView
    val changes = arrayOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentFragmentTag = TAG
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_cities_list, container, false)

        //mActivity.setFabVisibility(false)

        initialization()
        setContent()

        return mView
    }

    override fun initViews() {
        super.initViews()
        with(mView) {
            mButtonApply = findViewById(R.id.txtBtn)
            mCitiesListRecycler = findViewById(R.id.cities_list_recycler)
            mCitiesListBackground = findViewById(R.id.cities_list_background)
        }
        mCitiesListBackground.setColorFilter(
                Color.argb(150, 155, 155, 155),
                PorterDuff.Mode.DARKEN
        )
        mCitiesListBackground.setBackgroundColor(
                ContextCompat.getColor(
                        mActivity,
                        R.color.mDarkenBackground
                )
        )
    }

    @SuppressLint("Recycle")
    override fun initTools() {
        super.initTools()

        mActivity.mSetCity.isVisible = false
        mActivity.setFabVisibility(false)

        val citiesQuery = SQL_SELECT_ALL + WeatherDatabaseHelper.DATABASE_WEATHER_CITY
        val mCitiesCursor = mSqLiteDatabase.rawQuery(citiesQuery, arrayOfNulls(0))
        mCitiesCursor.moveToFirst()
        mCitiesCursor.moveToPrevious()
        mCitiesList = mutableListOf()
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
        mLayoutManager = LinearLayoutManager(mActivity, VERTICAL, false)
        mCitiesListRecycler.layoutManager = mLayoutManager
        mCitiesListAdapter = CitiesListAdapter(mActivity,
                this,
                mCitiesList,
                mSqLiteDatabase,
                changes
        )
        mCitiesListRecycler.adapter = mCitiesListAdapter
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initListeners() {
        super.initListeners()
        mButtonApply.setOnClickListener {
            onApplySettings()
        }
        mCitiesListBackground.setOnClickListener{
            onApplySettings()
        }
        mCitiesListRecycler.setOnTouchListener(
                RVClickHandler(mCitiesListRecycler)
        )
        mCitiesListRecycler.setOnClickListener {
            onApplySettings()
        }
    }

    @SuppressLint("RestrictedApi")
    fun onApplySettings() {
        if (changes[0] > 0) {
            mActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.mainFragment, MainFragment()).commit()
        } else {
            mActivity.supportFragmentManager.popBackStack()
        }

        mActivity.setFabVisibility(true)
        mActivity.mSetCity.isVisible = true
    }

    @SuppressLint("RestrictedApi")
    override fun onBackPressed(): Boolean {
        mActivity.supportFragmentManager.popBackStack()

        mActivity.setFabVisibility(true)
        mActivity.mSetCity.isVisible = true
        return true
    }

    companion object {
        const val TAG = "CitiesListFragment"

        @JvmStatic
        fun newInstance() =
                CitiesListFragment()
    }
}
