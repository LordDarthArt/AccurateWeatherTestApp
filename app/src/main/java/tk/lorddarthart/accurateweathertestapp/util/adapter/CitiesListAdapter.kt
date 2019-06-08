package tk.lorddarthart.accurateweathertestapp.util.adapter

import android.database.sqlite.SQLiteDatabase
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.jetbrains.anko.design.longSnackbar
import tk.lorddarthart.accurateweathertestapp.R
import tk.lorddarthart.accurateweathertestapp.application.model.CityModel
import tk.lorddarthart.accurateweathertestapp.application.view.activity.MainActivity
import tk.lorddarthart.accurateweathertestapp.util.tools.WeatherDatabaseHelper

class CitiesListAdapter(
        var mContext: MainActivity,
        var mCitiesList: MutableList<CityModel>,
        var mSqLiteDatabase: SQLiteDatabase
): RecyclerView.Adapter<CitiesListAdapter.ViewHolder>() {
    private lateinit var mView: View
    private lateinit var mViewHolder: ViewHolder

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        mView = LayoutInflater.from(mContext).inflate(R.layout.settings_city, p0, false)
        mViewHolder = ViewHolder(mView)
        return mViewHolder
    }

    override fun getItemCount(): Int {
        return mCitiesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewCity.text = mCitiesList[position].mCityName
        holder.buttonRemoveCity.setOnClickListener {
            try {
                val query = "DELETE from " +
                        WeatherDatabaseHelper.DATABASE_WEATHER + " WHERE " +
                        WeatherDatabaseHelper.WEATHER_FILTERNAME + " = \"" +
                        holder.textViewCity.text.toString() + "\""
                val query2 = "DELETE from " + WeatherDatabaseHelper.DATABASE_WEATHER_CITY +
                        " WHERE " + WeatherDatabaseHelper.WEATHER_CITY_FILTERNAME + " = \"" +
                        holder.textViewCity.text.toString() + "\""
                mSqLiteDatabase.execSQL(query)
                mSqLiteDatabase.execSQL(query2)
                holder.citiesListItem.visibility = View.GONE
                //changes[0]++
            } catch (e: Exception) {
                e.message?.let { errorMessage ->
                    longSnackbar(
                            mContext.findViewById(android.R.id.content), errorMessage
                    ).show()
                }
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewCity: TextView = itemView.findViewById(R.id.tvCity)
        val textViewLongitude: TextView = itemView.findViewById(R.id.tvLongitude)
        val textViewLatitude: TextView = itemView.findViewById(R.id.tvLatitude)
        val buttonRemoveCity: ImageView = itemView.findViewById(R.id.ivDelCity)
        val citiesListItem: CardView = itemView.findViewById(R.id.cities_list_item)
    }
}