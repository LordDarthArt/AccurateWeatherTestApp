package tk.lorddarthart.accurateweathertestapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;

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
        txtRising = findViewById(R.id.txtRising);
        txtVisibility = findViewById(R.id.txtVisibility);
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
        txtTitle.setText(getIntent().getExtras().getString("weatherDescription"));
        txtHumidity.setText(String.valueOf(getIntent().getExtras().getDouble("weatherHumidity"))+"%");
        txtPressure.setText(String.valueOf(getIntent().getExtras().getDouble("weatherPressure"))+" mb");
        txtRising.setText(String.valueOf(getIntent().getExtras().getLong("weatherRising")));
        txtVisibility.setText(String.valueOf(getIntent().getExtras().getDouble("weatherVisibility"))+" km");
        if (getIntent().getExtras().getDouble("weatherD1high")>0) {
            tempHighDay1.setText("+"+String.valueOf(getIntent().getExtras().getDouble("weatherD1high")));
        } else {
            tempHighDay1.setText(String.valueOf(getIntent().getExtras().getDouble("weatherD1high")));
        }
        if (getIntent().getExtras().getDouble("weatherD1low")>0.0) {
            tempLowDay1.setText("+" + String.valueOf(getIntent().getExtras().getDouble("weatherD1low")));
        } else {
            tempLowDay1.setText(String.valueOf(getIntent().getExtras().getDouble("weatherD1low")));
        }
        descDay1.setText(getIntent().getExtras().getString("weatherD1text"));
        nameDay1.setText(new DayofWeekConversion().convert(getIntent().getExtras().getString("weatherD1day")));
        if (getIntent().getExtras().getDouble("weatherD2high")>0) {
            tempHighDay2.setText("+"+String.valueOf(getIntent().getExtras().getDouble("weatherD2high")));
        } else {
            tempHighDay2.setText(String.valueOf(getIntent().getExtras().getDouble("weatherD2high")));
        }
        if (getIntent().getExtras().getDouble("weatherD2low")>0.0) {
            tempLowDay2.setText("+"+String.valueOf(getIntent().getExtras().getDouble("weatherD2low")));
        } else {
            tempLowDay2.setText(String.valueOf(getIntent().getExtras().getDouble("weatherD2low")));
        }
        descDay2.setText(getIntent().getExtras().getString("weatherD2text"));
        nameDay2.setText(new DayofWeekConversion().convert(getIntent().getExtras().getString("weatherD2day")));
        if (getIntent().getExtras().getDouble("weatherD3high")>0) {
            tempHighDay3.setText("+"+String.valueOf(getIntent().getExtras().getDouble("weatherD3high")));
        } else {
            tempHighDay3.setText(String.valueOf(getIntent().getExtras().getDouble("weatherD3high")));
        }
        if (getIntent().getExtras().getDouble("weatherD3low")>0.0) {
            tempLowDay3.setText("+"+String.valueOf(getIntent().getExtras().getDouble("weatherD3low")));
        } else {
            tempLowDay3.setText(String.valueOf(getIntent().getExtras().getDouble("weatherD3low")));
        }
        descDay3.setText(getIntent().getExtras().getString("weatherD3text"));
        nameDay3.setText(new DayofWeekConversion().convert(getIntent().getExtras().getString("weatherD3day")));
        if (getIntent().getExtras().getDouble("weatherD4high")>0) {
            tempHighDay4.setText("+"+String.valueOf(getIntent().getExtras().getDouble("weatherD4high")));
        } else {
            tempHighDay4.setText(String.valueOf(getIntent().getExtras().getDouble("weatherD4high")));
        }
        if (getIntent().getExtras().getDouble("weatherD4low")>0.0) {
            tempLowDay4.setText("+"+String.valueOf(getIntent().getExtras().getDouble("weatherD4low")));
        } else {
            tempLowDay4.setText(String.valueOf(getIntent().getExtras().getDouble("weatherD4low")));
        }
        descDay4.setText(getIntent().getExtras().getString("weatherD4text"));
        nameDay4.setText(new DayofWeekConversion().convert(getIntent().getExtras().getString("weatherD4day")));
        if (getIntent().getExtras().getDouble("weatherD5high")>0) {
            tempHighDay5.setText("+"+String.valueOf(getIntent().getExtras().getDouble("weatherD5high")));
        } else {
            tempHighDay5.setText(String.valueOf(getIntent().getExtras().getDouble("weatherD5high")));
        }
        if (getIntent().getExtras().getDouble("weatherD5low")>0.0) {
            tempLowDay5.setText("+"+String.valueOf(getIntent().getExtras().getDouble("weatherD5low")));
        } else {
            tempLowDay5.setText(String.valueOf(getIntent().getExtras().getDouble("weatherD5low")));
        }
        descDay5.setText(getIntent().getExtras().getString("weatherD5text"));
        nameDay5.setText(new DayofWeekConversion().convert(getIntent().getExtras().getString("weatherD5day")));
        if (getIntent().getExtras().getDouble("weatherD6high")>0) {
            tempHighDay6.setText("+"+String.valueOf(getIntent().getExtras().getDouble("weatherD6high")));
        } else {
            tempHighDay6.setText(String.valueOf(getIntent().getExtras().getDouble("weatherD6high")));
        }
        if (getIntent().getExtras().getDouble("weatherD6low")>0.0) {
            tempLowDay6.setText("+"+String.valueOf(getIntent().getExtras().getDouble("weatherD6low")));
        } else {
            tempLowDay6.setText(String.valueOf(getIntent().getExtras().getDouble("weatherD6low")));
        }
        descDay6.setText(getIntent().getExtras().getString("weatherD6text"));
        nameDay6.setText(new DayofWeekConversion().convert(getIntent().getExtras().getString("weatherD6day")));
        if (getIntent().getExtras().getDouble("weatherD7high")>0) {
            tempHighDay7.setText("+"+String.valueOf(getIntent().getExtras().getDouble("weatherD7high")));
        } else {
            tempHighDay7.setText(String.valueOf(getIntent().getExtras().getDouble("weatherD7high")));
        }
        if (getIntent().getExtras().getDouble("weatherD7low")>0.0) {
            tempLowDay7.setText("+"+String.valueOf(getIntent().getExtras().getDouble("weatherD7low")));
        } else {
            tempLowDay7.setText(String.valueOf(getIntent().getExtras().getDouble("weatherD7low")));
        }
        descDay7.setText(getIntent().getExtras().getString("weatherD7text"));
        nameDay7.setText(new DayofWeekConversion().convert(getIntent().getExtras().getString("weatherD7day")));
    }
}
