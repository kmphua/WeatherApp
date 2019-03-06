package com.hagarsoft.weatherapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hagarsoft.weatherapp.R;
import com.hagarsoft.weatherapp.data.WeatherLocation;

import java.util.List;
import java.util.Locale;

public class WeatherLocationAdapter extends ArrayAdapter<WeatherLocation> {

    private Context context;
    private int layoutResourceId;
    private List<WeatherLocation> data;
    private Typeface weatherFont;

    public WeatherLocationAdapter(Context context, int layoutResourceId, List<WeatherLocation> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        weatherFont = Typeface.createFromAsset(context.getAssets(), "fonts/weather.ttf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LocationArrayHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new LocationArrayHolder();
            holder.txtName = row.findViewById(R.id.txtName);
            holder.txtIcon = row.findViewById(R.id.txtIcon);
            holder.txtTemp = row.findViewById(R.id.txtTemp);

            row.setTag(holder);
        } else {
            holder = (LocationArrayHolder)row.getTag();
        }

        WeatherLocation weather = data.get(position);
        holder.txtName.setText(weather.getName());

        if (weather.getLastUpdated() > 0) {
            holder.txtIcon.setText(weather.getWeatherIcon());
            holder.txtIcon.setTypeface(weatherFont);
            holder.txtTemp.setText(String.format(Locale.getDefault(), "%.0f℃", weather.getTempCeisius()));
        } else {
            holder.txtIcon.setText("");
            holder.txtTemp.setText("--℃");
        }
        return row;
    }

    static class LocationArrayHolder {
        TextView txtName;
        TextView txtIcon;
        TextView txtTemp;
    }

    public WeatherLocation getItem(int position) {
        return data.get(position);
    }

    public void addItem(WeatherLocation location) {
        data.add(location);
    }

    public void deleteItem(int position) {
        data.remove(position);
    }
}
