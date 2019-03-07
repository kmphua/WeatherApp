package com.hagarsoft.weatherapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;

import com.hagarsoft.weatherapp.data.WeatherLocation;
import com.hagarsoft.weatherapp.data.WeatherLocationsRepository;

import java.util.List;

/**
 * View model for bookmarked locations
 */
public class WeatherLocationViewModel extends AndroidViewModel {

    public WeatherLocationViewModel(Application application) {
        super(application);
    }

    private final MutableLiveData<Integer> selectedLocation = new MutableLiveData<>();

    private WeatherLocationsRepository repository = new WeatherLocationsRepository(getApplication().getApplicationContext());

    public void selectLocation(int i) {
        selectedLocation.setValue(i);
    }

    public MutableLiveData<Integer> getSelectedLocation() {
        return selectedLocation;
    }

    public List<WeatherLocation> getLocationList() {
        return repository.getLocations();
    }

    public WeatherLocation getLocation(int i) {
        return repository.getLocation(i);
    }

    public void addLocation(WeatherLocation location) {
        repository.addLocation(location);
    }

    public void deleteLocation(int i) {
        repository.deleteLocation(i);
    }
}
