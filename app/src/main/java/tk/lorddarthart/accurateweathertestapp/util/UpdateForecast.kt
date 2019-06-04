import android.os.AsyncTask
import android.view.View
import tk.lorddarthart.accurateweathertestapp.application.view.fragment.MainFragment
import tk.lorddarthart.accurateweathertestapp.util.tools.WeatherDatabaseHelper

class UpdateForecast(fragment: MainFragment, city: String, latitude: String, longitude: String) : AsyncTask<String, Void, Void>() {

    override fun onPreExecute() {
    }

    override fun doInBackground(vararg strings: String): Void? {
        return null
    }

    override fun onPostExecute(result: Void) {
    }
}