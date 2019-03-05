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

import java.util.Locale;

public class WeatherLocationAdapter extends ArrayAdapter<WeatherLocation> {

    private Context context;
    private int layoutResourceId;
    private WeatherLocation data[] = null;
    private Typeface weatherFont;

    public WeatherLocationAdapter(Context context, int layoutResourceId, WeatherLocation data[]) {
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

        WeatherLocation weather = data[position];
        holder.txtName.setText(weather.name);
        holder.txtIcon.setText(weather.weatherIcon);
        holder.txtIcon.setTypeface(weatherFont);
        holder.txtTemp.setText(String.format(Locale.getDefault(), "%.0f℃", weather.tempC));

        return row;
    }

    static class LocationArrayHolder {
        TextView txtName;
        TextView txtIcon;
        TextView txtTemp;
    }
}
