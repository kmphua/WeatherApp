package com.hagarsoft.weatherapp.data;

import java.util.HashMap;

public class WeatherLocationsRepository {

    private String locations[];
    private HashMap<String, WeatherLocation> locationDetails;

    public String[] getLocations(){
        if(locations == null){
            locations = new String[3];
            locations[0] = "Tokyo";
            locations[1] = "London";
            locations[2] = "New York";
        }
        return locations;
    }

    public WeatherLocation getLocationDetails(String name){
        if(locationDetails == null){
            createLocationDetailsMap();
        }
        return locationDetails.get(name);
    }

    public void createLocationDetailsMap(){
        locationDetails = new HashMap<String, WeatherLocation>();

        WeatherLocation location = new WeatherLocation("Tokyo", 35.6895, 139.6917);
        locationDetails.put("Tokyo", location);

        location = new WeatherLocation("London", 51.5074, -0.1278);
        locationDetails.put("London", location);

        location = new WeatherLocation("New York", 40.7128, -74.0060);
        locationDetails.put("New York", location);
    }
}