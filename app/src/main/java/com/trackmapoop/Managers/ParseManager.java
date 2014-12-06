package com.trackmapoop.Managers;

import android.content.Context;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.trackmapoop.data.BRConstants;
import com.trackmapoop.data.Bathroom;

/**
 * Created by mike on 12/6/14.
 */
public class ParseManager
{
    public static final String TAG = "PARSE_MANAGER";

    private Context context;
    private static ParseManager manager;

    public static ParseManager getInstance(Context context)
    {
        if (manager == null)
        {
            manager = new ParseManager(context);
        }

        return manager;
    }

    private ParseManager(Context context)
    {
        this.context = context;
    }

    public void saveBathroom(final Bathroom bathroom)
    {
        final ParseObject parseBathroom = new ParseObject("Bathroom");
        ParseUser user = ParseUser.getCurrentUser();

        parseBathroom.put(BRConstants.BATHROOM_TITLE, bathroom.getTitle());
        parseBathroom.put(BRConstants.BATHROOM_LONG, bathroom.getLong());
        parseBathroom.put(BRConstants.BATHROOM_LAT, bathroom.getLat());
        parseBathroom.put(BRConstants.BATHROOM_COUNT, bathroom.getCount());
        parseBathroom.put(BRConstants.BATHROOM_USER, user);

        DatabaseManager.openDatabase(context).save(bathroom);

        // Save the bathroom to the server in background and save again with parse info
        parseBathroom.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null)
                {
                    bathroom.setParObjectId(parseBathroom.getObjectId());
                    bathroom.setParseLastUpdated(parseBathroom.getUpdatedAt());
                    bathroom.setParseCreatedAt(parseBathroom.getCreatedAt());

                    DatabaseManager.openDatabase(context).save(bathroom);
                }
                else
                {
                    DatabaseManager.openDatabase(context).save(bathroom);

                    // Save it locally but not synced to server
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    public void deleteBathroom(String bathroomTitle)
    {
        DatabaseManager.openDatabase(context).deleteBathroom(bathroomTitle);
        ParseUser user = ParseUser.getCurrentUser();

        // Delete it from the server now
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Bathroom");
        query.whereEqualTo(BRConstants.BATHROOM_USER, user);
        query.whereEqualTo(BRConstants.BATHROOM_TITLE, bathroomTitle);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (parseObject != null)
                {
                    parseObject.deleteEventually();
                }
                else
                {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }
}
