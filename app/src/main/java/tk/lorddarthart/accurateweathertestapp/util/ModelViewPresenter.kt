package tk.lorddarthart.accurateweathertestapp.util

import android.view.MenuItem

interface ModelViewPresenter {
    interface Model

    interface FragmentView {
        fun initViews()
        fun initVariables()
        fun setContent()
        fun animateFab()
        fun onClick()
        fun hideSoftKeyboard()
        fun getQuery(): String
        fun showLoading()
        suspend fun netOps(mCity: String, latitude: String, longitude: String)
        fun syncResult()
    }

    interface MainActivityView {
        fun initViews()
        fun initAnimations()
        fun setContent()
        fun animateFab()
        fun optionItemSelected(item: MenuItem)
    }
}