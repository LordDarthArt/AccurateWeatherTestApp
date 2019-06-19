package tk.lorddarthart.accurateweathertestapp.application.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import tk.lorddarthart.accurateweathertestapp.R
import tk.lorddarthart.accurateweathertestapp.application.view.base.BaseActivity
import tk.lorddarthart.accurateweathertestapp.application.view.fragment.MainFragment
import tk.lorddarthart.accurateweathertestapp.util.IOnBackPressed

class MainActivity : BaseActivity() {

    lateinit var mFragment: MainFragment
    lateinit var mFloatingActionButton: FloatingActionButton
    lateinit var mSetCity: MenuItem
    var mainFragment: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setMainFragment()
        super.onCreate(savedInstanceState)
        mFloatingActionButton = findViewById(R.id.floatingActionButton)
    }

    fun displayHomeAsUpEnabled(state: Boolean) {
        this.supportActionBar?.setDisplayHomeAsUpEnabled(state)
    }

    fun setActionBarTitle(title: String) {
        this.supportActionBar?.title = title
    }

    fun setFabVisibility(state: Boolean) {
        if (state) {
            mFloatingActionButton.show()
        } else {
            mFloatingActionButton.hide()
        }
    }

    fun setMainFragment() {
        mainFragment = R.id.mainFragment
    }

    override fun initFragment() {
        mFragment = MainFragment.newInstance()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_setcity, menu)
        mSetCity = menu.findItem(R.id.action_setcity)
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("RestrictedApi")
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when (it.itemId) {
                mSetCity.itemId -> mFragment.optionItemSelected()
                android.R.id.home -> {
                    supportFragmentManager.popBackStack()
                    setActionBarTitle(getString(R.string.app_name))
                    mSetCity.isVisible = true
                    displayHomeAsUpEnabled(false)
                    mFloatingActionButton.visibility = View.VISIBLE
                }
                else -> return false
            }
        }
        return super.onOptionsItemSelected(item!!)
    }

    override fun onBackPressed() {
        val fragment =
                this.supportFragmentManager.findFragmentById(R.id.mainFragment)
        try {
            (fragment as IOnBackPressed).onBackPressed()
        } catch (e: Exception) {
            super.onBackPressed()
        }
    }

    override fun setContent() {
        supportFragmentManager.beginTransaction()
                .replace(mainFragment!!, mFragment).commitAllowingStateLoss()
    }
}
