package com.trackmapoop.Managers;

import android.content.Context;

import com.localytics.android.LocalyticsAmpSession;
import com.localytics.android.LocalyticsSession;

import java.util.Map;

/**
 * Created by mike on 3/17/15.
 */
public class AnalyticsManager
{
    public static final String TAG = "ANALYTICS_MANAGER";

    private Context mContext;
    private LocalyticsSession localyticsSession;
    private static AnalyticsManager manager;

    private AnalyticsManager(Context context)
    {
        this.mContext = context;
        localyticsSession = new LocalyticsAmpSession(mContext);
    }

    public static AnalyticsManager getInstance(Context context)
    {
        if (manager == null)
        {
            manager = new AnalyticsManager(context);
        }

        return manager;
    }

    public void tagEvent(String eventName)
    {
        localyticsSession.tagEvent(eventName);
    }

    public void tagEvent(String eventName, Map<String, String> attributes)
    {
        localyticsSession.tagEvent(eventName, attributes);
    }
}
