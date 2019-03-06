package com.hagarsoft.weatherapp.data;

import android.content.Context;
import android.util.Log;

import com.hagarsoft.weatherapp.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class WeatherApiClient {
    private static final String TAG = "WeatherApiClient";

    private static final String OPEN_WEATHER_API =
            "http://api.openweathermap.org/data/2.5/weather?lat=%.6f&lon=%.6f&units=%s";

    private static final String OPEN_FORECAST_API =
            "http://api.openweathermap.org/data/2.5/forecast?lat=%.6f&lon=%.6f&units=%s";

    public static String getCurrentWeather(Context context, double lat, double lon, String measurement){
        try {
            URL url = new URL(String.format(OPEN_WEATHER_API, lat, lon, measurement));
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key",
                    context.getString(R.string.open_weather_maps_api_key));

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer();
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            return json.toString();
        }catch(Exception e){
            Log.e(TAG,"Exception occurred: " + e.getLocalizedMessage());
            return null;
        }
    }

    public static String getWeatherForecast(Context context, double lat, double lon, String measurement){
        try {
            URL url = new URL(String.format(OPEN_FORECAST_API, lat, lon, measurement));
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key",
                    context.getString(R.string.open_weather_maps_api_key));

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(4098);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            return json.toString();
        }catch(Exception e){
            Log.e(TAG,"Exception occurred: " + e.getLocalizedMessage());
            return null;
        }
    }
}
