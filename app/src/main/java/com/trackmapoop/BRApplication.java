package com.trackmapoop;

import android.app.Application;

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

        // Testing parse out
        final ParseObject parseObject = new ParseObject("TestObject");
        parseObject.put("foo", "bar");
        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                String id = parseObject.getObjectId();
                Date createdAt = parseObject.getCreatedAt();
            }
        });
    }
}
