package com.trackmapoop.async;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.trackmapoop.Managers.DatabaseManager;
import com.trackmapoop.Managers.WebCallsManager;
import com.trackmapoop.data.BRConstants;
import com.trackmapoop.data.NearestBathroomLocs;
import com.trackmapoop.fragments.MapFragment;
import com.trackmapoop.web.NearestBathroomsResponse;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit.RetrofitError;

/**
 * Created by mike on 8/13/14.
 */
public class NearestBRTask extends AsyncTask<Void, Void, String>{
    private Context mContext;

    public NearestBRTask(Context context)
    {
        this.mContext = context;
    }

    @Override
    protected String doInBackground(Void... params)
    {
        // Test to see what response I get from api
        try
        {
            Location currentLoc = MapFragment.currentLoc;
            NearestBathroomsResponse response = new WebCallsManager(mContext).findNearestBathrooms(currentLoc.getLatitude(), currentLoc.getLongitude());
            List<NearestBathroomLocs> locations = response.getMarkers();

            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            for (NearestBathroomLocs locs:locations)
            {
                try
                {
                    List<Address> addresses = geocoder.getFromLocation(locs.getLatitude(), locs.getLongitude(), 1);
                    if (addresses.size() > 0) {
                        Address current = addresses.get(0);
                        locs.setStreetAddress(current.getAddressLine(0));
                    }
                }
                catch (IOException e)
                {
                    Log.d("Couldn't find Address", "stupid idiot");
                }
            }

            DatabaseManager manager = DatabaseManager.openDatabase(mContext);
            manager.saveNearestLocations(locations);

            return "Success";
        }
        catch (RetrofitError e)
        {
            Log.d("Retrofit error: ", e.getMessage());
        }

        return "Failure";
    }

    @Override
    protected void onPostExecute(String result)
    {
        if (result.equalsIgnoreCase("Success"))
        {
            Intent intent = new Intent(BRConstants.NEAREST_BR_ACTION);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }
    }
}
