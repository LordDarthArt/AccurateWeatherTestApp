package tk.lorddarthart.accurateweathertestapp.application.view.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import tk.lorddarthart.accurateweathertestapp.R
import tk.lorddarthart.accurateweathertestapp.application.view.base.BaseActivity
import tk.lorddarthart.accurateweathertestapp.application.view.fragment.MainFragment
import tk.lorddarthart.accurateweathertestapp.util.ModelViewPresenter

class MainActivity : BaseActivity(), ModelViewPresenter.MainActivityView {

    lateinit var mFragment: MainFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFragment()
        setContent()
    }

    override fun initFragment() {
        mFragment = MainFragment()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_setcity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when (it.itemId) {
                R.id.action_setcity -> mFragment.optionItemSelected(it)
                else -> return false
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setContent() {
        val mainFragment = R.id.mainFragment
        supportFragmentManager.beginTransaction().replace(mainFragment, mFragment).commit()
    }
}
