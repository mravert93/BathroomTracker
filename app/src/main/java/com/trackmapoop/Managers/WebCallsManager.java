package com.trackmapoop.Managers;

import android.content.Context;

import com.trackmapoop.activities.R;
import com.trackmapoop.data.BRConstants;
import com.trackmapoop.web.BRWebClient;
import com.trackmapoop.web.NearestBathroomsResponse;

/**
 * Created by mike on 8/13/14.
 */
public class WebCallsManager {

    private Context context;

    public WebCallsManager(Context context)
    {
        this.context = context;
    }

    public NearestBathroomsResponse findNearestBathrooms(double latitude, double longitude)
    {
        String amenity = context.getResources().getString(R.string.nearest_amenity);
        String mode = context.getResources().getString(R.string.nearest_mode);

        return new BRWebClient(context).buildRequest().getNearestBathrooms(amenity, latitude, longitude,
                mode, BRConstants.NEAREST_BR_USERNAME, BRConstants.NEAREST_BR_API_KEY);
    }
}
