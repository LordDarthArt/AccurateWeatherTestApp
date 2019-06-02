package tk.lorddarthart.accurateweathertestapp.model

class WeatherDayModel {
    var weather_day: String? = null
    var weather_hight: Double? = null
    var weather_low: Double? = null
    var weather_text: String? = null

    constructor(weather_day: String, weather_hight: Double?, weather_low: Double?, weather_text: String) {
        this.weather_day = weather_day
        this.weather_hight = weather_hight
        this.weather_low = weather_low
        this.weather_text = weather_text
    }

    constructor() {

    }
}
