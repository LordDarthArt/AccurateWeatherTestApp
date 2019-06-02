package tk.lorddarthart.accurateweathertestapp.model

class WeatherModel {
    var weatherDate: Long = 0
    var weatherFilterName: String? = null
    var weatherNow: Double = 0.toDouble()
    var weatherCity: String? = null
    var weatherHigh: Double = 0.toDouble()
    var weatherLow: Double = 0.toDouble()
    var weatherText: String? = null
    var weatherDescription: String? = null
    var mWeatherHumidity: Double = 0.toDouble()
    var mWeatherPressure: Double = 0.toDouble()
    var mWeatherSunrise: String? = null
    var mWeatherSunset: String? = null
    var mWeatherDay1: String? = null
    var mWeatherDay2: String? = null
    var mWeatherDay3: String? = null
    var mWeatherDay4: String? = null
    var mWeatherDay5: String? = null
    var mWeatherDay6: String? = null
    var mWeatherDay7: String? = null

    constructor()

    constructor(weatherDate: Long, weatherFilterName: String?, weatherNow: Double, weatherCity: String?, weatherHigh: Double, weatherLow: Double, weatherText: String?, weatherDescription: String?, mWeatherHumidity: Double, mWeatherPressure: Double, mWeatherSunrise: String?, mWeatherSunset: String?, mWeatherDay1: String?, mWeatherDay2: String?, mWeatherDay3: String?, mWeatherDay4: String?, mWeatherDay5: String?, mWeatherDay6: String?, mWeatherDay7: String?) {
        this.weatherDate = weatherDate
        this.weatherFilterName = weatherFilterName
        this.weatherNow = weatherNow
        this.weatherCity = weatherCity
        this.weatherHigh = weatherHigh
        this.weatherLow = weatherLow
        this.weatherText = weatherText
        this.weatherDescription = weatherDescription
        this.mWeatherHumidity = mWeatherHumidity
        this.mWeatherPressure = mWeatherPressure
        this.mWeatherSunrise = mWeatherSunrise
        this.mWeatherSunset = mWeatherSunset
        this.mWeatherDay1 = mWeatherDay1
        this.mWeatherDay2 = mWeatherDay2
        this.mWeatherDay3 = mWeatherDay3
        this.mWeatherDay4 = mWeatherDay4
        this.mWeatherDay5 = mWeatherDay5
        this.mWeatherDay6 = mWeatherDay6
        this.mWeatherDay7 = mWeatherDay7
    }
}
