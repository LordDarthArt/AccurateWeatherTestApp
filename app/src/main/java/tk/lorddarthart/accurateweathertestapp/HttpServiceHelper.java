package tk.lorddarthart.accurateweathertestapp;

import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class HttpServiceHelper {

    public int getForecast(SQLiteDatabase mSqLiteDatabase, String city, String latitude, String longitude) throws IOException, JSONException {
        String url = "https://api.weather.yandex.ru/v1/forecast?lat="+latitude+"&lon="+longitude+"&lang=ru_RU";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");

        con.setRequestProperty("connection", "keep-alive");
        con.setRequestProperty("content-type", "application/octet-stream");
        con.setRequestProperty("X-Yandex-API-Key", "0b7449ae-2807-4618-9afe-6781f963e8a4");
        con.setConnectTimeout(10000);
        con.setReadTimeout(10000);

        int responseCode = con.getResponseCode();

        if (!(responseCode == 401)) {
            InputStream inputStream = con.getInputStream();
            String stringResponse = inputStreamToString(inputStream);

            List<Weather> weathers = readWeatherArray(stringResponse, city);
            System.out.println(weathers);
            if (weathers!=null) {
                for (Weather weather : weathers) {
                    WeatherDatabaseHelper.addWeather(mSqLiteDatabase, weather.getWeather_date(), weather.getWeather_filterName(), weather.getWeather_now(), weather.getWeather_city(),
                            weather.getWeather_high(), weather.getWeather_low(), weather.getWeather_text(), weather.getWeather_description(), weather.getWeather_humidity(),
                            weather.getWeather_pressure(), weather.getWeather_sunrise(), weather.getWeather_sunset(),
                            weather.getWeather_d1(), weather.getWeather_d2(), weather.getWeather_d3(), weather.getWeather_d4(), weather.getWeather_d5(),
                            weather.getWeather_d6(), weather.getWeather_d7());
                }
            }
        }
        return responseCode;
    }

    public Weather readWeather(String stringResponse, String filterName) throws JSONException {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String dayofweek = getDayOfWeek(day);
        long weather_date = Long.valueOf((Integer)new JSONObject(stringResponse).get("now"))*1000;
        double weather_now = Double.parseDouble(String.valueOf(new JSONObject(stringResponse).getJSONObject("fact").get("temp")));
        String weather_city = filterName;
        String weather_text = dayofweek;
        String weather_description = (String) new JSONObject(stringResponse).getJSONObject("fact").get("condition");
        double weather_humidity=Double.valueOf((int)new JSONObject(stringResponse).getJSONObject("fact").get("humidity"));
        double weather_pressure=Double.valueOf((int)new JSONObject(stringResponse).getJSONObject("fact").get("pressure_mm"));
        double weather_high = Double.valueOf((int)new JSONObject(stringResponse).getJSONArray("forecasts").getJSONObject(0).getJSONObject("parts").getJSONObject("day").get("temp_max"));
        double weather_low = Double.valueOf((int)new JSONObject(stringResponse).getJSONArray("forecasts").getJSONObject(0).getJSONObject("parts").getJSONObject("day").get("temp_min"));
        String weather_sunrise = (String)new JSONObject(stringResponse).getJSONArray("forecasts").getJSONObject(0).get("sunrise");
        String weather_sunset = (String)new JSONObject(stringResponse).getJSONArray("forecasts").getJSONObject(0).get("sunset");
        String weather_d1 = addWeatherDay(stringResponse, 0);
        String weather_d2 = addWeatherDay(stringResponse, 1);
        String weather_d3 = addWeatherDay(stringResponse, 2);
        String weather_d4 = addWeatherDay(stringResponse, 3);
        String weather_d5 = addWeatherDay(stringResponse, 4);
        String weather_d6 = addWeatherDay(stringResponse, 5);
        String weather_d7 = addWeatherDay(stringResponse, 6);

        return new Weather(weather_date, filterName, weather_now, weather_city,  weather_high, weather_low, weather_text, weather_description, weather_humidity, weather_pressure, weather_sunrise, weather_sunset,
                weather_d1, weather_d2, weather_d3, weather_d4, weather_d5, weather_d6, weather_d7);
    }

    private String inputStreamToString(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString("UTF-8");
        }
    }

    private String getDayOfWeek(int day) {
        switch (day) {
            case Calendar.MONDAY: {
                return "Понедельник";
            }

            case Calendar.TUESDAY: {
                return "Вторник";
            }

            case Calendar.WEDNESDAY: {
                return "Среда";
            }

            case Calendar.THURSDAY: {
                return "Четверг";
            }

            case Calendar.FRIDAY: {
                return "Пятница";
            }

            case Calendar.SATURDAY: {
                return "Суббота";
            }

            case Calendar.SUNDAY: {
                return "Воскресенье";
            }
        }
        return "";
    }

    private String addWeatherDay(String stringResponse, int i) {
        try {
            Date d = new Date((long)((Integer)new JSONObject(stringResponse).getJSONArray("forecasts").getJSONObject(i).get("date_ts"))*1000);
            SimpleDateFormat sdf2 = new SimpleDateFormat("EEE");
            String dayOfTheWeek = sdf2.format(d);
            LinkedList<WeatherDay> weather_d1_list = new LinkedList<>();
            weather_d1_list.add(new WeatherDay(dayOfTheWeek,
                    Double.parseDouble(String.valueOf((int)new JSONObject(stringResponse).getJSONArray("forecasts").getJSONObject(i).getJSONObject("parts").getJSONObject("day").get("temp_max"))),
                    Double.parseDouble(String.valueOf((int)new JSONObject(stringResponse).getJSONArray("forecasts").getJSONObject(i).getJSONObject("parts").getJSONObject("day").get("temp_min"))),
                    (String) new JSONObject(stringResponse).getJSONArray("forecasts").getJSONObject(i).getJSONObject("parts").getJSONObject("day").get("condition")));
            Gson gson = new Gson();
            String weather = gson.toJson(weather_d1_list);
            return weather;
        } catch (Exception e) {
            System.out.println();
        }
        return "";
    }

    private List<Weather> readWeatherArray(String array, String city) throws JSONException {
        List<Weather> tasks = new ArrayList<>();

        tasks.add(readWeather(array, city));
        return tasks;
    }
}
