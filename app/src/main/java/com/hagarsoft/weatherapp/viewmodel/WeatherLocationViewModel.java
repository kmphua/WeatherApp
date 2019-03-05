package com.hagarsoft.weatherapp.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.hagarsoft.weatherapp.data.WeatherLocation;
import com.hagarsoft.weatherapp.data.WeatherLocationsRepository;

import java.util.List;

public class WeatherLocationViewModel extends ViewModel {

    private final MutableLiveData<Integer> selectedLocation = new MutableLiveData<>();

    private WeatherLocationsRepository repository = new WeatherLocationsRepository();

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
