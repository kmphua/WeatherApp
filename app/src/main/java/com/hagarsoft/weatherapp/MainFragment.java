package com.hagarsoft.weatherapp;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.hagarsoft.weatherapp.adapter.WeatherLocationAdapter;
import com.hagarsoft.weatherapp.data.WeatherLocation;
import com.hagarsoft.weatherapp.viewmodel.WeatherLocationViewModel;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends ListFragment {
    private static final String TAG = "MainFragment";

    private WeatherLocationViewModel viewModel;
    private WeatherLocationAdapter adapter;

    public MainFragment() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this.getActivity()).get(WeatherLocationViewModel.class);

        // Load saved locations from SharedPreferences
        adapter = new WeatherLocationAdapter(getActivity(), R.layout.row_item, viewModel.getLocationList());
        setListAdapter(adapter);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            Log.d(TAG, "Fragment visible, refreshing location data");
            // Refresh location data
            refreshData();
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        WeatherLocation location = (WeatherLocation) getListAdapter().getItem(position);
        viewModel.selectLocation(position);   // Set selected location in view model

        if (location != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment, new WeatherFragment());
            //fragmentTransaction.hide(this);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            Snackbar.make(this.getView(), R.string.location_list_error, Snackbar.LENGTH_LONG).show();
        }
    }

    private void refreshData() {
        Log.d(TAG, "refreshData");
        adapter.notifyDataSetChanged();
    }
}
