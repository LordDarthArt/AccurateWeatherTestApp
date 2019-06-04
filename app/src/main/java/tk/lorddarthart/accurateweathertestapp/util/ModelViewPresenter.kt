package tk.lorddarthart.accurateweathertestapp.util

import android.view.MenuItem

interface ModelViewPresenter {
    interface Model

    interface FragmentView {
        fun initViews()
        fun initAnimations()
        fun initLists()
        fun initialization()
        fun setContent()
        fun animateFab()
        fun onClick()
        fun hideSoftKeyboard()
        fun getQuery(): String
        fun showLoading()
        suspend fun netOps(mCity: String, latitude: String, longitude: String)
        fun syncResult()
        fun optionItemSelected(item: MenuItem)
    }

    interface MainActivityView {
        fun initFragment()
        fun setContent()
    }
}