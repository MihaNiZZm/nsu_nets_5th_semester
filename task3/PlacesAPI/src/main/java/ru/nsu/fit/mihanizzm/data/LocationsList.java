package ru.nsu.fit.mihanizzm.data;

import java.util.HashMap;

public class LocationsList {
    private final HashMap<Integer, LocationInfo> map;
    private LocationInfo selectedLocation;

    public LocationsList(HashMap<Integer, LocationInfo> map) {
        this.map = map;
    }

    public void setSelectedLocation(LocationInfo selectedLocation) {
        this.selectedLocation = selectedLocation;
    }

    public HashMap<Integer, LocationInfo> getMap() {
        return this.map;
    }

    public LocationInfo getSelectedLocation() {
        return this.selectedLocation;
    }

}
