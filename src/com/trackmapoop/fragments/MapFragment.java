package com.trackmapoop.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.trackmapoop.activities.R;
import com.trackmapoop.data.Bathroom;
import com.trackmapoop.data.BathroomContract;
import com.trackmapoop.data.BathroomContract.BathroomDbHelper;
import com.trackmapoop.data.BathroomContract.BathroomEntry;

public class MapFragment extends Fragment
				implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener{
	private static View view;
	private GoogleMap map;
	private LocationClient lm;
	private LatLng myLoc;
	public static Location currentLoc;
	
	// These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
                // Inflate the layout for this fragment
                view = inflater.inflate(R.layout.map_view, container, false);
                
                SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                if(mapFragment != null) {
                        map = mapFragment.getMap();

                        map.setMyLocationEnabled(true);
                }

        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }
        return view;
    }
	
	@Override
    public void onPause() {
        super.onPause();
        if (lm != null) {
            lm.disconnect();
        }
    }
	
	public void setMarkers(ArrayList<Bathroom> baths) {
		for(int i = 0; i < baths.size(); i++) {
			Bathroom tmpbath = baths.get(i);
			LatLng pos = new LatLng(tmpbath.getLat(), tmpbath.getLong());
			
			MarkerOptions marker = new MarkerOptions().position(pos).title(tmpbath.getTitle());
			map.addMarker(marker);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.map);
        map = mapFragment.getMap();
		LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
		
		//Read from the database of locations
		ArrayList<Bathroom> baths = readLocs();

		setUpLocAndZoom();
		lm.connect();
        setMarkers(baths);
	}
	
	public ArrayList<Bathroom> readLocs() {
		ArrayList<Bathroom> locs = new ArrayList<Bathroom>();
		
		BathroomContract contract = new BathroomContract();
		BathroomDbHelper mDbHelper = contract.new BathroomDbHelper(this.getActivity());
		
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		
		//projection of columns needed
		String[] projection = {
				BathroomEntry._ID,
				BathroomEntry.TITLE_COL,
				BathroomEntry.LAT_COL,
				BathroomEntry.LONG_COL };
		
		Cursor c = db.query(
				BathroomEntry.TABLE_NAME, 
				projection, 
				null, null, null, null, null);
		
		if(c.moveToFirst()) {
			do {
				Bathroom bath = new Bathroom();
				bath.setTitle(c.getString(1));
				bath.setLat(Double.parseDouble(c.getString(2)));
				bath.setLong(Double.parseDouble(c.getString(3)));
				
				//Add new bath to array
				locs.add(bath);
			} while(c.moveToNext());
		}
		
		return locs;
	}
	
	//Sets up the location client and zooms into the current location
	public void setUpLocAndZoom() {
		if(lm == null) {
			lm = new LocationClient(
					getActivity(),
					this, this);
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		//Do Nothing
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		lm.requestLocationUpdates(REQUEST, this);
		
        currentLoc = lm.getLastLocation();
        LatLng lat = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(lat, 14));
	}

	@Override
	public void onDisconnected() {
		//Do nothing
		
	}

	@Override
	public void onLocationChanged(Location location) {
		currentLoc = location;
		
	}
	
}
