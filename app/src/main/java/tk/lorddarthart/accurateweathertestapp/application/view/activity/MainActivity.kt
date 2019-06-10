package tk.lorddarthart.accurateweathertestapp.application.view.activity

import android.view.Menu
import android.view.MenuItem
import tk.lorddarthart.accurateweathertestapp.R
import tk.lorddarthart.accurateweathertestapp.application.view.base.BaseActivity
import tk.lorddarthart.accurateweathertestapp.application.view.fragment.MainFragment

class MainActivity : BaseActivity() {

    private lateinit var mFragment: MainFragment

    override fun initFragment() {
        mFragment = MainFragment.newInstance()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_setcity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when (it.itemId) {
                R.id.action_setcity -> mFragment.optionItemSelected()
                else -> return false
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setContent() {
        val mainFragment = R.id.mainFragment
        supportFragmentManager.beginTransaction()
                .replace(mainFragment, mFragment).commitAllowingStateLoss()
    }
}
