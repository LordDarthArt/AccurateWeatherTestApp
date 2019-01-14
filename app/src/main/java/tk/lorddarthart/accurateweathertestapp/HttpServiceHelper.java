package tk.lorddarthart.accurateweathertestapp;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class HttpServiceHelper {

    public int getTasks(SQLiteDatabase mSqLiteDatabase, String city) throws IOException, JSONException, ParseException {
        String url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22"+city+"%22)%20and%20u%3D'c'&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");

        con.setRequestProperty("connection", "keep-alive");
        con.setRequestProperty("content-type", "application/octet-stream");
        con.setConnectTimeout(10000);
        con.setReadTimeout(10000);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        if (!(responseCode == 401)) {
            InputStream inputStream = con.getInputStream();
            String stringResponse = imputStreamToString(inputStream);

            List<Weather> weathers = readWeatherArray(stringResponse, city);
            System.out.println(weathers);
            if (weathers!=null) {
                for (Weather weather : weathers) {
                    WeatherDatabaseHelper.addWeather(mSqLiteDatabase, weather.getWeather_date(), weather.getWeather_filterName(), weather.getWeather_now(), weather.getWeather_city(),
                            weather.getWeather_high(), weather.getWeather_low(), weather.getWeather_text(), weather.getWeather_description(), weather.getWeather_humidity(),
                            weather.getWeather_pressure(), weather.getWeather_rising(), weather.getWeather_visibility(), weather.getWeather_sunrise(), weather.getWeather_sunset(),
                            weather.getWeather_d1(), weather.getWeather_d2(), weather.getWeather_d3(), weather.getWeather_d4(), weather.getWeather_d5(),
                            weather.getWeather_d6(), weather.getWeather_d7());
                }
            }
        }
        return responseCode;
    }

    public Weather readTask(String stringResponse, String filterName) throws JSONException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-3"));
        long weather_date = sdf.parse(new JSONObject(stringResponse).getJSONObject("query").get("created").toString()).getTime();
        double weather_now = Double.parseDouble((String)new JSONObject(stringResponse).getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("item").getJSONObject("condition").get("temp"));
        String weather_city = (String) new JSONObject(stringResponse).getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("location").get("city");
        String weather_text = (String) new JSONObject(stringResponse).getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("item").getJSONObject("condition").get("text");
        String weather_description = (String) new JSONObject(stringResponse).getJSONObject("query").getJSONObject("results").getJSONObject("channel").get("description");
        double weather_humidity=Double.parseDouble((String)new JSONObject(stringResponse).getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("atmosphere").get("humidity"));
        double weather_pressure=Double.parseDouble((String)new JSONObject(stringResponse).getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("atmosphere").get("pressure"));
        long weather_rising=Long.parseLong((String)new JSONObject(stringResponse).getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("atmosphere").get("rising"));
        double weather_visibility=Double.parseDouble((String)new JSONObject(stringResponse).getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("atmosphere").get("visibility"));
        double weather_high = Double.parseDouble((String)new JSONObject(stringResponse).getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("item").getJSONArray("forecast").getJSONObject(0).get("high"));
        double weather_low = Double.parseDouble((String)new JSONObject(stringResponse).getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("item").getJSONArray("forecast").getJSONObject(0).get("low"));
        String weather_sunrise = new SimpleDateFormat("HH:mm").format(new SimpleDateFormat("KK:mm a", Locale.US).parse(new JSONObject(stringResponse).getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("astronomy").get("sunrise").toString().toUpperCase()));;
        String weather_sunset = new SimpleDateFormat("HH:mm").format(new SimpleDateFormat("KK:mm a", Locale.US).parse(new JSONObject(stringResponse).getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("astronomy").get("sunset").toString().toUpperCase()));
        String weather_d1 = addWeatherDay(stringResponse, 0);
        String weather_d2 = addWeatherDay(stringResponse, 1);
        String weather_d3 = addWeatherDay(stringResponse, 2);
        String weather_d4 = addWeatherDay(stringResponse, 3);
        String weather_d5 = addWeatherDay(stringResponse, 4);
        String weather_d6 = addWeatherDay(stringResponse, 5);
        String weather_d7 = addWeatherDay(stringResponse, 6);

        return new Weather(weather_date, filterName, weather_now, weather_city,  weather_high, weather_low, weather_text, weather_description, weather_humidity, weather_pressure,weather_rising, weather_visibility, weather_sunrise, weather_sunset,
                weather_d1, weather_d2, weather_d3, weather_d4, weather_d5, weather_d6, weather_d7);
    }

    private String imputStreamToString(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString("UTF-8");
        }
    }

    private String addWeatherDay(String stringResponse, int i) {
        try {
            LinkedList<WeatherDay> weather_d1_list = new LinkedList<>();
            weather_d1_list.add(new WeatherDay((String) new JSONObject(stringResponse).getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("item").getJSONArray("forecast").getJSONObject(i).get("day"),
                    Double.parseDouble((String) new JSONObject(stringResponse).getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("item").getJSONArray("forecast").getJSONObject(i).get("high")),
                    Double.parseDouble((String) new JSONObject(stringResponse).getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("item").getJSONArray("forecast").getJSONObject(i).get("low")),
                    (String) new JSONObject(stringResponse).getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("item").getJSONArray("forecast").getJSONObject(i).get("text")));
            String weather = weather_d1_list.toString();
            return weather;
        } catch (Exception e) {
            System.out.println();
        }
        return "";
    }

    private List<Weather> readWeatherArray(String array, String city) throws IOException, JSONException, ParseException {
        List<Weather> tasks = new ArrayList<>();


        tasks.add(readTask(array, city));
        return tasks;
    }
}
