package com.hagarsoft.weatherapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hagarsoft.weatherapp.R;
import com.hagarsoft.weatherapp.data.WeatherForecastItem;
import com.hagarsoft.weatherapp.data.WeatherLocation;
import com.hagarsoft.weatherapp.util.Utils;

import java.util.List;
import java.util.Locale;

public class WeatherForecastAdapter extends ArrayAdapter<WeatherForecastItem> {

    private Context context;
    private int layoutResourceId;
    private WeatherForecastItem data[];
    private Typeface weatherFont;

    public WeatherForecastAdapter(Context context, int layoutResourceId, WeatherForecastItem data[]) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        weatherFont = Typeface.createFromAsset(context.getAssets(), "fonts/weather.ttf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ForecastArrayHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ForecastArrayHolder();
            holder.txtDate = row.findViewById(R.id.txtDate);
            holder.txtHumidity = row.findViewById(R.id.txtHumidity);
            //holder.txtRain = row.findViewById(R.id.txtRain);
            holder.txtWind = row.findViewById(R.id.txtWind);
            holder.txtIcon = row.findViewById(R.id.txtIcon);
            holder.txtTemp = row.findViewById(R.id.txtTemp);

            row.setTag(holder);
        } else {
            holder = (ForecastArrayHolder)row.getTag();
        }

        WeatherForecastItem forecastItem = data[position];
        holder.txtDate.setText(Utils.dateStringFromTimestamp(forecastItem.dt));

        String weatherIconString = Utils.getWeatherIconString(getContext(),
                forecastItem.weather[0].id,
                forecastItem.sys.sunrise * 1000,
                forecastItem.sys.sunset * 1000);
        holder.txtIcon.setTypeface(weatherFont);
        holder.txtIcon.setText(weatherIconString);

        /*
        if (forecastItem.rain != null) {
            String rainText = getContext().getString(R.string.format_rain, forecastItem.rain.onehour);
            holder.txtRain.setText(rainText);
        } else {
            holder.txtRain.setVisibility(View.GONE);
        }
        */

        String humidityText = getContext().getString(R.string.format_humidity, forecastItem.main.humidity);
        holder.txtHumidity.setText(humidityText);

        if (forecastItem.wind != null) {
            String windText = getContext().getString(R.string.format_wind, forecastItem.wind.speed,
                    Utils.isMetricSystem(getContext()) ? "m/s" : "mph", Utils.degToCompass(forecastItem.wind.deg));
            holder.txtWind.setText(windText);
        } else {
            holder.txtWind.setVisibility(View.GONE);
        }

        String temperatureText = getContext().getString(R.string.format_temp, forecastItem.main.temp,
                Utils.isMetricSystem(getContext()) ? "°C" : "°F");
        holder.txtTemp.setText(temperatureText);
        return row;
    }

    static class ForecastArrayHolder {
        TextView txtDate;
        TextView txtHumidity;
        TextView txtRain;
        TextView txtWind;
        TextView txtIcon;
        TextView txtTemp;
    }

    public WeatherForecastItem getItem(int position) {
        return data[position];
    }
}
