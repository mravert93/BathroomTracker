package com.trackmapoop;

import android.app.Application;

import com.localytics.android.LocalyticsActivityLifecycleCallbacks;
import com.localytics.android.LocalyticsAmpSession;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.trackmapoop.data.BRConstants;

import java.util.Date;

/**
 * Created by mike on 11/23/14.
 */
public class BRApplication extends Application {

    @Override
    public void onCreate()
    {
        // Initialize Parse
        Parse.initialize(this, BRConstants.PARSE_APP_KEY, BRConstants.PARSE_CLIENT_KEY);

        // Initialize Localytics
        registerActivityLifecycleCallbacks(new LocalyticsActivityLifecycleCallbacks(new LocalyticsAmpSession(this)));
    }
}
