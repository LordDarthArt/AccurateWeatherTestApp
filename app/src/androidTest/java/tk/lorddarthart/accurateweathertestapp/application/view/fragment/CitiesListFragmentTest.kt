package tk.lorddarthart.accurateweathertestapp.application.view.fragment

import android.os.Handler
import android.support.test.runner.AndroidJUnitRunner
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import tk.lorddarthart.accurateweathertestapp.FragmentTestRule
import tk.lorddarthart.accurateweathertestapp.util.adapter.CitiesListAdapter

class CitiesListFragmentTest : AndroidJUnitRunner() {

    @Rule
    @JvmField
    var mFragmentRule = FragmentTestRule(CitiesListFragment::class.java)

    private var mFragment: CitiesListFragment? = null
    private var mButtonApply: TextView? = null
    private var mCitiesListRecycler: RecyclerView? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var mCitiesListAdapter: CitiesListAdapter? = null
    private var mCitiesListBackground: ImageView? = null
    private val changes = arrayOf(0)
    private var currentFragmentTag: String? = null

    @Before
    fun setUp() {
        mFragmentRule.launchActivity(null)
        mButtonApply = (mFragmentRule.fragment as CitiesListFragment).mButtonApply
        mCitiesListAdapter = (mFragmentRule.fragment as CitiesListFragment).mCitiesListAdapter
        mCitiesListRecycler = (mFragmentRule.fragment as CitiesListFragment).mCitiesListRecycler
        mLayoutManager = (mFragmentRule.fragment as CitiesListFragment).mLayoutManager
        mCitiesListBackground = (mFragmentRule.fragment as CitiesListFragment).mCitiesListBackground
    }

    @Test
    fun onCreate() {
        currentFragmentTag = CitiesListFragment.TAG
    }

    @Test
    fun onCreateView() {

    }

    @Test
    fun initViews() {
        assertNotNull(mButtonApply)
        assertNotNull(mCitiesListRecycler)
        assertNotNull(mCitiesListBackground)
    }

    @Test
    fun initTools() {
    }

    @Test
    fun initListeners() {
    }

    @Test
    fun onApplySettings() {
    }

    @Test
    fun onBackPressed() {
    }

    @Test
    fun newInstance() {
    }

    @After
    fun tearDown() {
    }
}