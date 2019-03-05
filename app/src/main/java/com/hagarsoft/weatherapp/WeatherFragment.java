package com.hagarsoft.weatherapp;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hagarsoft.weatherapp.data.CurrentWeather;
import com.hagarsoft.weatherapp.data.Weather;
import com.hagarsoft.weatherapp.data.WeatherApiClient;
import com.hagarsoft.weatherapp.data.WeatherForecast;
import com.hagarsoft.weatherapp.data.WeatherLocation;
import com.hagarsoft.weatherapp.viewmodel.WeatherLocationViewModel;

import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WeatherFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherFragment extends Fragment {
    private static final String TAG = "WeatherFragment";

    private WeatherLocationViewModel viewModel;

    Typeface weatherFont;
    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView weatherIcon;

    Handler handler;

    public WeatherFragment() {
        handler = new Handler();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");

        viewModel = ViewModelProviders.of(this.getActivity()).get(WeatherLocationViewModel.class);

        viewModel.getSelectedLocation().observe(this, item -> {
            WeatherLocation location = viewModel.getLocationDetails(item);
            updateWeatherData(location.getLat(), location.getLon());
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        cityField = (TextView)rootView.findViewById(R.id.city_field);
        updatedField = (TextView)rootView.findViewById(R.id.updated_field);
        detailsField = (TextView)rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView)rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView)rootView.findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);

        return rootView;
    }

    private void updateWeatherData(final double lat, final double lon) {
        new Thread(){
            public void run(){
                final String json = WeatherApiClient.getCurrentWeather(getActivity(), lat, lon);
                Log.d(TAG, "JSON string = " + json);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(String json){
        Gson gson = new Gson();
        try {
            // Convert JSON to Java Object
            CurrentWeather currWeather = gson.fromJson(json, CurrentWeather.class);

            cityField.setText(currWeather.name.toUpperCase(Locale.US) +
                    ", " + currWeather.sys.country);

            Weather weather = currWeather.weather[0];
            detailsField.setText(
                    weather.description.toUpperCase(Locale.US) +
                            "\n" + "Humidity: " + currWeather.main.humidity + "%" +
                            "\n" + "Pressure: " + currWeather.main.pressure + " hPa");

            currentTemperatureField.setText(
                    String.format(Locale.getDefault(), "%.2f ℃", currWeather.main.temp));

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(currWeather.dt*1000));
            updatedField.setText("Last update: " + updatedOn);

            setWeatherIcon(weather.id,
                           currWeather.sys.sunrise * 1000,
                            currWeather.sys.sunrise * 1000);

        } catch (Exception e) {
            Log.e(TAG,"Exception occurred: " + e.getLocalizedMessage());
        }
    }

    private void updateForecastData(final double lat, final double lon){
        new Thread(){
            public void run(){
                final String json = WeatherApiClient.getWeatherForecast(getActivity(), lat, lon);
                Log.d(TAG, "JSON string = " + json);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            renderForecast(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderForecast(String json){
        Gson gson = new Gson();
        try {
            // Convert JSON to Java Object
            WeatherForecast forecast = gson.fromJson(json, WeatherForecast.class);
        } catch (Exception e) {
            Log.e(TAG,"Exception occurred: " + e.getLocalizedMessage());
        }
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = getActivity().getString(R.string.weather_sunny);
            } else {
                icon = getActivity().getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 3 : icon = getActivity().getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = getActivity().getString(R.string.weather_foggy);
                    break;
                case 8 : icon = getActivity().getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 5 : icon = getActivity().getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
