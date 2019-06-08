package tk.lorddarthart.accurateweathertestapp.application.view.fragment

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearLayoutManager.VERTICAL
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import tk.lorddarthart.accurateweathertestapp.R
import tk.lorddarthart.accurateweathertestapp.application.model.CityModel
import tk.lorddarthart.accurateweathertestapp.application.view.base.BaseFragment
import tk.lorddarthart.accurateweathertestapp.util.adapter.CitiesListAdapter
import tk.lorddarthart.accurateweathertestapp.util.tools.WeatherDatabaseHelper

class CitiesListFragment : BaseFragment() {
    private lateinit var mButtonApply: TextView
    private lateinit var mCitiesListRecycler: RecyclerView
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private lateinit var mCitiesListAdapter: CitiesListAdapter
    private lateinit var mCitiesListBackground: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentFragmentTag = TAG
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_cities_list, container, false)

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
        mCitiesListBackground.setBackgroundColor(Color.parseColor("#88343434"))
    }

    override fun initTools() {
        super.initTools()
        val changes = arrayOf(0)
        val citiesQuery = "SELECT * FROM " + WeatherDatabaseHelper.DATABASE_WEATHER_CITY
        val mCitiesCursor = mSqLiteDatabase.rawQuery(citiesQuery, arrayOfNulls(0))
        mCitiesCursor.moveToFirst()
        mCitiesCursor.moveToPrevious()
        mCitiesList = mutableListOf()
        mCitiesList.clear()
        while (mCitiesCursor.moveToNext()) {
            mCitiesList.add(
                    CityModel(mCitiesCursor.getInt(
                            mCitiesCursor.getColumnIndex(WeatherDatabaseHelper.WEATHER_CITY_ID)),
                            mCitiesCursor.getString(
                                    mCitiesCursor.getColumnIndex(
                                            WeatherDatabaseHelper.WEATHER_CITY_FILTERNAME
                                    )
                            )
                    )
            )
        }
        mLayoutManager = LinearLayoutManager(mActivity, VERTICAL, false)
        mCitiesListRecycler.layoutManager = mLayoutManager
        mCitiesListAdapter = CitiesListAdapter(mActivity, mCitiesList, mSqLiteDatabase)
        mCitiesListRecycler.adapter = mCitiesListAdapter
    }

    override fun onClick(btn: String) {
        super.onClick(btn)
    }

    override fun initListeners() {
        super.initListeners()
    }

    override fun checkSharedPreferences() {
        super.checkSharedPreferences()
    }

    override fun finishingSetContent() {
        super.finishingSetContent()
    }

    companion object {
        const val TAG = "CitiesListFragment"

        @JvmStatic
        fun newInstance() =
                CitiesListFragment()
    }
}
