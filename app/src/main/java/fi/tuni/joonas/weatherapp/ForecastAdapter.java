package fi.tuni.joonas.weatherapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Adapter that helps populate the RecyclerView in main activity.
 *
 * @author Joonas Salojärvi
 * @version 2019.04.22
 * @since 2019.04.22
 */
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    /**
     * All forecasts for a single location.
     */
    private List<Forecast> forecasts;

    public ForecastAdapter(List<Forecast> forecasts){
        this.forecasts = forecasts;
    }

    /**
     * Method from super. Inflates a single row item.
     *
     * @param parent from super
     * @param viewType from super
     * @return ViewHolder, that holds a single row item
     */
    @Override
    public ForecastAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View forecastView = inflater.inflate(R.layout.forecast_row, parent, false);

        ViewHolder viewHolder = new ViewHolder(forecastView);
        return viewHolder;
    }

    /**
     * Sets texts to ViewHolder
     *
     * @param viewHolder ViewHolder to access
     * @param position Position of ViewHolder
     */
    @Override
    public void onBindViewHolder(ForecastAdapter.ViewHolder viewHolder, int position) {
        Forecast forecast = forecasts.get(position);

        viewHolder.date.setText(forecast.time);
        viewHolder.icon.setImageResource(forecast.icon);
        viewHolder.condition.setText(forecast.desc);
        viewHolder.temp.setText(forecast.temp + " °C");
        viewHolder.wind.setText(forecast.wind + " m/s");
    }

    /**
     * Amount of forecasts
     * @return Amount of forecasts
     */
    @Override
    public int getItemCount() {
        return forecasts.size();
    }

    /**
     * Class that helps changing the texts for a forecast.
     *
     * @author Joonas Salojärvi
     * @version 2019.04.22
     * @since 2019.04.22
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        ImageView icon;
        TextView condition;
        TextView temp;
        TextView wind;
        public ViewHolder(View itemView) {
            super(itemView);

            date = (TextView) itemView.findViewById(R.id.date);
            icon = (ImageView) itemView.findViewById(R.id.row_icon);
            condition = (TextView) itemView.findViewById(R.id.condition);
            temp = (TextView) itemView.findViewById(R.id.row_temp);
            wind = (TextView) itemView.findViewById(R.id.row_wind);
        }
    }
}
