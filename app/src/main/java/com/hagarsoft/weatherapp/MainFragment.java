package com.hagarsoft.weatherapp;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.hagarsoft.weatherapp.adapter.WeatherLocationAdapter;
import com.hagarsoft.weatherapp.data.WeatherLocation;
import com.hagarsoft.weatherapp.util.DataStoreUtil;
import com.hagarsoft.weatherapp.viewmodel.WeatherLocationViewModel;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends ListFragment {
    private static final String TAG = "MainFragment";

    private WeatherLocationViewModel viewModel;
    private List<WeatherLocation> mLocations;

    public MainFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this.getActivity()).get(WeatherLocationViewModel.class);

        // Load saved locations from SharedPreferences
        mLocations = DataStoreUtil.readLocationData(getContext());
        WeatherLocationAdapter adapter = new WeatherLocationAdapter(getActivity(),
                R.layout.row_item, mLocations);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        WeatherLocation location = (WeatherLocation) getListAdapter().getItem(position);
        viewModel.selectLocation(location.getName());   // Set selected location in view model

        if (location != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment, new WeatherFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            Snackbar.make(this.getView(), R.string.location_list_error, Snackbar.LENGTH_LONG).show();
        }
    }
}
