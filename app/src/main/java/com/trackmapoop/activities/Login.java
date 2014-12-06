package com.trackmapoop.activities;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.trackmapoop.BRPreferenceManager;
import com.trackmapoop.json.JSONParser;

public class Login extends Activity implements OnClickListener{
	public static final String TAG = "LOGIN";

	private EditText username, password, confirmPassword;
	private Button login, register, submit;
    private LinearLayout registerSection;
	
	//progress dialog
	private ProgressDialog progress;

    private BRPreferenceManager prefs;

	//JSON element ids from json response
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();

        setContentView(R.layout.activity_login);

		//input fields
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
		
		//buttons
		register = (Button) findViewById(R.id.register);
		login = (Button) findViewById(R.id.login);
        submit = (Button) findViewById(R.id.submitButton);

        registerSection = (LinearLayout) findViewById(R.id.registerSection);
        confirmPassword.setVisibility(View.GONE);
		
		//setup listeners
		register.setOnClickListener(this);
		login.setOnClickListener(this);
        submit.setOnClickListener(this);

        // Init preference manager
        prefs = BRPreferenceManager.getInstance(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	//Onclick
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.login:
        {
            confirmPassword.setVisibility(View.GONE);
            break;
        }
		case R.id.register:
        {
            confirmPassword.setVisibility(View.VISIBLE);
            break;
        }
        case R.id.submitButton:
        {
            final String newUsername = username.getText().toString();
            String newPassword = password.getText().toString();

            // If this is a new user
            if (confirmPassword.getVisibility() == View.VISIBLE)
            {
                String newConfirmed = confirmPassword.getText().toString();

                if (newPassword.equals(newConfirmed))
                {
                    final ParseUser newUser = new ParseUser();
                    newUser.setUsername(newUsername);
                    newUser.setPassword(newPassword);

                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null)
                            {
                                prefs.setParseUsername(newUsername);
                                prefs.setParseSessionToken(newUser.getSessionToken());

                                Intent intent = new Intent(Login.this, MainTabs.class);
                                startActivity(intent);
                            }
                            else
                            {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    });
                }
                else
                {
                    AlertDialog alertDialog = new AlertDialog.Builder(this)
                            .setTitle(getResources().getString(R.string.password_mismatch))
                            .setMessage(getResources().getString(R.string.password_mismatch_desc))
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // no-op
                                }
                            }).show();
                }
            }
            // Else this user is logging in
            else
            {
                ParseUser.logInInBackground(newUsername, newPassword, new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e)
                    {
                        if (parseUser != null)
                        {
                            prefs.setParseUsername(parseUser.getUsername());
                            prefs.setParseSessionToken(parseUser.getSessionToken());

                            Intent intent = new Intent(Login.this, MainTabs.class);
                            startActivity(intent);
                        }
                        else
                        {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                });
            }

            break;
        }
		default:
			break;
		}
	}

}
