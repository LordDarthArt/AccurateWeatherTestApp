package tk.lorddarthart.accurateweathertestapp.controller

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import tk.lorddarthart.accurateweathertestapp.R
import tk.lorddarthart.accurateweathertestapp.model.WeatherDayModel

import java.util.LinkedList

internal class HorizontalRecyclerViewAdapter(//Адаптер для создания карточек с погодой на неделю в подробном просмотре.
        var context: Context,
        var listWeatherDay: LinkedList<WeatherDayModel>
) : RecyclerView.Adapter<HorizontalRecyclerViewAdapter.ViewHolder>() {
    private lateinit var mView: View
    private lateinit var mViewHolder: ViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mView = LayoutInflater.from(context).inflate(R.layout.single_item_horizontal, parent, false)
        mViewHolder = ViewHolder(mView)
        return mViewHolder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val weatherDay = listWeatherDay[i]
        if (weatherDay.weather_hight!! > 0) {
            viewHolder.tempHighDay.text = "+${weatherDay.weather_hight.toString()}"
        } else {
            viewHolder.tempHighDay.text = weatherDay.weather_hight.toString()
        }
        if (weatherDay.weather_low!! > 0.0) {
            viewHolder.tempLowDay.text = "+${weatherDay.weather_low.toString()}"
        } else {
            viewHolder.tempLowDay.text = weatherDay.weather_low.toString()
        }
        viewHolder.descDay.text = weatherDay.weather_text
        viewHolder.nameDay.text = weatherDay.weather_day

    }

    override fun getItemCount(): Int {
        return listWeatherDay.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var tempHighDay: TextView
        internal var tempLowDay: TextView
        internal var nameDay: TextView
        internal var descDay: TextView

        init {

            tempHighDay = itemView.findViewById(R.id.tempHighDay)
            tempLowDay = itemView.findViewById(R.id.tempDayLow)
            descDay = itemView.findViewById(R.id.descDay)
            nameDay = itemView.findViewById(R.id.nameDay)
        }
    }
}
