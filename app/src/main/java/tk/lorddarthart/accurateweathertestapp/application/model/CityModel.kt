package tk.lorddarthart.accurateweathertestapp.application.model

import tk.lorddarthart.accurateweathertestapp.util.ModelViewPresenter

class CityModel: ModelViewPresenter.Model {
    var mId: Int? = null
    var mCityName: String? = null
    var mLatitude: String? = null
    var mLongitude: String? = null

    constructor(mId: Int?, mCityName: String?, mLatitude: String?, mLongitude: String?) {
        this.mId = mId
        this.mCityName = mCityName
        this.mLatitude = mLatitude
        this.mLongitude = mLongitude
    }

    constructor(mId: Int?, mCityName: String) {
        this.mId = mId
        this.mCityName = mCityName
    }

    constructor()
}
