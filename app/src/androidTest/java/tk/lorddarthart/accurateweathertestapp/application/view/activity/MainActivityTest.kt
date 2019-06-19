package tk.lorddarthart.accurateweathertestapp.application.view.activity

import android.support.design.widget.FloatingActionButton
import android.support.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import android.support.test.runner.AndroidJUnit4
import android.view.MenuItem
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnitRunner
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import tk.lorddarthart.accurateweathertestapp.R
import tk.lorddarthart.accurateweathertestapp.application.view.fragment.MainFragment

@RunWith(AndroidJUnit4::class)
class MainActivityTest : AndroidJUnitRunner() {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    private var mActivity: MainActivity? = null
    private var mFragment: MainFragment? = null
    private var mFloatingActionButton: FloatingActionButton? = null
    private var mSetCity: MenuItem? = null
    private var mainFragment: Int? = null

    @Before
    fun setUp() {
        mActivity = mActivityTestRule.activity
        mActivity?.let { it ->
            mFragment = it.mFragment
            mFloatingActionButton = it.findViewById(R.id.floatingActionButton)
            mSetCity = it.mSetCity
            mainFragment = it.mainFragment
        }
    }

    @Test
    fun setFloatingActionButton() {
        mFloatingActionButton = mActivity!!.findViewById(R.id.floatingActionButton)
    }

    @Test
    fun getFloatingActionButton() {
        assertNotNull(mFloatingActionButton)
    }

    @Test
    fun setSetCity() {
        mSetCity = mActivity!!.mSetCity
    }

    @Test
    fun getSetCity() {
        assertNotNull(mSetCity)
    }

    @Test
    fun setMainFragment() {
        mActivity!!.setMainFragment()
    }

    @Test
    fun onCreate() {
        assertNotNull(mainFragment)
    }

    @Test
    fun displayHomeAsUpEnabled() {
        runOnUiThread {
            mActivity!!.displayHomeAsUpEnabled(true)
        }
    }

    @Test
    fun setActionBarTitle() {
        runOnUiThread {
            mActivity!!.setActionBarTitle("Test")
        }
    }

    @Test
    fun setFabVisibility() {
        runOnUiThread {
            mFloatingActionButton!!.show()
        }
    }

    @Test
    fun initFragment() {
        mFragment = MainFragment.newInstance()
    }

    @Test
    fun onCreateOptionsMenu() {
        runOnUiThread {
            mActivity!!.openOptionsMenu()
        }
    }

    @Test
    fun onOptionsItemSelected() {
        runOnUiThread {
            mActivity!!.onOptionsItemSelected(mSetCity)
        }
    }

    @Test
    fun setContent() {
        mActivity!!.supportFragmentManager.beginTransaction()
                .replace(mainFragment!!, mFragment!!).commitAllowingStateLoss()
    }

    @Test
    fun onBackPressed() {
        mActivity!!.finishAffinity()
    }

    @After
    fun tearDown() {
        mActivity = null
        mFragment = null
        mainFragment = null
        mSetCity = null
        mFloatingActionButton = null
    }
}