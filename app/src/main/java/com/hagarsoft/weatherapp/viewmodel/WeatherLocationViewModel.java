package com.hagarsoft.weatherapp.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.hagarsoft.weatherapp.data.WeatherLocation;
import com.hagarsoft.weatherapp.data.WeatherLocationsRepository;

public class WeatherLocationViewModel extends ViewModel {
    private final MutableLiveData<String> selectedLocation = new MutableLiveData<String>();

    private WeatherLocationsRepository repository = new WeatherLocationsRepository();

    public void selectLocation(String location) {
        selectedLocation.setValue(location);
    }

    public MutableLiveData<String> getSelectedLocation() {
        return selectedLocation;
    }

    public String[] getLocationList(){
        return repository.getLocations();
    }

    public WeatherLocation getLocationDetails(String name){
        return repository.getLocationDetails(name);
    }
}
