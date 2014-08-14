package com.trackmapoop.web;

import com.trackmapoop.data.NearestBathroomLocs;

import java.util.List;

/**
 * Created by mike on 8/13/14.
 */
public class NearestBathroomsResponse {
    private List<NearestBathroomLocs> markers;

    public NearestBathroomsResponse() {}

    public List<NearestBathroomLocs> getMarkers() {
        return markers;
    }

    public void setMarkers(List<NearestBathroomLocs> markers) {
        this.markers = markers;
    }
}
