package tk.lorddarthart.accurateweathertestapp;

import android.view.View;

public interface OnItemTouchListener {
    /**
     * Callback invoked when the user Taps one of the RecyclerView items
     *
     * @param view     the CardView touched
     * @param position the index of the item touched in the RecyclerView
     */
    void onCardViewTap(View view, int position);

    /**
     * Callback invoked when the Button1 of an item is touched
     *
     * @param view     the Button touched
     * @param position the index of the item touched in the RecyclerView
     */

    void onButtonCvMenuClick(View view, int position);

}