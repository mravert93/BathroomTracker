package com.trackmapoop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.crashlytics.android.Crashlytics;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.trackmapoop.activities.Login;
import com.trackmapoop.activities.MainTabs;
import com.trackmapoop.activities.R;

public class BRSplashScreen extends Activity
{
    private static int TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();

        setContentView(R.layout.splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String user = BRPreferenceManager.getInstance(BRSplashScreen.this).getParseUsername();
                ParseUser parseUser = ParseUser.getCurrentUser();

                if (parseUser == null)
                {
                    ParseAnonymousUtils.logIn(new LogInCallback() {

                        @Override
                        public void done(ParseUser parseUser, ParseException e)
                        {
                            showMainTabs(parseUser);
                        }
                    });
                }
                else
                {
                    showMainTabs(parseUser);
                }

                // Uncomment this once you're ready for user system
//                if (user == null && parseUser == null)
//                {
//                    Intent i = new Intent(BRSplashScreen.this, Login.class);
//                    startActivity(i);
//
//                    finish();
//                }
//                else
//                {
//                    BRPreferenceManager.getInstance(BRSplashScreen.this).setParseUsername(parseUser.getUsername());
//
//                    Intent i = new Intent(BRSplashScreen.this, MainTabs.class);
//                    startActivity(i);
//
//                    finish();
//                }
            }
        }, TIME_OUT);
    }

    void showMainTabs(ParseUser parseUser)
    {
        BRPreferenceManager.getInstance(BRSplashScreen.this).setParseUsername(parseUser.getUsername());

        Intent i = new Intent(BRSplashScreen.this, MainTabs.class);
        startActivity(i);

        finish();
    }
}
