package com.hagarsoft.weatherapp;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.Date;

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
    private WeatherLocation currentLocation;
    private boolean isMetric = true;    // TODO: Link to settings

    Typeface weatherFont;
    TextView tvCity;
    TextView tvIcon;
    TextView tvCondition;
    TextView tvTemp;
    TextView tvHumidity;
    TextView tvRain;
    TextView tvWind;
    Handler handler;

    public WeatherFragment() {
        handler = new Handler();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this.getActivity()).get(WeatherLocationViewModel.class);

        viewModel.getSelectedLocation().observe(this, item -> {
            currentLocation = viewModel.getLocation(item);
            updateWeatherData(currentLocation.getLat(), currentLocation.getLon());
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        tvCity = (TextView)rootView.findViewById(R.id.tv_city);
        tvTemp = (TextView)rootView.findViewById(R.id.tv_temp);
        tvHumidity = (TextView)rootView.findViewById(R.id.tv_humidity);
        tvWind = (TextView)rootView.findViewById(R.id.tv_wind);
        tvRain = (TextView)rootView.findViewById(R.id.tv_rain);
        tvCondition = (TextView)rootView.findViewById(R.id.tv_condition);
        tvIcon = (TextView)rootView.findViewById(R.id.tv_icon);
        tvIcon.setTypeface(weatherFont);
        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item=menu.findItem(R.id.action_add_location);
        item.setVisible(false);
    }

    private void updateWeatherData(final double lat, final double lon) {
        Log.d(TAG, "updateWeatherData");
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
        try {
            // Convert JSON to Java Object
            CurrentWeather currWeather = new Gson().fromJson(json, CurrentWeather.class);

            tvCity.setText(currentLocation.getName());

            setTemp(currWeather.main.temp);

            setHumidity(currWeather.main.humidity);

            if (currWeather.rain != null) {
                setRain((int) currWeather.rain.onehour);
            } else {
                tvRain.setVisibility(View.GONE);
            }

            if (currWeather.wind != null) {
                setWindSpeed(currWeather.wind.speed, degToCompass(currWeather.wind.deg));
            } else {
                tvWind.setVisibility(View.GONE);
            }

            Weather weather = currWeather.weather[0];
            if (weather != null) {
                setCondition(weather.description);
                setWeatherIcon(weather.id,
                        currWeather.sys.sunrise * 1000,
                        currWeather.sys.sunrise * 1000);
            } else {
                tvIcon.setVisibility(View.GONE);
                tvCondition.setVisibility(View.GONE);
            }

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
        tvIcon.setText(icon);
    }


    private void setTemp(double temperature) {
        if (tvTemp != null) {
            Resources res = getResources();
            String temperatureText = res.getString(R.string.format_temp, temperature, isMetric ? "°C" : "°F");
            tvTemp.setText(temperatureText);
        }
    }

    private void setHumidity(int humidity) {
        if (tvHumidity != null) {
            Resources res = getResources();
            String humidityText = res.getString(R.string.format_humidity, humidity);
            tvHumidity.setText(humidityText);
        }
    }

    private void setRain(int rain) {
        if (tvRain != null) {
            Resources res = getResources();
            String humidityText = res.getString(R.string.format_rain, rain);
            tvRain.setText(humidityText);
        }
    }

    private void setWindSpeed(double windSpeed, String windDir) {
        if (tvWind != null) {
            Resources res = getResources();
            String windText = res.getString(R.string.format_wind,
                    windSpeed, isMetric?"m/s":"mph", windDir);
            tvWind.setText(windText);
        }
    }

    private void setCondition(String condition) {
        if (tvCondition != null) {
            tvCondition.setText(condition);
        }
    }

    private String degToCompass(double num) {
        double val = Math.floor((num / 22.5) + 0.5);
        String arr[] = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
        return arr[((int)val % 16)];
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
