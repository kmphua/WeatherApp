package com.hagarsoft.weatherapp;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hagarsoft.weatherapp.data.WeatherLocation;
import com.hagarsoft.weatherapp.viewmodel.WeatherLocationViewModel;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "MapFragment";

    private WeatherLocationViewModel viewModel;
    private GoogleMap mMap;
    private String mCityName;
    private double mLat;
    private double mLon;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        viewModel = ViewModelProviders.of(this.getActivity()).get(WeatherLocationViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        // Gets the MapView from the XML layout and creates it
        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady");
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false);

        // Listen for map click events
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                List<Address> addresses;
                String cityName = "";
                try {
                    addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
                    Address address = addresses.get(0);
                    cityName = address.getLocality();
                    if (cityName == null) {
                        cityName = address.getSubAdminArea();
                    }
                    if (cityName == null) {
                        cityName = address.getAdminArea();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(point.latitude, point.longitude)).title(cityName);

                mMap.clear();
                mMap.addMarker(marker);

                mCityName = cityName;
                mLat = point.latitude;
                mLon = point.longitude;

                Log.d(TAG, "city: " + cityName + ", lat:" + point.latitude + ", lon:" + point.longitude);

                Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                        String.format(getString(R.string.add_new_location), cityName), Snackbar.LENGTH_LONG);
                snackBar.setAction(R.string.add, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "Added location " + mCityName);
                        viewModel.addLocation(new WeatherLocation(mCityName, mLat, mLon));
                    }
                });
                snackBar.show();
            }
        });

    }
}
