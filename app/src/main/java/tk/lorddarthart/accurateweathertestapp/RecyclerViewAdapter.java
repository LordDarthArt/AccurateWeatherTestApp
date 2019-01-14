package tk.lorddarthart.accurateweathertestapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context context;
    ArrayList<Weather> listWeather;
    View view;
    ViewHolder viewHolder;
    private OnItemTouchListener onItemTouchListener;

    public RecyclerViewAdapter(Context context, ArrayList<Weather> listWeather, OnItemTouchListener onItemTouchListener) {
        this.context = context;
        this.listWeather = listWeather;
        this.onItemTouchListener = onItemTouchListener;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.single_item, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
        Weather weather = listWeather.get(position);
        holder.tvCity.setText(weather.getWeather_city());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date date = new Date(weather.getWeather_date());
        holder.tvToday.setText(sdf.format(date));
        if (weather.getWeather_high()>0) {
            holder.tvHighLow.setText("▲ +" + String.valueOf(weather.getWeather_high()));
        } else {
            holder.tvHighLow.setText("▲ " + String.valueOf(weather.getWeather_high()));
        }
        if (weather.getWeather_low()>0) {
            holder.tvHighLow.setText(holder.tvHighLow.getText() + " ▼ +" + String.valueOf(weather.getWeather_low()));
        } else {
            holder.tvHighLow.setText(holder.tvHighLow.getText() + " ▼ " + String.valueOf(weather.getWeather_low()));
        }
        if (weather.getWeather_now()>0) {
            holder.tvCelsius.setText("+"+String.valueOf(weather.getWeather_now()));
        } else {
            holder.tvCelsius.setText(String.valueOf(weather.getWeather_now()));
        }
        holder.tvSuntime.setText("✺▲ " + weather.getWeather_sunrise()+" ✺▼ " + weather.getWeather_sunset());
    }

    @Override
    public int getItemCount() {
        return listWeather.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvCity, tvToday, tvCelsius, tvSuntime, tvHighLow;

        public ViewHolder(View itemView) {
            super(itemView);

            tvCity = itemView.findViewById(R.id.tvCity);
            tvToday = itemView.findViewById(R.id.tvDate);
            tvCelsius = itemView.findViewById(R.id.tvTemp);
            tvSuntime = itemView.findViewById(R.id.tvSuntime);
            tvHighLow = itemView.findViewById(R.id.tvHighlow);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemTouchListener.onCardViewTap(v, getLayoutPosition());
                }
            });
        }
    }
}