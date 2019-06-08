import android.os.AsyncTask
import tk.lorddarthart.accurateweathertestapp.application.view.fragment.MainFragment

class UpdateForecast(
        fragment: MainFragment,
        city: String,
        latitude: String,
        longitude: String
): AsyncTask<String, Void, Void>() { // This class is no longer useful

    override fun onPreExecute() {
    }

    override fun doInBackground(vararg strings: String): Void? {
        return null
    }

    override fun onPostExecute(result: Void) {
    }
}