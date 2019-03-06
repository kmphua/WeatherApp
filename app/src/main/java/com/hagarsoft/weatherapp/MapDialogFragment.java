package com.hagarsoft.weatherapp;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
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
 * {@link MapDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MapDialogFragment extends DialogFragment implements OnMapReadyCallback {
    private static final String TAG = "MapDialogFragment";

    private WeatherLocationViewModel viewModel;
    private GoogleMap mMap;
    private String mCityName;
    private double mLat;
    private double mLon;

    private RelativeLayout layoutAlert;
    private TextView tvAlert;
    private Button btnAddLocation;
    private Button btnCancel;

    public MapDialogFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        View view = inflater.inflate(R.layout.fragment_map_dialog, container, false);
        final SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        layoutAlert = view.findViewById(R.id.layout_alert);
        tvAlert = view.findViewById(R.id.tv_alert);
        btnAddLocation = view.findViewById(R.id.btn_ok);
        btnAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add location and dismiss alert window
                Log.d(TAG, "Added location " + mCityName);
                viewModel.addLocation(new WeatherLocation(mCityName, mLat, mLon));
                layoutAlert.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), String.format(getString(R.string.added_location), mCityName), Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel = view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss alert window
                layoutAlert.setVisibility(View.INVISIBLE);
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState Called");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SupportMapFragment f = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        if (f != null) {
            getFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
        }
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady");
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false);

        LatLng latLng = new LatLng(37.7688472,-122.4130859);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,8));

        // Listen for map click events
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                List<Address> addresses;
                String cityName = null;
                try {
                    addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
                    if (addresses != null && addresses.size() > 0) {
                        Address address = addresses.get(0);
                        cityName = address.getLocality();
                        if (cityName == null) {
                            cityName = address.getSubAdminArea();
                        }
                        if (cityName == null) {
                            cityName = address.getAdminArea();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (cityName != null) {
                    MarkerOptions marker = new MarkerOptions().position(
                            new LatLng(point.latitude, point.longitude)).title(cityName);

                    mMap.clear();
                    mMap.addMarker(marker);

                    mCityName = cityName;
                    mLat = point.latitude;
                    mLon = point.longitude;

                    Log.d(TAG, "city: " + cityName + ", lat:" + point.latitude + ", lon:" + point.longitude);

                    tvAlert.setText(String.format(getString(R.string.add_new_location), mCityName));
                    layoutAlert.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getContext(), getString(R.string.place_not_found), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
