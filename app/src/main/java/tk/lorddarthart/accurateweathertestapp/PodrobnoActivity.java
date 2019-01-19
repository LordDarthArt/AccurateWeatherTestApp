package tk.lorddarthart.accurateweathertestapp;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView.LayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

public class PodrobnoActivity extends AppCompatActivity {
    TextView txtDay, txtMonthYear, txtText, txtTemp, txtTitle, txtHumidity, txtPressure, txt3days, txt7days;
    LinkedList<WeatherDay> finalOutputString;
    RecyclerView recyclerView;
    HorizontalRecyclerViewAdapter recyclerViewAdapter;
    LayoutManager layoutManager;
    Integer futureForecasts; // Количество дней, которые необходимо отобразить
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podrobno);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Type type = new TypeToken<LinkedList<WeatherDay>>() {}.getType();
        Gson gson = new Gson();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();
        txt3days = findViewById(R.id.textView3days);
        txt7days = findViewById(R.id.textView7days);
        if (!sharedPreferences.contains("futureForecast")) {  //Настроено ли время прогноза на предстоящие дни
            editor.putString("futureForecast", "7days");
            futureForecasts = 7;
            editor.commit();
            txt7days.setTextColor(getResources().getColor(R.color.colorPrimary));
            txt3days.setTextColor(Color.parseColor("#808080"));
        } else { //Если настроено, то...
            if (sharedPreferences.getString("futureForecast", "7days").equals("3days")) { //...Приравнять переменную к трём и выделить текст
                futureForecasts = 3;
                txt3days.setTextColor(getResources().getColor(R.color.colorPrimary));
                txt7days.setTextColor(Color.parseColor("#808080"));
            } else if (sharedPreferences.getString("futureForecast", "7days").equals("7days")) { //...Приравнять переменную к семи и выделить текст
                futureForecasts = 7;
                txt7days.setTextColor(getResources().getColor(R.color.colorPrimary));
                txt3days.setTextColor(Color.parseColor("#808080"));
            }
        }
        recyclerView = findViewById(R.id.horizontalRecyclerView);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        txtDay = findViewById(R.id.txtDay);
        txtMonthYear = findViewById(R.id.txtMonthYear);
        txtText = findViewById(R.id.txtText);
        txtTemp = findViewById(R.id.txtTemp);
        txtTitle = findViewById(R.id.txtTitle);
        txtHumidity = findViewById(R.id.txtHumidity);
        txtPressure = findViewById(R.id.txtPressure);
        try {
            txtDay.setText(new SimpleDateFormat("dd").format(sdf.parse(getIntent().getExtras().getString("weatherDate"))));
            txtMonthYear.setText(new SimpleDateFormat("MMMM, yyyy").format(sdf.parse(getIntent().getExtras().getString("weatherDate"))));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        txtText.setText(getIntent().getExtras().getString("weatherText"));
        if (getIntent().getExtras().getDouble("weatherNow")>0.0){
            txtTemp.setText("+"+String.valueOf(getIntent().getExtras().getDouble("weatherNow")));
        } else {
            txtTemp.setText(String.valueOf(getIntent().getExtras().getDouble("weatherNow")));
        }
        txtTitle.setText(getIntent().getExtras().getString("weatherCity"));
        txtHumidity.setText(String.valueOf(getIntent().getExtras().getDouble("weatherHumidity"))+"%");
        txtPressure.setText(String.valueOf(getIntent().getExtras().getDouble("weatherPressure"))+" mb");

        finalOutputString = new LinkedList<>();

        txt3days.setOnClickListener(new View.OnClickListener() { // Обработка клика на три дня
            @Override
            public void onClick(View v) {
                if (futureForecasts!=3) { //Если клик не на себя же, изменяем настройку
                    futureForecasts=3;
                    editor.putString("futureForecast", "3days");
                    editor.commit();
                    txt3days.setTextColor(getResources().getColor(R.color.colorPrimary));
                    txt7days.setTextColor(Color.parseColor("#808080"));
                    PodrobnoActivity.this.recreate();
                }
            }
        });

        txt7days.setOnClickListener(new View.OnClickListener() { // Обработка клика на семь дней
            @Override
            public void onClick(View v) {
                if (futureForecasts!=7) { //Если клик не на себя же, изменяем настройку
                    futureForecasts=7;
                    editor.putString("futureForecast", "7days");
                    editor.commit();
                    txt7days.setTextColor(getResources().getColor(R.color.colorPrimary));
                    txt3days.setTextColor(Color.parseColor("#808080"));
                    PodrobnoActivity.this.recreate();

                }
            }
        });

        for (int i=0; i<futureForecasts; i++) { // Добавляем данные в подготовленный List
            finalOutputString.add(((LinkedList<WeatherDay>)gson.fromJson(getIntent().getExtras().getString("weatherD"+(i+1)), type)).get(0));
        }
        recyclerViewAdapter = new HorizontalRecyclerViewAdapter(PodrobnoActivity.this, finalOutputString);
        recyclerView.setAdapter(recyclerViewAdapter);
    }
}
