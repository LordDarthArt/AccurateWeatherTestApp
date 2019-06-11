package tk.lorddarthart.accurateweathertestapp.util.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import tk.lorddarthart.accurateweathertestapp.R
import tk.lorddarthart.accurateweathertestapp.application.model.weather.WeatherModel
import tk.lorddarthart.accurateweathertestapp.util.constants.SimpleDateFormatPatterns.TXT_DAY_MONTH_PATTERN
import tk.lorddarthart.accurateweathertestapp.util.tools.OnItemTouchListener

import java.text.SimpleDateFormat
import java.util.Date

class CitiesForecastsListAdapter(  //Адаптер для создания карточек с погодой городов на сегодняшний день на главном экране.
        private var context: Context,
        private var listWeather: MutableList<WeatherModel>,
        private val onItemTouchListener: OnItemTouchListener
) : RecyclerView.Adapter<CitiesForecastsListAdapter.ViewHolder>() {
    private lateinit var view: View
    private lateinit var viewHolder: ViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        view = LayoutInflater.from(context).inflate(R.layout.single_item, parent, false)
        viewHolder = ViewHolder(view)
        return viewHolder
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weather = listWeather[position]
        holder.tvCity.text = weather.weatherCity //город
        val sdf = SimpleDateFormat(TXT_DAY_MONTH_PATTERN)
        val date = Date(weather.weatherDate)
        holder.tvToday.text = sdf.format(date)
        if (weather.weatherHigh > 0) {
            holder.tvHighLow.text = "▲ +${weather.weatherHigh}"
        } else {
            holder.tvHighLow.text = "▲ ${weather.weatherHigh}"
        }
        if (weather.weatherLow > 0) {
            holder.tvHighLow.text = "${holder.tvHighLow.text} ▼ +${weather.weatherLow}"
        } else {
            holder.tvHighLow.text = "${holder.tvHighLow.text} ▼ ${weather.weatherLow}"
        }
        if (weather.weatherNow > 0) {
            holder.tvCelsius.text = "+${weather.weatherNow}"
        } else {
            holder.tvCelsius.text = weather.weatherNow.toString()
        }
        holder.tvSuntime.text = "✺▲ ${weather.mWeatherSunrise} ✺▼ ${weather.mWeatherSunset}"
    }

    override fun getItemCount(): Int {
        return listWeather.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var tvCity: TextView = itemView.findViewById(R.id.tvCity)
        internal var tvToday: TextView = itemView.findViewById(R.id.tvDate)
        internal var tvCelsius: TextView = itemView.findViewById(R.id.tvTemp)
        internal var tvSuntime: TextView = itemView.findViewById(R.id.tvSuntime)
        internal var tvHighLow: TextView = itemView.findViewById(R.id.tvHighlow)

        init {

            itemView.setOnClickListener { v -> onItemTouchListener.onCardViewTap(v, layoutPosition) }
        }
    }
}