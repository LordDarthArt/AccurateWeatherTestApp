package tk.lorddarthart.accurateweathertestapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class WeatherDatabaseHelper extends SQLiteOpenHelper implements BaseColumns {
    public static final String DATABASE_NAME = "tk.lorddarthart.accurateweathertestappweather.db";
    public static int DATABASE_VERSION = 1;

    public static final String DATABASE_WEATHER = "weather";
    public static final String WEATHER_FILTERNAME = "weather_filterName";
    public static final String WEATHER_ID = "weather_id";
    public static final String WEATHER_NOW = "weather_now";
    public static final String WEATHER_DATE = "weather_date";
    public static final String WEATHER_CITY = "weather_city";
    public static final String WEATHER_HIGH = "weather_high";
    public static final String WEATHER_LOW = "weather_low";
    public static final String WEATHER_TEXT = "weather_text";
    public static final String WEATHER_DESCRIPTION = "weather_description";
    public static final String WEATHER_HUMIDITY = "weather_humidity";
    public static final String WEATHER_PRESSURE = "weather_pressure";
    public static final String WEATHER_SUNRISE = "weather_sunrise";
    public static final String WEATHER_SUNSET = "weather_sunset";
    public static final String WEATHER_D1 = "weather_d1";
    public static final String WEATHER_D2 = "weather_d2";
    public static final String WEATHER_D3 = "weather_d3";
    public static final String WEATHER_D4 = "weather_d4";
    public static final String WEATHER_D5 = "weather_d5";
    public static final String WEATHER_D6 = "weather_d6";
    public static final String WEATHER_D7 = "weather_d7";

    public static final String DATABASE_WEATHER_CITY = "city";
    public static final String WEATHER_CITY_ID = "city_id";
    public static final String WEATHER_CITY_FILTERNAME = "city_name";
    public static final String WEATHER_CITY_LATITUDE = "city_latitude";
    public static final String WEATHER_CITY_LONGITUDE = "city_longitude";

    public static final String DATABASE_CREATE_WEATHER_CITY_SCRIPT = "create table "
            + DATABASE_WEATHER_CITY
            + " (" + WEATHER_CITY_ID + " integer not null primary key autoincrement, "
            + WEATHER_CITY_FILTERNAME + " text not null, "
            + WEATHER_CITY_LATITUDE + " text not null, "
            + WEATHER_CITY_LONGITUDE + " text not null, "
            + "UNIQUE(" + WEATHER_CITY_FILTERNAME + ") ON CONFLICT REPLACE);";

    public static final String DATABASE_CREATE_WEATHER_SCRIPT = "create table "
            + DATABASE_WEATHER
            + " (" + WEATHER_ID + " integer not null primary key autoincrement, "
            + WEATHER_FILTERNAME + " text not null, "
            + WEATHER_DATE + " long not null, "
            + WEATHER_CITY + " text not null, "
            + WEATHER_NOW + " double not null, "
            + WEATHER_HIGH + " double not null, "
            + WEATHER_LOW + " double not null, "
            + WEATHER_TEXT + " text not null, "
            + WEATHER_DESCRIPTION + " text not null, "
            + WEATHER_HUMIDITY + " double not null, "
            + WEATHER_PRESSURE + " double not null, "
            + WEATHER_SUNRISE + " text not null, "
            + WEATHER_SUNSET + " text not null, "
            + WEATHER_D1 + " text not null, "
            + WEATHER_D2 + " text not null, "
            + WEATHER_D3 + " text not null, "
            + WEATHER_D4 + " text not null, "
            + WEATHER_D5 + " text not null, "
            + WEATHER_D6 + " text not null, "
            + WEATHER_D7 + " text not null, "
            + "UNIQUE(" + WEATHER_CITY + ") ON CONFLICT REPLACE);";

    WeatherDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public WeatherDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_WEATHER_SCRIPT);
        db.execSQL(DATABASE_CREATE_WEATHER_CITY_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public static void addWeather(SQLiteDatabase mSqLiteDatabase, long weather_date, String weather_filterName, double weather_now, String weather_city, double weather_high,
                                  double weather_low, String weather_text, String weather_description, double weather_humidity, double weather_pressure, String weather_sunrise, String weather_sunset, String weather_d1, String weather_d2, String weather_d3,
                                  String weather_d4, String weather_d5, String weather_d6, String weather_d7) {

        ContentValues newValues = new ContentValues();
        newValues.put(WeatherDatabaseHelper.WEATHER_DATE, weather_date);
        newValues.put(WeatherDatabaseHelper.WEATHER_FILTERNAME, weather_filterName);
        newValues.put(WeatherDatabaseHelper.WEATHER_NOW, weather_now);
        newValues.put(WeatherDatabaseHelper.WEATHER_CITY, weather_city);
        newValues.put(WeatherDatabaseHelper.WEATHER_HIGH, weather_high);
        newValues.put(WeatherDatabaseHelper.WEATHER_LOW, weather_low);
        newValues.put(WeatherDatabaseHelper.WEATHER_TEXT, weather_text);
        newValues.put(WeatherDatabaseHelper.WEATHER_HUMIDITY, weather_humidity);
        newValues.put(WeatherDatabaseHelper.WEATHER_PRESSURE, weather_pressure);
        newValues.put(WeatherDatabaseHelper.WEATHER_DESCRIPTION, weather_description);
        newValues.put(WeatherDatabaseHelper.WEATHER_SUNRISE, weather_sunrise);
        newValues.put(WeatherDatabaseHelper.WEATHER_SUNSET, weather_sunset);
        newValues.put(WeatherDatabaseHelper.WEATHER_D1, weather_d1);
        newValues.put(WeatherDatabaseHelper.WEATHER_D2, weather_d2);
        newValues.put(WeatherDatabaseHelper.WEATHER_D3, weather_d3);
        newValues.put(WeatherDatabaseHelper.WEATHER_D4, weather_d4);
        newValues.put(WeatherDatabaseHelper.WEATHER_D5, weather_d5);
        newValues.put(WeatherDatabaseHelper.WEATHER_D6, weather_d6);
        newValues.put(WeatherDatabaseHelper.WEATHER_D7, weather_d7);

        mSqLiteDatabase.insertWithOnConflict(WeatherDatabaseHelper.DATABASE_WEATHER, null, newValues, SQLiteDatabase.CONFLICT_REPLACE);
    }
}
