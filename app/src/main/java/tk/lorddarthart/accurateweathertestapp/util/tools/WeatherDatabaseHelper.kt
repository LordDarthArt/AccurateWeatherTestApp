package tk.lorddarthart.accurateweathertestapp.util.tools

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class WeatherDatabaseHelper : SQLiteOpenHelper, BaseColumns {

    internal constructor(context: Context) : super(context, DATABASE_NAME, null, DATABASE_VERSION) {}

    constructor(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(context, name, factory, version) {}

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(DATABASE_CREATE_WEATHER_SCRIPT)
        db.execSQL(DATABASE_CREATE_WEATHER_CITY_SCRIPT)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {

    }

    companion object {
        const val DATABASE_NAME = "tk.lorddarthart.accurateweathertestappweather.db"
        var DATABASE_VERSION = 1

        const val DATABASE_WEATHER = "weather"
        const val WEATHER_FILTERNAME = "weatherFilterName"
        const val WEATHER_ID = "weather_id"
        const val WEATHER_NOW = "weatherNow"
        const val WEATHER_DATE = "weatherDate"
        const val WEATHER_CITY = "weatherCity"
        const val WEATHER_HIGH = "weatherHigh"
        const val WEATHER_LOW = "weatherLow"
        const val WEATHER_TEXT = "weatherText"
        const val WEATHER_DESCRIPTION = "weatherDescription"
        const val WEATHER_HUMIDITY = "mWeatherHumidity"
        const val WEATHER_PRESSURE = "mWeatherPressure"
        const val WEATHER_SUNRISE = "mWeatherSunrise"
        const val WEATHER_SUNSET = "mWeatherSunset"
        const val WEATHER_D1 = "mWeatherDay1"
        const val WEATHER_D2 = "mWeatherDay2"
        const val WEATHER_D3 = "mWeatherDay3"
        const val WEATHER_D4 = "mWeatherDay4"
        const val WEATHER_D5 = "mWeatherDay5"
        const val WEATHER_D6 = "mWeatherDay6"
        const val WEATHER_D7 = "mWeatherDay7"

        const val DATABASE_WEATHER_CITY = "city"
        const val WEATHER_CITY_ID = "city_id"
        const val WEATHER_CITY_FILTERNAME = "city_name"
        const val WEATHER_CITY_LATITUDE = "city_latitude"
        const val WEATHER_CITY_LONGITUDE = "city_longitude"

        const val DATABASE_CREATE_WEATHER_CITY_SCRIPT = ("create table "
                + DATABASE_WEATHER_CITY
                + " (" + WEATHER_CITY_ID + " integer not null primary key autoincrement, "
                + WEATHER_CITY_FILTERNAME + " text not null, "
                + WEATHER_CITY_LATITUDE + " text not null, "
                + WEATHER_CITY_LONGITUDE + " text not null, "
                + "UNIQUE(" + WEATHER_CITY_FILTERNAME + ") ON CONFLICT REPLACE);")

        const val DATABASE_CREATE_WEATHER_SCRIPT = ("create table "
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
                + "UNIQUE(" + WEATHER_CITY + ") ON CONFLICT REPLACE);")

        fun addWeather(mSqLiteDatabase: SQLiteDatabase, weather_date: Long, weather_filterName: String, weather_now: Double, weather_city: String, weather_high: Double,
                       weather_low: Double, weather_text: String, weather_description: String, weather_humidity: Double, weather_pressure: Double, weather_sunrise: String, weather_sunset: String, weather_d1: String, weather_d2: String, weather_d3: String,
                       weather_d4: String, weather_d5: String, weather_d6: String, weather_d7: String) {

            val newValues = ContentValues()
            newValues.put(WEATHER_DATE, weather_date)
            newValues.put(WEATHER_FILTERNAME, weather_filterName)
            newValues.put(WEATHER_NOW, weather_now)
            newValues.put(WEATHER_CITY, weather_city)
            newValues.put(WEATHER_HIGH, weather_high)
            newValues.put(WEATHER_LOW, weather_low)
            newValues.put(WEATHER_TEXT, weather_text)
            newValues.put(WEATHER_HUMIDITY, weather_humidity)
            newValues.put(WEATHER_PRESSURE, weather_pressure)
            newValues.put(WEATHER_DESCRIPTION, weather_description)
            newValues.put(WEATHER_SUNRISE, weather_sunrise)
            newValues.put(WEATHER_SUNSET, weather_sunset)
            newValues.put(WEATHER_D1, weather_d1)
            newValues.put(WEATHER_D2, weather_d2)
            newValues.put(WEATHER_D3, weather_d3)
            newValues.put(WEATHER_D4, weather_d4)
            newValues.put(WEATHER_D5, weather_d5)
            newValues.put(WEATHER_D6, weather_d6)
            newValues.put(WEATHER_D7, weather_d7)

            mSqLiteDatabase.insertWithOnConflict(DATABASE_WEATHER, null, newValues, SQLiteDatabase.CONFLICT_REPLACE)
        }
    }
}
