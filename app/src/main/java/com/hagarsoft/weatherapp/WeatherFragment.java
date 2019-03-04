package com.hagarsoft.weatherapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
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

import org.json.JSONObject;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
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

    private static final String WEATHER_ICON_API = "http://openweathermap.org/img/w/%s.png";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LAT = "paramLat";
    private static final String ARG_LON = "paramLon";

    // TODO: Rename and change types of parameters
    private float mLat;
    private float mLon;

    //private OnFragmentInteractionListener mListener;

    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    ImageView weatherIcon;

    Handler handler;

    public WeatherFragment() {
        handler = new Handler();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeatherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeatherFragment newInstance(float lat, float lon) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putFloat(ARG_LAT, lat);
        args.putFloat(ARG_LON, lon);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLat = getArguments().getFloat(ARG_LAT);
            mLon = getArguments().getFloat(ARG_LON);
        }
        updateWeatherData("Tokyo");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        cityField = (TextView)rootView.findViewById(R.id.city_field);
        updatedField = (TextView)rootView.findViewById(R.id.updated_field);
        detailsField = (TextView)rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView)rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = (ImageView)rootView.findViewById(R.id.weather_icon);
        return rootView;
    }

    /*
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
*/

    /*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    */

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
*/

    private void updateWeatherData(final String city){
        new Thread(){
            public void run(){
                final String json = WeatherApiClient.getCurrentWeather(getActivity(), city);
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

            setWeatherIcon(weather.icon);
        } catch (Exception e) {
            Log.e(TAG,"Exception occurred: " + e.getLocalizedMessage());
        }
    }

    private void updateForecastData(final String city){
        new Thread(){
            public void run(){
                final String json = WeatherApiClient.getWeatherForecast(getActivity(), city);
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

    private void setWeatherIcon(String iconId) {
        String iconUrl = String.format(WEATHER_ICON_API, iconId);
        Log.d(TAG, "Weather icon URL = " + iconUrl);
        new DownloadImageTask(weatherIcon)
                .execute(iconUrl);
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
