package com.trackmapoop;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.trackmapoop.data.BRConstants;

/**
 * Created by mike on 12/6/14.
 */
public class BRPreferenceManager
{
    private static BRPreferenceManager instance;

    private SharedPreferences preferences;

    public static BRPreferenceManager getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new BRPreferenceManager(context);
        }

        return instance;
    }

    private BRPreferenceManager(Context context)
    {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setParseUsername(String username)
    {
        preferences.edit().putString(BRConstants.PARSE_USERNAME, username).commit();
    }

    public String getParseUsername()
    {
        return preferences.getString(BRConstants.PARSE_USERNAME, null);
    }

    public void setParseSessionToken(String sessionToken)
    {
        preferences.edit().putString(BRConstants.PARSE_SESSION_TOKEN, sessionToken).commit();
    }

    public String getParseSessionToken()
    {
        return preferences.getString(BRConstants.PARSE_SESSION_TOKEN, null);
    }
}
