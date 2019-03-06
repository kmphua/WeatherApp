package com.hagarsoft.weatherapp;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    private ListView listView;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        listView = (ListView)rootView.findViewById(android.R.id.list);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                WeatherLocation location = adapter.getItem(i);
                Snackbar sb = Snackbar.make(view, String.format(getString(R.string.delete_location), location.getName()), Snackbar.LENGTH_LONG);
                sb.setAction(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d(TAG, "Deleted item at position " + i);
                                viewModel.deleteLocation(i);
                                adapter.notifyDataSetChanged();
                            }
                        });
                sb.show();
                return true;
            }
        });
        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        WeatherLocation location = (WeatherLocation) getListAdapter().getItem(position);
        viewModel.selectLocation(position);   // Set selected location in view model

        if (location != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment, new WeatherFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            Snackbar.make(this.getView(), R.string.location_list_error, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Show floating action button
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.hideFab(false);
        adapter.notifyDataSetChanged();
    }

    public void refreshData() {
        Log.d(TAG, "refreshData");
        adapter.notifyDataSetChanged();
    }
}
