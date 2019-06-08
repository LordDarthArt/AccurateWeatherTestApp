package tk.lorddarthart.accurateweathertestapp.util.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import tk.lorddarthart.accurateweathertestapp.R
import tk.lorddarthart.accurateweathertestapp.application.model.WeatherDayModel

internal class FutureForecastsAdapter(//Адаптер для создания карточек с погодой на неделю в подробном просмотре.
        var context: Context,
        var listWeatherDay: MutableList<WeatherDayModel>
) : RecyclerView.Adapter<FutureForecastsAdapter.ViewHolder>() {
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
        if (weatherDay.mWeatherHigh!! > 0) {
            viewHolder.tempHighDay.text = "+${weatherDay.mWeatherHigh.toString()}"
        } else {
            viewHolder.tempHighDay.text = weatherDay.mWeatherHigh.toString()
        }
        if (weatherDay.mWeatherLow!! > 0.0) {
            viewHolder.tempLowDay.text = "+${weatherDay.mWeatherLow.toString()}"
        } else {
            viewHolder.tempLowDay.text = weatherDay.mWeatherLow.toString()
        }
        viewHolder.descDay.text = weatherDay.mWeatherText
        viewHolder.nameDay.text = weatherDay.mWeatherDay

    }

    override fun getItemCount(): Int {
        return listWeatherDay.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var tempHighDay: TextView = itemView.findViewById(R.id.tempHighDay)
        internal var tempLowDay: TextView = itemView.findViewById(R.id.tempDayLow)
        internal var nameDay: TextView = itemView.findViewById(R.id.nameDay)
        internal var descDay: TextView = itemView.findViewById(R.id.descDay)

    }
}
