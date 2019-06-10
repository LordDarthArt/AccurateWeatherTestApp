package tk.lorddarthart.accurateweathertestapp.util.tools

import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View

class RVClickHandler(private val mRecyclerView: RecyclerView) : View.OnTouchListener {
    private var mStartX: Float = 0.toFloat()
    private var mStartY: Float = 0.toFloat()

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        var isConsumed = false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mStartX = event.x
                mStartY = event.y
            }
            MotionEvent.ACTION_UP -> {
                val endX = event.x
                val endY = event.y
                if (detectClick(mStartX, mStartY, endX, endY)) {
                    val itemView = mRecyclerView.findChildViewUnder(endX, endY)
                    if (itemView == null) {
                        mRecyclerView.performClick()
                        isConsumed = true
                    }
                }
            }
        }
        return isConsumed
    }

    private fun detectClick(startX: Float, startY: Float, endX: Float, endY: Float): Boolean {
        return Math.abs(startX - endX) < 3.0 && Math.abs(startY - endY) < 3.0
    }

}