package tk.lorddarthart.accurateweathertestapp.application.view.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import tk.lorddarthart.accurateweathertestapp.R
import tk.lorddarthart.accurateweathertestapp.util.ModelViewPresenter

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity(), ModelViewPresenter.MainActivityView {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFragment()
        setContent()
    }

    override fun initFragment() {

    }

    override fun setContent() {

    }

}