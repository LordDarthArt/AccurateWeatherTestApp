package tk.lorddarthart.accurateweathertestapp.application.view.activity

import android.view.Menu
import android.view.MenuItem
import tk.lorddarthart.accurateweathertestapp.R
import tk.lorddarthart.accurateweathertestapp.application.view.base.BaseActivity
import tk.lorddarthart.accurateweathertestapp.application.view.fragment.MainFragment
import tk.lorddarthart.accurateweathertestapp.util.IOnBackPressed
import java.lang.Exception

class MainActivity : BaseActivity() {

    private lateinit var mFragment: MainFragment
    lateinit var mSetCity: MenuItem

    fun displayHomeAsUpEnabled(state: Boolean) {
        this.supportActionBar?.setDisplayHomeAsUpEnabled(state)
    }

    fun setActionBarTitle(title: String) {
        this.supportActionBar?.title = title
    }

    override fun initFragment() {
        mFragment = MainFragment.newInstance()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_setcity, menu)
        mSetCity = menu.findItem(R.id.action_setcity)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when (it.itemId) {
                mSetCity.itemId -> mFragment.optionItemSelected()
                android.R.id.home -> {
                    supportFragmentManager.popBackStack()
                    setActionBarTitle(getString(R.string.app_name))
                    mSetCity.isVisible  = true
                    displayHomeAsUpEnabled(false)
                }
                else -> return false
            }
        }
        return super.onOptionsItemSelected(item)
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
        val mainFragment = R.id.mainFragment
        supportFragmentManager.beginTransaction()
                .replace(mainFragment, mFragment).commitAllowingStateLoss()
    }
}
