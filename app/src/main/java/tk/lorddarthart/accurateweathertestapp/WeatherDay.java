package tk.lorddarthart.accurateweathertestapp;

public class WeatherDay {
    private String weather_day;
    private Double weather_hight;
    private Double weather_low;
    private String weather_text;

    public WeatherDay(String weather_day, Double weather_hight, Double weather_low, String weather_text) {
        this.weather_day = weather_day;
        this.weather_hight = weather_hight;
        this.weather_low = weather_low;
        this.weather_text = weather_text;
    }

    public WeatherDay() {

    }

    public String getWeather_day() {
        return weather_day;
    }

    public void setWeather_day(String weather_day) {
        this.weather_day = weather_day;
    }

    public Double getWeather_hight() {
        return weather_hight;
    }

    public void setWeather_hight(Double weather_hight) {
        this.weather_hight = weather_hight;
    }

    public Double getWeather_low() {
        return weather_low;
    }

    public void setWeather_low(Double weather_low) {
        this.weather_low = weather_low;
    }

    public String getWeather_text() {
        return weather_text;
    }

    public void setWeather_text(String weather_text) {
        this.weather_text = weather_text;
    }
}
