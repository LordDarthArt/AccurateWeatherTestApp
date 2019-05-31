package tk.lorddarthart.accurateweathertestapp.view.base

import android.content.Context
import android.support.v4.app.Fragment
import android.view.View
import tk.lorddarthart.accurateweathertestapp.view.activity.MainActivity

open class BaseFragment : Fragment() {
    lateinit var mView: View
    lateinit var mActivity: MainActivity

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivity = context as MainActivity
    }

    companion object {
        const val TAG = "BaseFragment"
    }
}