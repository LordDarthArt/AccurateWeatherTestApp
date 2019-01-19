package tk.lorddarthart.accurateweathertestapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

public class PodrobnoActivity extends AppCompatActivity {
    TextView txtDay, txtMonthYear, txtText, txtTemp, txtTitle, txtHumidity, txtPressure, txtRising, txtVisibility, tempHighDay1, tempLowDay1, nameDay1, descDay1, tempHighDay2, tempLowDay2, nameDay2, descDay2, tempHighDay3, tempLowDay3, nameDay3, descDay3, tempHighDay4, tempLowDay4, nameDay4, descDay4, tempHighDay5, tempLowDay5, nameDay5, descDay5, tempHighDay6, tempLowDay6, nameDay6, descDay6, tempHighDay7, tempLowDay7, nameDay7, descDay7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podrobno);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        txtDay = findViewById(R.id.txtDay);
        txtMonthYear = findViewById(R.id.txtMonthYear);
        txtText = findViewById(R.id.txtText);
        txtTemp = findViewById(R.id.txtTemp);
        txtTitle = findViewById(R.id.txtTitle);
        txtHumidity = findViewById(R.id.txtHumidity);
        txtPressure = findViewById(R.id.txtPressure);
        tempHighDay1 = findViewById(R.id.tempHighDay1);
        tempLowDay1 = findViewById(R.id.tempDayLow1);
        descDay1 = findViewById(R.id.descDay1);
        nameDay1 = findViewById(R.id.nameDay1);
        tempHighDay2 = findViewById(R.id.tempHighDay2);
        tempLowDay2 = findViewById(R.id.tempDayLow2);
        descDay2 = findViewById(R.id.descDay2);
        nameDay2 = findViewById(R.id.nameDay2);
        tempHighDay3 = findViewById(R.id.tempHighDay3);
        tempLowDay3 = findViewById(R.id.tempDayLow3);
        descDay3 = findViewById(R.id.descDay3);
        nameDay3 = findViewById(R.id.nameDay3);
        tempHighDay4 = findViewById(R.id.tempHighDay4);
        tempLowDay4 = findViewById(R.id.tempDayLow4);
        descDay4 = findViewById(R.id.descDay4);
        nameDay4 = findViewById(R.id.nameDay4);
        tempHighDay5 = findViewById(R.id.tempHighDay5);
        tempLowDay5 = findViewById(R.id.tempDayLow5);
        descDay5 = findViewById(R.id.descDay5);
        nameDay5 = findViewById(R.id.nameDay5);
        tempHighDay6 = findViewById(R.id.tempHighDay6);
        tempLowDay6 = findViewById(R.id.tempDayLow6);
        descDay6 = findViewById(R.id.descDay6);
        nameDay6 = findViewById(R.id.nameDay6);
        tempHighDay7 = findViewById(R.id.tempHighDay7);
        tempLowDay7 = findViewById(R.id.tempDayLow7);
        descDay7 = findViewById(R.id.descDay7);
        nameDay7 = findViewById(R.id.nameDay7);
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

        Type type = new TypeToken<LinkedList<WeatherDay>>() {}.getType();
        Gson gson = new Gson();
        LinkedList<WeatherDay> finalOutputString = gson.fromJson(getIntent().getExtras().getString("weatherD1"), type);
        if (finalOutputString.get(0).getWeather_hight()>0) {
            tempHighDay1.setText("+"+String.valueOf(finalOutputString.get(0).getWeather_hight()));
        } else {
            tempHighDay1.setText(String.valueOf(finalOutputString.get(0).getWeather_hight()));
        }
        if (finalOutputString.get(0).getWeather_low()>0.0) {
            tempLowDay1.setText("+" + String.valueOf(finalOutputString.get(0).getWeather_low()));
        } else {
            tempLowDay1.setText(String.valueOf(finalOutputString.get(0).getWeather_low()));
        }
        descDay1.setText(finalOutputString.get(0).getWeather_text());
        nameDay1.setText(finalOutputString.get(0).getWeather_day());

        finalOutputString.clear();
        finalOutputString = gson.fromJson(getIntent().getExtras().getString("weatherD2"), type);
        if (finalOutputString.get(0).getWeather_hight()>0) {
            tempHighDay2.setText("+"+String.valueOf(finalOutputString.get(0).getWeather_hight()));
        } else {
            tempHighDay2.setText(String.valueOf(finalOutputString.get(0).getWeather_hight()));
        }
        if (finalOutputString.get(0).getWeather_low()>0.0) {
            tempLowDay2.setText("+" + String.valueOf(finalOutputString.get(0).getWeather_low()));
        } else {
            tempLowDay2.setText(String.valueOf(finalOutputString.get(0).getWeather_low()));
        }
        descDay2.setText(finalOutputString.get(0).getWeather_text());
        nameDay2.setText(finalOutputString.get(0).getWeather_day());

        finalOutputString.clear();
        finalOutputString = gson.fromJson(getIntent().getExtras().getString("weatherD3"), type);
        if (finalOutputString.get(0).getWeather_hight()>0) {
            tempHighDay3.setText("+"+String.valueOf(finalOutputString.get(0).getWeather_hight()));
        } else {
            tempHighDay3.setText(String.valueOf(finalOutputString.get(0).getWeather_hight()));
        }
        if (finalOutputString.get(0).getWeather_low()>0.0) {
            tempLowDay3.setText("+" + String.valueOf(finalOutputString.get(0).getWeather_low()));
        } else {
            tempLowDay3.setText(String.valueOf(finalOutputString.get(0).getWeather_low()));
        }
        descDay3.setText(finalOutputString.get(0).getWeather_text());
        nameDay3.setText(finalOutputString.get(0).getWeather_day());

        finalOutputString.clear();
        finalOutputString = gson.fromJson(getIntent().getExtras().getString("weatherD4"), type);
        if (finalOutputString.get(0).getWeather_hight()>0) {
            tempHighDay4.setText("+"+String.valueOf(finalOutputString.get(0).getWeather_hight()));
        } else {
            tempHighDay4.setText(String.valueOf(finalOutputString.get(0).getWeather_hight()));
        }
        if (finalOutputString.get(0).getWeather_low()>0.0) {
            tempLowDay4.setText("+" + String.valueOf(finalOutputString.get(0).getWeather_low()));
        } else {
            tempLowDay4.setText(String.valueOf(finalOutputString.get(0).getWeather_low()));
        }
        descDay4.setText(finalOutputString.get(0).getWeather_text());
        nameDay4.setText(finalOutputString.get(0).getWeather_day());

        finalOutputString.clear();
        finalOutputString = gson.fromJson(getIntent().getExtras().getString("weatherD5"), type);
        if (finalOutputString.get(0).getWeather_hight()>0) {
            tempHighDay5.setText("+"+String.valueOf(finalOutputString.get(0).getWeather_hight()));
        } else {
            tempHighDay5.setText(String.valueOf(finalOutputString.get(0).getWeather_hight()));
        }
        if (finalOutputString.get(0).getWeather_low()>0.0) {
            tempLowDay5.setText("+" + String.valueOf(finalOutputString.get(0).getWeather_low()));
        } else {
            tempLowDay5.setText(String.valueOf(finalOutputString.get(0).getWeather_low()));
        }
        descDay5.setText(finalOutputString.get(0).getWeather_text());
        nameDay5.setText(finalOutputString.get(0).getWeather_day());

        finalOutputString.clear();
        finalOutputString = gson.fromJson(getIntent().getExtras().getString("weatherD6"), type);
        if (finalOutputString.get(0).getWeather_hight()>0) {
            tempHighDay6.setText("+"+String.valueOf(finalOutputString.get(0).getWeather_hight()));
        } else {
            tempHighDay6.setText(String.valueOf(finalOutputString.get(0).getWeather_hight()));
        }
        if (finalOutputString.get(0).getWeather_low()>0.0) {
            tempLowDay6.setText("+" + String.valueOf(finalOutputString.get(0).getWeather_low()));
        } else {
            tempLowDay6.setText(String.valueOf(finalOutputString.get(0).getWeather_low()));
        }
        descDay6.setText(finalOutputString.get(0).getWeather_text());
        nameDay6.setText(finalOutputString.get(0).getWeather_day());

        finalOutputString.clear();
        finalOutputString = gson.fromJson(getIntent().getExtras().getString("weatherD7"), type);
        if (finalOutputString.get(0).getWeather_hight()>0) {
            tempHighDay7.setText("+"+String.valueOf(finalOutputString.get(0).getWeather_hight()));
        } else {
            tempHighDay7.setText(String.valueOf(finalOutputString.get(0).getWeather_hight()));
        }
        if (finalOutputString.get(0).getWeather_low()>0.0) {
            tempLowDay7.setText("+" + String.valueOf(finalOutputString.get(0).getWeather_low()));
        } else {
            tempLowDay7.setText(String.valueOf(finalOutputString.get(0).getWeather_low()));
        }
        descDay7.setText(finalOutputString.get(0).getWeather_text());
        nameDay7.setText(finalOutputString.get(0).getWeather_day());
    }
}
