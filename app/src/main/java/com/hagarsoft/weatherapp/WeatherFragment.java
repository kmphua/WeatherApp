package com.hagarsoft.weatherapp;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hagarsoft.weatherapp.adapter.WeatherForecastAdapter;
import com.hagarsoft.weatherapp.data.CurrentWeather;
import com.hagarsoft.weatherapp.data.Weather;
import com.hagarsoft.weatherapp.data.WeatherApiClient;
import com.hagarsoft.weatherapp.data.WeatherForecast;
import com.hagarsoft.weatherapp.data.WeatherLocation;
import com.hagarsoft.weatherapp.util.Utils;
import com.hagarsoft.weatherapp.viewmodel.WeatherLocationViewModel;

/**
 * Weather fragment to display current weather and 5-day forecast for a location
 */
public class WeatherFragment extends Fragment {
    private static final String TAG = "WeatherFragment";

    private WeatherLocationViewModel viewModel;
    private WeatherLocation currentLocation;
    private boolean isMetric = true;

    Typeface weatherFont;
    TextView tvCity;
    TextView tvIcon;
    TextView tvCondition;
    TextView tvTemp;
    TextView tvHumidity;
    TextView tvRain;
    TextView tvWind;
    ListView listView;
    ProgressBar progressBar;

    Handler handler;

    public WeatherFragment() {
        handler = new Handler();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
        isMetric = Utils.isMetricSystem(getContext());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this.getActivity()).get(WeatherLocationViewModel.class);

        viewModel.getSelectedLocation().observe(this, item -> {
            currentLocation = viewModel.getLocation(item);

            // Check network connectivity
            if (Utils.isNetworkConnected(getContext())) {
                // Show progress bar when retrieving web data
                progressBar.setVisibility(ProgressBar.VISIBLE);
                updateWeatherData(currentLocation.getLat(), currentLocation.getLon());
                updateForecastData(currentLocation.getLat(), currentLocation.getLon());
            } else {
                // Show alert dialog
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.network_error_title)
                        .setMessage(R.string.network_error_message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
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
        progressBar = (ProgressBar)rootView.findViewById(R.id.progress);
        progressBar.setIndeterminate(true);
        listView = (ListView)rootView.findViewById(R.id.list);
        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Hide floating action button
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.hideFab(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            try {
                // Reload layout on orientation change
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateWeatherData(final double lat, final double lon) {
        new Thread(){
            public void run(){
                String measurement = isMetric ? "metric" : "imperial";
                final String json = WeatherApiClient.getCurrentWeather(getActivity(), lat, lon, measurement);
                //Log.d(TAG, "JSON string = " + json);
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
                setWindSpeed(currWeather.wind.speed, Utils.degToCompass(currWeather.wind.deg));
            } else {
                tvWind.setVisibility(View.GONE);
            }

            Weather weather = currWeather.weather[0];
            if (weather != null) {
                setCondition(weather.description);
                tvIcon.setText(Utils.getWeatherIconString(getContext(),
                        weather.id,
                        currWeather.sys.sunrise * 1000,
                        currWeather.sys.sunset * 1000));
            } else {
                tvIcon.setVisibility(View.GONE);
                tvCondition.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            Log.e(TAG,"Exception occurred: " + e.getLocalizedMessage());
        }
    }

    private void updateForecastData(final double lat, final double lon){
        //Log.d(TAG, "updateForecastData");
        new Thread(){
            public void run(){
                String measurement = isMetric ? "metric" : "imperial";
                final String json = WeatherApiClient.getWeatherForecast(getActivity(), lat, lon, measurement);
                //Log.d(TAG, "JSON string = " + json);
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
            progressBar.setVisibility(ProgressBar.INVISIBLE);

            // Load saved locations from SharedPreferences
            WeatherForecastAdapter adapter = new WeatherForecastAdapter(getActivity(), R.layout.forecast_row_item, forecast.list);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            Log.e(TAG,"Exception occurred: " + e.getLocalizedMessage());
        }
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

    /*
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
    */
}
