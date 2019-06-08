package tk.lorddarthart.accurateweathertestapp.util

interface ModelViewPresenter {
    interface Model

    interface FragmentView {
        fun initialization()
        fun initViews()
        fun initTools()
        fun setContent()
        fun initListeners()
        fun checkSharedPreferences()
        fun finishingSetContent()
        fun onClick(btn: String)
        fun refreshFragment()
    }

    interface MainActivityView {
        fun initFragment()
        fun setContent()
    }
}