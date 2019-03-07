package com.hagarsoft.weatherapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v7.preference.PreferenceManager;

import com.hagarsoft.weatherapp.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility methods
 */
public class Utils {

    public static boolean isMetricSystem(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String value = prefs.getString(context.getString(R.string.key_unit_of_measure), "0");
        if (value.equalsIgnoreCase("0")) {
            return true;
        }
        return false;
    }

    public static String degToCompass(double num) {
        double val = Math.floor((num / 22.5) + 0.5);
        String arr[] = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
        return arr[((int)val % 16)];
    }

    public static String dateStringFromTimestamp(long timestamp) {
        Date d = new Date((long)timestamp*1000);
        DateFormat f = new SimpleDateFormat("MM/dd HH:mm");
        return f.format(d);
    }

    public static String getWeatherIconString(Context context, int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = context.getString(R.string.weather_sunny);
            } else {
                icon = context.getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = context.getString(R.string.weather_thunder);
                    break;
                case 3 : icon = context.getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = context.getString(R.string.weather_foggy);
                    break;
                case 8 : icon = context.getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = context.getString(R.string.weather_snowy);
                    break;
                case 5 : icon = context.getString(R.string.weather_rainy);
                    break;
            }
        }
        return icon;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
