package tk.lorddarthart.accurateweathertestapp

import android.support.test.rule.ActivityTestRule
import android.support.v4.app.Fragment
import org.junit.Assert
import tk.lorddarthart.accurateweathertestapp.application.view.activity.MainActivity

class FragmentTestRule<F : Fragment>(private val mFragmentClass: Class<F>) :
        ActivityTestRule<MainActivity>(
                MainActivity::class.java,
                true,
                false
        ){
    var fragment: F? = null
        private set

    override fun afterActivityLaunched() {
        super.afterActivityLaunched()

        activity.runOnUiThread {
            try {
                //Instantiate and insert the fragment into the container layout
                val manager = activity.supportFragmentManager
                val transaction = manager.beginTransaction()
                fragment = mFragmentClass.newInstance()
                transaction.replace(R.id.mainFragment, fragment!!)
                transaction.commitAllowingStateLoss()
            } catch (e: InstantiationException) {
                Assert.fail(String.format("%s: Could not insert %s into TestActivity: %s",
                        javaClass.simpleName,
                        mFragmentClass.simpleName,
                        e.message))
            } catch (e: IllegalAccessException) {
                Assert.fail(
                        String.format(
                                "%s: Could not insert %s into TestActivity: %s",
                                javaClass.simpleName,
                                mFragmentClass.simpleName,
                                e.message
                        )
                )
            }
        }
    }
}