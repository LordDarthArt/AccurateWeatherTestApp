package tk.lorddarthart.accurateweathertestapp.application.view.base

import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.os.SharedMemory
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tk.lorddarthart.accurateweathertestapp.R
import tk.lorddarthart.accurateweathertestapp.application.model.CityModel
import tk.lorddarthart.accurateweathertestapp.application.view.activity.MainActivity
import tk.lorddarthart.accurateweathertestapp.util.ModelViewPresenter
import tk.lorddarthart.accurateweathertestapp.util.tools.WeatherDatabaseHelper

open class BaseFragment : Fragment(), ModelViewPresenter.FragmentView {
    lateinit var mView: View
    lateinit var mActivity: MainActivity
    lateinit var currentFragmentTag: String
    lateinit var mSharedPreferences: SharedPreferences
    lateinit var mDatabaseHelper: WeatherDatabaseHelper
    lateinit var mSqLiteDatabase: SQLiteDatabase
    lateinit var mCitiesList: MutableList<CityModel>

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        mActivity = context as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(
                currentFragmentTag,
                "Attached"
        )
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun initialization() {
        Log.d(currentFragmentTag, getString(R.string.init_log))

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity)

        initViews()
        initTools()
        initListeners()
    }

    override fun setContent() {
        Log.d(currentFragmentTag, getString(R.string.content_log))

        checkSharedPreferences()
        finishingSetContent()
    }

    override fun initViews() {
        Log.d(currentFragmentTag, getString(R.string.initviews_log))
    }

    override fun initTools() {
        Log.d(currentFragmentTag, getString(R.string.inittools_log))
        mDatabaseHelper = WeatherDatabaseHelper(
                mActivity,
                WeatherDatabaseHelper.DATABASE_NAME,
                null,
                WeatherDatabaseHelper.DATABASE_VERSION
        )
        mSqLiteDatabase = mDatabaseHelper.writableDatabase
    }

    override fun onClick(btn: String) {
        Log.d(currentFragmentTag, "Clicked on $btn")
    }

    override fun refreshFragment() {
        Log.d(currentFragmentTag, "Refreshing the $currentFragmentTag")

        val ft = fragmentManager!!.beginTransaction()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ft.setReorderingAllowed(false)
        }
        ft.detach(this).attach(this).commit()
    }

    override fun initListeners() {
        Log.d(currentFragmentTag, getString(R.string.initlisteners_log))
    }

    override fun checkSharedPreferences() {
        Log.d(currentFragmentTag, getString(R.string.checksharedpreferences_log))
    }

    override fun finishingSetContent() {
        Log.d(currentFragmentTag, getString(R.string.finishingsetcontent_log))
    }

    companion object {
        const val TAG = "BaseFragment"
    }
}