package tk.lorddarthart.accurateweathertestapp.model

class City {
    var id: Int? = null
    var cityName: String? = null
    var latitude: String? = null
    var longitude: String? = null

    constructor(id: Int?, cityName: String, latitude: String, longitude: String) {
        this.id = id
        this.cityName = cityName
        this.latitude = latitude
        this.longitude = longitude
    }

    constructor(id: Int?, cityName: String) {
        this.id = id
        this.cityName = cityName
    }

    constructor() {

    }
}
