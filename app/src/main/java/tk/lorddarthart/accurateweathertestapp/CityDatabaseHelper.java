package tk.lorddarthart.accurateweathertestapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class CityDatabaseHelper extends SQLiteOpenHelper implements BaseColumns {
    public static final String DATABASE_NAME = "tk.lorddarthart.accurateweathertestappcities.db";
    public static int DATABASE_VERSION = 1;

    public static final String DATABASE_WEATHER_CITY = "city";
    public static final String WEATHER_CITY_ID = "city_id";
    public static final String WEATHER_CITY_FILTERNAME = "city_name";

    public static final String DATABASE_CREATE_WEATHER_SCRIPT = "create table "
            + DATABASE_WEATHER_CITY
            + " (" + WEATHER_CITY_ID + " integer not null primary key autoincrement, "
            + WEATHER_CITY_FILTERNAME + " text not null, " + "UNIQUE(" + WEATHER_CITY_FILTERNAME + ") ON CONFLICT REPLACE);";

    CityDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public CityDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_WEATHER_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public static void addCity(SQLiteDatabase mSqLiteDatabase, String city_name) {
        ContentValues newValues = new ContentValues();
        newValues.put(CityDatabaseHelper.WEATHER_CITY_FILTERNAME, city_name);

        mSqLiteDatabase.insertWithOnConflict(CityDatabaseHelper.DATABASE_WEATHER_CITY, null, newValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

}
