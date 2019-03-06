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
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this.getActivity()).get(WeatherLocationViewModel.class);

        // Load saved locations from SharedPreferences
        adapter = new WeatherLocationAdapter(getActivity(), R.layout.row_item, viewModel.getLocationList());
        setListAdapter(adapter);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.d(TAG, "onHiddenChanged = " + hidden);
        super.onHiddenChanged(hidden);
        if (!hidden) {
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
            fragmentTransaction.hide(this);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            Snackbar.make(this.getView(), R.string.location_list_error, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_location) {
            // Open map fragment to select location
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment, new MapFragment());
            fragmentTransaction.hide(this);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshData() {
        Log.d(TAG, "refreshData");
        adapter.notifyDataSetChanged();
    }
}
