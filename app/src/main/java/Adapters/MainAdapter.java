package Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maruks.sergejs.weatherlogger.R;

import java.util.Collections;
import java.util.List;

import Entities.WeatherInfo;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.mainViewHolder> {

    private LayoutInflater inflater;

    // Empty collection for weather events
    List<WeatherInfo> weatherEvents = Collections.emptyList();

    // Listener member variable
    private static OnRecyclerViewItemClickListener mListener;

    // Listener interface
    public interface OnRecyclerViewItemClickListener {
        void onItemClicked(CharSequence text);
    }

    // Method that allows the parent activity or fragment to define the listener.
    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mListener = listener;
    }

    public MainAdapter(Context context, List<WeatherInfo> weatherEvents){
        inflater = LayoutInflater.from(context);
        this.weatherEvents=weatherEvents;
    }

    @Override
    public mainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.weather_display_layout, parent,false);
        mainViewHolder holder = new mainViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(mainViewHolder holder, int position) {
        WeatherInfo current = weatherEvents.get(position);
        //Context context = holder.cover_image.getContext();

        holder.temperature.setText(current.temperature.toString());
        holder.date.setText(current.dateOfEvent.toString());
        holder.card.setTag(String.valueOf(current.dataId));
    }

    @Override
    public int getItemCount() {
        return weatherEvents.size();
    }

    public static class mainViewHolder extends RecyclerView.ViewHolder {

        TextView temperature;
        TextView date;
        CardView card;

        public mainViewHolder(View itemView) {
            super(itemView);
            temperature = itemView.findViewById(R.id.tw_Temp);
            date = itemView.findViewById(R.id.tw_Date);
            card = itemView.findViewById(R.id.cw_Weather_card);

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // send the text to the listener, i.e Activity.
                    mListener.onItemClicked((CharSequence) v.getTag());
                }
            });

        }
    }
}