package tk.lorddarthart.accurateweathertestapp;

public class City {
    private Integer id;
    private String cityName;
    private String latitude;
    private String longitude;

    public City(Integer id, String cityName, String latitude, String longitude) {
        this.id = id;
        this.cityName = cityName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public City(Integer id, String cityName) {
        this.id = id;
        this.cityName = cityName;
    }

    public City() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
