package tk.lorddarthart.accurateweathertestapp.view.activity

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import android.support.v7.widget.RecyclerView.LayoutManager

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tk.lorddarthart.accurateweathertestapp.R
import tk.lorddarthart.accurateweathertestapp.controller.HorizontalRecyclerViewAdapter
import tk.lorddarthart.accurateweathertestapp.model.WeatherDay
import tk.lorddarthart.accurateweathertestapp.view.base.BaseActivity

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.LinkedList

class PodrobnoActivity : BaseActivity() {
}
