package com.trackmapoop.activities;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.trackmapoop.json.JSONParser;

public class Login extends Activity implements OnClickListener{
	
	private EditText username, password;
	private Button login, register;
	
	//progress dialog
	private ProgressDialog progress;
	
	//JSON parser class
	JSONParser jsonParser = new JSONParser();
	
	//php script location
	//localhost
	private static final String LOGIN_URL = 
			"http://ec2-54-187-96-4.us-west-2.compute.amazonaws.com/";
	
	//JSON element ids from json response
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		//input fields
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		
		//buttons
		register = (Button) findViewById(R.id.register);
		login = (Button) findViewById(R.id.login);
		
		//setup listeners
		register.setOnClickListener(this);
		login.setOnClickListener(this);
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
			new AttemptLogin().execute();
			break;
		case R.id.register:
			//Intent i = new Intent(this, Register.class);
			//startActivity(i);
			break;
		default:
			break;
		}
	}
	
	//An async class that will do any sort of networking
	class AttemptLogin extends AsyncTask<String, String, String> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress = new ProgressDialog(Login.this);
			progress.setMessage("Attempting Login...");
			progress.setIndeterminate(false);
			progress.setCancelable(true);
			progress.show();
		}
		
		@Override
		protected String doInBackground(String... args) {
			//Check for success tag
			int success;
			String user = username.getText().toString();
			String pass = password.getText().toString();
			
			try {
				//Build the parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("username", user));
				params.add(new BasicNameValuePair("password", pass));
				
				Log.d("Request!", "Starting!");
				//Making http request
				JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST", params);
				
				//Check log for http response
				Log.d("Login attempt", json.toString());
				
				//json success tag
				success = json.getInt(TAG_SUCCESS);
				if(success == 1) {
					Log.d("Login Successful!", json.toString());
					Intent i = new Intent(Login.this, MainTabs.class);
					finish();
					startActivity(i);
					return json.getString(TAG_MESSAGE);
				} else {
					Log.d("Login Failure!", json.getString(TAG_MESSAGE));
					return json.getString(TAG_MESSAGE);
				}
			} catch(JSONException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String file_url) {
			//dismiss the dialog
			progress.dismiss();
			if(file_url != null) {
				Toast.makeText(Login.this, file_url, Toast.LENGTH_LONG).show();
			}
		}
	}

}
