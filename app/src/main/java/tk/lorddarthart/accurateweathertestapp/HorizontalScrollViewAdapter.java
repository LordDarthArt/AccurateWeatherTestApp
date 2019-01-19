package tk.lorddarthart.accurateweathertestapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;

class HorizontalRecyclerViewAdapter extends RecyclerView.Adapter<HorizontalRecyclerViewAdapter.ViewHolder> {
    //Адаптер для создания карточек с погодой на неделю в подробном просмотре.
    Context context;
    LinkedList<WeatherDay> listWeatherDay;
    View view;
    HorizontalRecyclerViewAdapter.ViewHolder viewHolder;

    public HorizontalRecyclerViewAdapter(Context context, LinkedList<WeatherDay> listWeatherDay) {
        this.context = context;
        this.listWeatherDay = listWeatherDay;
    }

    @Override
    public HorizontalRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.single_item_horizontal, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalRecyclerViewAdapter.ViewHolder viewHolder, int i) {
        WeatherDay weatherDay = listWeatherDay.get(i);
        if (weatherDay.getWeather_hight()>0) {
            viewHolder.tempHighDay.setText("+"+String.valueOf(weatherDay.getWeather_hight()));
        } else {
            viewHolder.tempHighDay.setText(String.valueOf(weatherDay.getWeather_hight()));
        }
        if (weatherDay.getWeather_low()>0.0) {
            viewHolder.tempLowDay.setText("+" + String.valueOf(weatherDay.getWeather_low()));
        } else {
            viewHolder.tempLowDay.setText(String.valueOf(weatherDay.getWeather_low()));
        }
        viewHolder.descDay.setText(weatherDay.getWeather_text());
        viewHolder.nameDay.setText(weatherDay.getWeather_day());

    }

    @Override
    public int getItemCount() {
        return listWeatherDay.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tempHighDay, tempLowDay, nameDay, descDay;

        public ViewHolder(View itemView) {
            super(itemView);

            tempHighDay = itemView.findViewById(R.id.tempHighDay);
            tempLowDay = itemView.findViewById(R.id.tempDayLow);
            descDay = itemView.findViewById(R.id.descDay);
            nameDay = itemView.findViewById(R.id.nameDay);
        }
    }
}
