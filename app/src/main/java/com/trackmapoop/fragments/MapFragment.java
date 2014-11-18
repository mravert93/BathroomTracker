package com.trackmapoop.fragments;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.trackmapoop.Managers.DatabaseManager;
import com.trackmapoop.activities.R;
import com.trackmapoop.async.NearestBRTask;
import com.trackmapoop.data.BRConstants;
import com.trackmapoop.data.Bathroom;
import com.trackmapoop.data.NearestBathroomLocs;

public class MapFragment extends Fragment
				implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener{
    private static final String TAG = "MAP FRAGMENT";

    // Location update interval in millis
    private static final int UPDATE_INTERVAL = 5000;

	private static View view;
    private Button findNearestButton;
	private GoogleMap map;
	private GoogleApiClient lm;

    private LocationRequest locationRequest;
    private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
	public static Location currentLoc;

    private Context mContext;

    private boolean hasZoomed;
	
	// These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    private BroadcastReceiver onBathroomSearchComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Nearest Bathrooms Synced");
            DatabaseManager manager = DatabaseManager.openDatabase(mContext);

            List<NearestBathroomLocs> nearestBathroomLocsList = manager.getNearestBathrooms();
            setNearestMarkers(nearestBathroomLocsList);
        }
    };

    public static MapFragment newInstance(Context context)
    {
        MapFragment fragment = new MapFragment();
        fragment.mContext = context;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceBundle)
    {
        super.onCreate(savedInstanceBundle);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
    }

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

            findNearestButton = (Button) view.findViewById(R.id.findNearestButton);
            findNearestButton.setOnClickListener(this);

            setupMapIfNeeded();
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }

        // TODO: TURN BUTTON TO FIND NEAREST BATHROOMS BACK ON
        findNearestButton.setVisibility(View.GONE);

        return view;
    }
	
	@Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(onBathroomSearchComplete);

        if (lm != null) {
            lm.disconnect();
        }
    }

	@Override
	public void onResume() {
		super.onResume();

        IntentFilter filter = new IntentFilter(BRConstants.NEAREST_BR_ACTION);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(onBathroomSearchComplete, filter);

		//Read from the database of locations
        DatabaseManager manager = DatabaseManager.openDatabase(mContext);
		List<Bathroom> baths = manager.getAllBathrooms();
        List<NearestBathroomLocs> nearest = manager.getNearestBathrooms();

        setupMapIfNeeded();

        if (map != null)
        {
            map.setMyLocationEnabled(true);
        }

		//Set up location manager and set markers on map
		lm = getLocationClient();
		lm.connect();
        hasZoomed = false;
        setMarkers(baths);
        setNearestMarkers(nearest);
	}

    public void setMarkers(List<Bathroom> baths) {
        map.clear();
        for(int i = 0; i < baths.size(); i++) {
            Bathroom tmpbath = baths.get(i);
            LatLng pos = new LatLng(tmpbath.getLat(), tmpbath.getLong());

            MarkerOptions marker = new MarkerOptions().position(pos).title(tmpbath.getTitle());
            map.addMarker(marker);
        }
    }

    public void setNearestMarkers(List<NearestBathroomLocs> nearestMarkers)
    {
        for (NearestBathroomLocs nearest : nearestMarkers)
        {
            LatLng pos = new LatLng(nearest.getLatitude(), nearest.getLongitude());

            MarkerOptions marker = new MarkerOptions().position(pos).title(nearest.getStreetAddress());
            map.addMarker(marker);
        }
    }

	//Sets up the location client and zooms into the current location
	public GoogleApiClient getLocationClient() {
		if(lm == null) {
            return new GoogleApiClient.Builder(mContext)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
		}
        return lm;
	}

    private void setupMapIfNeeded()
    {
        if (map == null)
        {
            com.google.android.gms.maps.MapFragment mapFragment = (com.google.android.gms.maps.MapFragment) getActivity()
                    .getFragmentManager().findFragmentById(R.id.map);

            map = mapFragment.getMap();
        }
    }

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		//Do Nothing
		
	}

	@Override
	public void onConnected(Bundle connectionHint)
    {
        fusedLocationProviderApi.requestLocationUpdates(lm, locationRequest,this)   ;
	}

    @Override
    public void onConnectionSuspended(int i)
    {
        // no-op
    }

	@Override
	public void onLocationChanged(Location location) {
		currentLoc = location;

        if (!hasZoomed)
        {
            hasZoomed = true;
            LatLng lat = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(lat, 14));
        }
	}

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.findNearestButton)
        {
            DatabaseManager manager = DatabaseManager.openDatabase(mContext);
            manager.deleteNearBathrooms();

            NearestBRTask task = new NearestBRTask(mContext);
            task.execute();
        }
    }
}
