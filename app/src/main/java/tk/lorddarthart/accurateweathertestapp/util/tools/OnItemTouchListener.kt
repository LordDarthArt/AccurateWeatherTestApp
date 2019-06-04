package tk.lorddarthart.accurateweathertestapp.util.tools

import android.view.View

interface OnItemTouchListener {
    /**
     * Callback invoked when the user Taps one of the RecyclerView items
     *
     * @param view     the CardView touched
     * @param position the index of the item touched in the RecyclerView
     */
    fun onCardViewTap(view: View, position: Int)

    /**
     * Callback invoked when the Button1 of an item is touched
     *
     * @param view     the Button touched
     * @param position the index of the item touched in the RecyclerView
     */

    fun onButtonCvMenuClick(view: View, position: Int)

}