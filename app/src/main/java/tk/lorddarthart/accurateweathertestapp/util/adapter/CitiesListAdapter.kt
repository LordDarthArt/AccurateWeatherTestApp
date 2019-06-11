package tk.lorddarthart.accurateweathertestapp.util.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import tk.lorddarthart.accurateweathertestapp.application.model.city.CityModel
import tk.lorddarthart.accurateweathertestapp.application.view.activity.MainActivity
import tk.lorddarthart.accurateweathertestapp.application.view.fragment.CitiesListFragment
import tk.lorddarthart.accurateweathertestapp.util.constants.SqlCommands.SQL_DELETE
import tk.lorddarthart.accurateweathertestapp.util.constants.SqlCommands.SQL_EQUALLY
import tk.lorddarthart.accurateweathertestapp.util.constants.SqlCommands.SQL_SEMICOLON
import tk.lorddarthart.accurateweathertestapp.util.constants.SqlCommands.SQL_WHERE
import tk.lorddarthart.accurateweathertestapp.util.tools.WeatherDatabaseHelper

class CitiesListAdapter(
        private var mContext: MainActivity,
        var mFragment: CitiesListFragment,
        private var mCitiesList: MutableList<CityModel>,
        private var mSqLiteDatabase: SQLiteDatabase,
        private var changes: Array<Int>
) : RecyclerView.Adapter<CitiesListAdapter.ViewHolder>() {
    private lateinit var mView: View
    private lateinit var mViewHolder: ViewHolder

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.settings_city, p0, false)
        mViewHolder = ViewHolder(mView)
        return mViewHolder
    }

    override fun getItemCount(): Int {
        return mCitiesList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewCity.text = mCitiesList[position].mCityName
        holder.textViewLatitude.text = "${mContext.resources.getString(R.string.latitude)} ${mCitiesList[position].mLatitude}"
        holder.textViewLongitude.text = "${mContext.resources.getString(R.string.longitude)} ${mCitiesList[position].mLongitude}"
        holder.buttonRemoveCity.setOnClickListener {
            try {
                AlertDialog.Builder(mContext)
                        .setTitle(mContext.getString(R.string.deleting_city_title))
                        .setMessage(mContext.getString(R.string.delete_city_message))
                        .setPositiveButton(R.string.yes) { _, _ ->
                            val query = SQL_DELETE +
                                    WeatherDatabaseHelper.DATABASE_WEATHER + SQL_WHERE +
                                    WeatherDatabaseHelper.WEATHER_FILTERNAME + " = \"" +
                                    holder.textViewCity.text.toString() + "\""
                            val query2 = SQL_DELETE + WeatherDatabaseHelper.DATABASE_WEATHER_CITY +
                                    SQL_WHERE + WeatherDatabaseHelper.WEATHER_CITY_FILTERNAME +
                                    "$SQL_EQUALLY$SQL_SEMICOLON" +
                                    holder.textViewCity.text.toString() + SQL_SEMICOLON
                            mSqLiteDatabase.execSQL(query)
                            mSqLiteDatabase.execSQL(query2)
                            holder.citiesListItem.visibility = View.GONE
                            changes[0]++
                        }
                        .setNegativeButton(R.string.no) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setCancelable(false)
                        .show()
            } catch (e: Exception) {
                e.message?.let { errorMessage ->
                    mView.longSnackbar(
                            errorMessage
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

        init {
            itemView.setOnClickListener {
                mFragment.onApplySettings()
            }
        }
    }
}