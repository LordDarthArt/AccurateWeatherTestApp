package tk.lorddarthart.accurateweathertestapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase mSqLiteDatabase;
    Geocoder geocoder;
    WeatherDatabaseHelper mDatabaseHelper;
    HttpServiceHelper httpServiceHelper;
    ArrayList<Weather> weather = new ArrayList<>();
    private Cursor cursor;
    private Cursor cursor2;
    private ProgressDialog dialog;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor ed;
    int opening=0, opening2=0;
    List<Address> addresses;
    LinkedList<City> cities = new LinkedList<>();
    ConstraintLayout consLayText;
    ImageView constraintLayout;
    FloatingActionButton fab;
    EditText editText;
    Animation consLayOpen, consLayClose, rotateForward, rotateBackward, tvOpen, tvClose;
    boolean isOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        geocoder = new Geocoder(getApplicationContext());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sharedPreferences.edit();
        constraintLayout = findViewById(R.id.consLayHide);
        consLayText = findViewById(R.id.consLayText);
        editText = findViewById(R.id.editText);
        fab = findViewById(R.id.floatingActionButton);
        constraintLayout.setVisibility(View.VISIBLE);
        mDatabaseHelper = new WeatherDatabaseHelper(MainActivity.this, WeatherDatabaseHelper.DATABASE_NAME, null, WeatherDatabaseHelper.DATABASE_VERSION);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        String query = "SELECT " + WeatherDatabaseHelper.WEATHER_FILTERNAME + ", " + WeatherDatabaseHelper.WEATHER_DATE + ", " + WeatherDatabaseHelper.WEATHER_CITY + ", " + WeatherDatabaseHelper.WEATHER_NOW + ", "
                + WeatherDatabaseHelper.WEATHER_HIGH + " , " + WeatherDatabaseHelper.WEATHER_LOW + ", " + WeatherDatabaseHelper.WEATHER_TEXT + ", " + WeatherDatabaseHelper.WEATHER_DESCRIPTION + ", "
                + WeatherDatabaseHelper.WEATHER_HUMIDITY + ", " + WeatherDatabaseHelper.WEATHER_PRESSURE  + ", "
                + WeatherDatabaseHelper.WEATHER_SUNRISE + ", " + WeatherDatabaseHelper.WEATHER_SUNSET + ", " + WeatherDatabaseHelper.WEATHER_D1+ ", " + WeatherDatabaseHelper.WEATHER_D2+ ", " + WeatherDatabaseHelper.WEATHER_D3
                + ", " + WeatherDatabaseHelper.WEATHER_D4+ ", " + WeatherDatabaseHelper.WEATHER_D5+ ", " + WeatherDatabaseHelper.WEATHER_D6+ ", " + WeatherDatabaseHelper.WEATHER_D7 +" FROM " + WeatherDatabaseHelper.DATABASE_WEATHER;
        cursor = mSqLiteDatabase.rawQuery(query, new String[0]);
        mRecyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
            }
        });
        rotateForward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        consLayOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.conslay_open);
        consLayClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.conslay_close);
        tvOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.tv_open);
        tvClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.tv_close);
        if (sharedPreferences.getBoolean("cities", false)==true) {
            String citiesQuery = "SELECT * FROM " + WeatherDatabaseHelper.DATABASE_WEATHER_CITY;
            cursor2 = mSqLiteDatabase.rawQuery(citiesQuery, new String[0]);
            cursor2.moveToFirst();
            cursor2.moveToPrevious();
            cities.clear();
            while (cursor2.moveToNext()) {
                cities.add(new City(cursor2.getInt(cursor2.getColumnIndex(mDatabaseHelper.WEATHER_CITY_ID)),cursor2.getString(cursor2.getColumnIndex(mDatabaseHelper.WEATHER_CITY_FILTERNAME)),cursor2.getString(cursor2.getColumnIndex(mDatabaseHelper.WEATHER_CITY_LATITUDE)),cursor2.getString(cursor2.getColumnIndex(mDatabaseHelper.WEATHER_CITY_LONGITUDE))));
            }
        } else {
            try {
                addresses = geocoder.getFromLocationName("Санкт-Петербург", 1);
                if(addresses.size() > 0) {
                    double latitude= addresses.get(0).getLatitude();
                    double longitude= addresses.get(0).getLongitude();
                    String addCitiesQuery = "INSERT INTO " + WeatherDatabaseHelper.DATABASE_WEATHER_CITY + " ("+mDatabaseHelper.WEATHER_CITY_FILTERNAME+", "+mDatabaseHelper.WEATHER_CITY_LATITUDE+", "+mDatabaseHelper.WEATHER_CITY_LONGITUDE+") VALUES ('Санкт-Петербург',"+String.valueOf(latitude)+", "+String.valueOf(longitude)+")";
                    mSqLiteDatabase.execSQL(addCitiesQuery);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                addresses = geocoder.getFromLocationName("Москва", 1);
                if(addresses.size() > 0) {
                    double latitude= addresses.get(0).getLatitude();
                    double longitude= addresses.get(0).getLongitude();
                    String addCitiesQuery = "INSERT INTO " + WeatherDatabaseHelper.DATABASE_WEATHER_CITY + " ("+mDatabaseHelper.WEATHER_CITY_FILTERNAME+", "+mDatabaseHelper.WEATHER_CITY_LATITUDE+", "+mDatabaseHelper.WEATHER_CITY_LONGITUDE+") VALUES ('Москва',"+String.valueOf(latitude)+", "+String.valueOf(longitude)+")";
                    mSqLiteDatabase.execSQL(addCitiesQuery);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            ed.putBoolean("cities", true);
            ed.commit();
            String citiesQuery = "SELECT * FROM " + WeatherDatabaseHelper.DATABASE_WEATHER_CITY;
            cursor2 = mSqLiteDatabase.rawQuery(citiesQuery, new String[0]);
            cursor2.moveToFirst();
            cursor2.moveToPrevious();
            cities.clear();
            while (cursor2.moveToNext()) {
                cities.add(new City(cursor2.getInt(cursor2.getColumnIndex(mDatabaseHelper.WEATHER_CITY_ID)),cursor2.getString(cursor2.getColumnIndex(mDatabaseHelper.WEATHER_CITY_FILTERNAME)),cursor2.getString(cursor2.getColumnIndex(mDatabaseHelper.WEATHER_CITY_LATITUDE)),cursor2.getString(cursor2.getColumnIndex(mDatabaseHelper.WEATHER_CITY_LONGITUDE))));
            }
        }
        httpServiceHelper = new HttpServiceHelper();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText.getText().length()>0&&fab.getRotation()!=-45) {
                    fab.setImageResource(android.R.drawable.ic_menu_send);
                    fab.setRotation(-45);
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            fab.setRotation(0);
                            fab.setImageResource(R.drawable.ic_baseline_plus_24px);
                            animateFab();
                            fab.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    animateFab();
                                }
                            });
                            try {
                                addresses = geocoder.getFromLocationName(editText.getText().toString(), 1);
                                if(addresses.size() > 0) {
                                    String latitude= String.valueOf(addresses.get(0).getLatitude());
                                    String longitude= String.valueOf(addresses.get(0).getLongitude());
                                    String sql = "Insert into " + WeatherDatabaseHelper.DATABASE_WEATHER_CITY + " ("+mDatabaseHelper.WEATHER_CITY_FILTERNAME+", "+mDatabaseHelper.WEATHER_CITY_LATITUDE+", "+mDatabaseHelper.WEATHER_CITY_LONGITUDE+") VALUES ('"+editText.getText().toString()+"',"+latitude+", "+longitude+")";
                                    mSqLiteDatabase.execSQL(sql);
                                } else {
                                    throw new IOException();
                                }
                            } catch (IOException e) {
                                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Произошла ошибка, повторите попытку ввода", Snackbar.LENGTH_LONG).show();
                            }
                            editText.setText("");
                            hideSoftKeyboard();
                            String citiesQuery = "SELECT * FROM " + WeatherDatabaseHelper.DATABASE_WEATHER_CITY;
                            cursor2 = mSqLiteDatabase.rawQuery(citiesQuery, new String[0]);
                            cursor2.moveToFirst();
                            cursor2.moveToPrevious();
                            while (cursor2.moveToNext()) {
                                cities.add(new City(cursor2.getInt(cursor2.getColumnIndex(mDatabaseHelper.WEATHER_CITY_ID)),cursor2.getString(cursor2.getColumnIndex(mDatabaseHelper.WEATHER_CITY_FILTERNAME)),cursor2.getString(cursor2.getColumnIndex(mDatabaseHelper.WEATHER_CITY_LATITUDE)),cursor2.getString(cursor2.getColumnIndex(mDatabaseHelper.WEATHER_CITY_LONGITUDE))));
                            }
                            opening=0;
                            opening2=0;
                            mRecyclerView.setVisibility(View.INVISIBLE);
                            try {
                                for (int i=0; i<cities.size(); i++) {
                                    new UpdateForecast(cities.get(i).getCityName(), cities.get(i).getLatitude(), cities.get(i).getLongitude()).execute();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }else if (editText.getText().length()==0) {
                    fab.setImageResource(R.drawable.ic_baseline_plus_24px);
                    fab.setRotation(0);
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            animateFab();
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        try {
            for (int i=0; i<cities.size(); i++) {
                new UpdateForecast(cities.get(i).getCityName(), cities.get(i).getLatitude(), cities.get(i).getLongitude()).execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void animateFab() {
        if (isOpen) {
            fab.startAnimation(rotateForward);
            consLayText.startAnimation(tvClose);
            consLayText.setClickable(false);
            consLayText.setVisibility(View.GONE);
            constraintLayout.startAnimation(consLayClose);
            constraintLayout.setClickable(false);
            constraintLayout.setVisibility(View.GONE);
            isOpen=false;
        }
        else
        {
            fab.startAnimation(rotateBackward);
            consLayText.startAnimation(tvOpen);
            consLayText.setClickable(true);
            consLayText.setVisibility(View.VISIBLE);
            constraintLayout.setClickable(true);
            constraintLayout.setColorFilter(Color.argb(150, 155, 155, 155),  PorterDuff.Mode.DARKEN);
            constraintLayout.startAnimation(consLayOpen);
            isOpen=true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_setcity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    protected void onStop() {
        super.onStop();
        TaskLoader.cancel();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LinearLayout container = new LinearLayout(getApplicationContext());
        container.setOrientation(LinearLayout.VERTICAL);
        String citiesQuery = "SELECT * FROM " + WeatherDatabaseHelper.DATABASE_WEATHER_CITY;
        cursor2 = mSqLiteDatabase.rawQuery(citiesQuery, new String[0]);
        cursor2.moveToFirst();
        cursor2.moveToPrevious();
        cities.clear();
        while (cursor2.moveToNext()) {
            cities.add(new City(cursor2.getInt(cursor2.getColumnIndex(mDatabaseHelper.WEATHER_CITY_ID)), cursor2.getString(cursor2.getColumnIndex(mDatabaseHelper.WEATHER_CITY_FILTERNAME))));
        }
        for (int i=0; i<cities.size(); i++) {
            View holder = getLayoutInflater().inflate(R.layout.settings_city, null, false);
            final TextView textViewCity = holder.findViewById(R.id.tvCity);
            textViewCity.setText(cities.get(i).getCityName());
            final ImageView img = holder.findViewById(R.id.ivDelCity);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        String query = "DELETE from " + mDatabaseHelper.DATABASE_WEATHER + " WHERE "+ mDatabaseHelper.WEATHER_FILTERNAME + " = \"" + textViewCity.getText().toString()+"\"";
                        String query2 = "DELETE from " + mDatabaseHelper.DATABASE_WEATHER_CITY + " WHERE "+ mDatabaseHelper.WEATHER_CITY_FILTERNAME + " = \"" + textViewCity.getText().toString()+"\"";
                        mSqLiteDatabase.execSQL(query);
                        mSqLiteDatabase.execSQL(query2);
                        textViewCity.setVisibility(View.GONE);
                        img.setVisibility(View.GONE);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                    }
                }
            });
            container.addView(holder);
        }
        final AlertDialog builder = new AlertDialog.Builder( this)
                .setTitle("Введите необходимые данные")
                .setPositiveButton(android.R.string.ok, null)
                .setCancelable(false)
                .setView(container)
                .create();
        switch (item.getItemId()) {
            case R.id.action_setcity:{
                builder.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialogInterface) {

                        Button button = builder.getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    builder.dismiss();
                                    MainActivity.this.recreate();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });

                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                    }
                });
            }
        }
        builder.show();
        return super.onOptionsItemSelected(item);
    }

    public void getCurrentForecast() {
        cursor.moveToFirst();
        cursor.moveToPrevious();
        weather.clear();
        while (cursor.moveToNext()) {
            Weather weathers = new Weather();
            weathers.setWeather_city(cursor.getString(cursor.getColumnIndex(mDatabaseHelper.WEATHER_CITY)));
            weathers.setWeather_now(cursor.getDouble(cursor.getColumnIndex(mDatabaseHelper.WEATHER_NOW)));
            weathers.setWeather_date(cursor.getLong(cursor.getColumnIndex(mDatabaseHelper.WEATHER_DATE)));
            weathers.setWeather_high(cursor.getDouble(cursor.getColumnIndex(mDatabaseHelper.WEATHER_HIGH)));
            weathers.setWeather_low(cursor.getDouble(cursor.getColumnIndex(mDatabaseHelper.WEATHER_LOW)));
            weathers.setWeather_sunrise(cursor.getString(cursor.getColumnIndex(mDatabaseHelper.WEATHER_SUNRISE)));
            weathers.setWeather_sunset(cursor.getString(cursor.getColumnIndex(mDatabaseHelper.WEATHER_SUNSET)));
            weathers.setWeather_text(cursor.getString(cursor.getColumnIndex(mDatabaseHelper.WEATHER_TEXT)));
            weathers.setWeather_description(cursor.getString(cursor.getColumnIndex(mDatabaseHelper.WEATHER_DESCRIPTION)));
            weathers.setWeather_humidity(cursor.getDouble(cursor.getColumnIndex(mDatabaseHelper.WEATHER_HUMIDITY)));
            weathers.setWeather_pressure(cursor.getDouble(cursor.getColumnIndex(mDatabaseHelper.WEATHER_PRESSURE)));
            weathers.setWeather_d1(cursor.getString(cursor.getColumnIndex(mDatabaseHelper.WEATHER_D1)));
            weathers.setWeather_d2(cursor.getString(cursor.getColumnIndex(mDatabaseHelper.WEATHER_D2)));
            weathers.setWeather_d3(cursor.getString(cursor.getColumnIndex(mDatabaseHelper.WEATHER_D3)));
            weathers.setWeather_d4(cursor.getString(cursor.getColumnIndex(mDatabaseHelper.WEATHER_D4)));
            weathers.setWeather_d5(cursor.getString(cursor.getColumnIndex(mDatabaseHelper.WEATHER_D5)));
            weathers.setWeather_d6(cursor.getString(cursor.getColumnIndex(mDatabaseHelper.WEATHER_D6)));
            weathers.setWeather_d7(cursor.getString(cursor.getColumnIndex(mDatabaseHelper.WEATHER_D7)));
            weather.add(weathers);
        }
        initializeAdapter();
    }

    private void initializeAdapter() {
        OnItemTouchListener itemTouchListener = new OnItemTouchListener() {
            @Override
            public void onCardViewTap(View view, int position) {
                Intent startTaskInfoActivity = new Intent(MainActivity.this, PodrobnoActivity.class);
                Weather weathers = weather.get(position);
                startTaskInfoActivity.putExtra("weatherCity", weathers.getWeather_city());
                startTaskInfoActivity.putExtra("weatherNow", weathers.getWeather_now());
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                Date date = new Date(weathers.getWeather_date());
                String dateText = sdf.format(date);
                startTaskInfoActivity.putExtra("weatherDate", dateText);
                startTaskInfoActivity.putExtra("weatherHigh", weathers.getWeather_high());
                startTaskInfoActivity.putExtra("weatherLow", weathers.getWeather_low());
                startTaskInfoActivity.putExtra("weatherSunrise", weathers.getWeather_sunrise());
                startTaskInfoActivity.putExtra("weatherSunset", weathers.getWeather_sunset());
                startTaskInfoActivity.putExtra("weatherDescription", weathers.getWeather_description());
                startTaskInfoActivity.putExtra("weatherText", weathers.getWeather_text());
                startTaskInfoActivity.putExtra("weatherHumidity", weathers.getWeather_humidity());
                startTaskInfoActivity.putExtra("weatherPressure", weathers.getWeather_pressure());
                startTaskInfoActivity.putExtra("weatherD1", weathers.getWeather_d1());
                startTaskInfoActivity.putExtra("weatherD2", weathers.getWeather_d2());
                startTaskInfoActivity.putExtra("weatherD3", weathers.getWeather_d3());
                startTaskInfoActivity.putExtra("weatherD4", weathers.getWeather_d4());
                startTaskInfoActivity.putExtra("weatherD5", weathers.getWeather_d5());
                startTaskInfoActivity.putExtra("weatherD6", weathers.getWeather_d6());
                startTaskInfoActivity.putExtra("weatherD7", weathers.getWeather_d7());
                startActivity(startTaskInfoActivity);
            }

            @Override
            public void onButtonCvMenuClick(View view, int position) {

            }
        };
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(), weather, itemTouchListener);
        mRecyclerView.setAdapter(recyclerViewAdapter);
    }

    class UpdateForecast extends AsyncTask<String, Void, Void> {
        int responseCode;
        String city;
        String latitude;
        String longitude;

        public UpdateForecast(String city, String latitude, String longitude) {
            this.city = city;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected void onPreExecute() {
            if (opening==0) {
                dialog = new ProgressDialog(MainActivity.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("Синхронизация…");
                dialog.setCancelable(false);
                dialog.show();
                opening++;
            }
        }

        @Override
        protected Void doInBackground(String...strings) {
            TaskLoader.setTask(this);
            try {
                responseCode = httpServiceHelper.getForecast(mSqLiteDatabase, city, latitude, longitude);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            opening2++;
            if (opening2==cities.size()) {
                String query = "SELECT " + WeatherDatabaseHelper.WEATHER_FILTERNAME + ", " + WeatherDatabaseHelper.WEATHER_DATE + ", " + WeatherDatabaseHelper.WEATHER_CITY + ", " + WeatherDatabaseHelper.WEATHER_NOW + ", "
                        + WeatherDatabaseHelper.WEATHER_HIGH + " , " + WeatherDatabaseHelper.WEATHER_LOW + ", " + WeatherDatabaseHelper.WEATHER_TEXT + ", " + WeatherDatabaseHelper.WEATHER_DESCRIPTION + ", "
                        + WeatherDatabaseHelper.WEATHER_HUMIDITY + ", " + WeatherDatabaseHelper.WEATHER_PRESSURE + ", "
                        + WeatherDatabaseHelper.WEATHER_SUNRISE + ", " + WeatherDatabaseHelper.WEATHER_SUNSET + ", " + WeatherDatabaseHelper.WEATHER_D1 + ", " + WeatherDatabaseHelper.WEATHER_D2 + ", " + WeatherDatabaseHelper.WEATHER_D3
                        + ", " + WeatherDatabaseHelper.WEATHER_D4 + ", " + WeatherDatabaseHelper.WEATHER_D5 + ", " + WeatherDatabaseHelper.WEATHER_D6 + ", " + WeatherDatabaseHelper.WEATHER_D7+ " FROM " + WeatherDatabaseHelper.DATABASE_WEATHER;
                cursor = mSqLiteDatabase.rawQuery(query, new String[0]);
                getCurrentForecast();
                dialog.dismiss();
                mRecyclerView.setVisibility(View.VISIBLE);
                constraintLayout.setVisibility(View.GONE);
            }
        }
    }

    public void showSoftKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideSoftKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(fab.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
