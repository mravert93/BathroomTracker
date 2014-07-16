package com.trackmapoop.activities;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.model.LatLng;
import com.trackmapoop.data.Bathroom;
import com.trackmapoop.data.BathroomContract;
import com.trackmapoop.data.BathroomContract.BathroomDbHelper;
import com.trackmapoop.data.BathroomContract.BathroomEntry;
import com.trackmapoop.data.MyArrayAdapter;
import com.trackmapoop.data.TabsAdapter;
import com.trackmapoop.dialog.CountDialog;
import com.trackmapoop.dialog.CustomDialog;
import com.trackmapoop.dialog.SelectDialog;
import com.trackmapoop.fragments.HomeFragment;
import com.trackmapoop.fragments.MapFragment;

public class MainTabs extends FragmentActivity implements CustomDialog.NoticeDialogListener, ActionBar.TabListener,
								CountDialog.NoticeDialogListener, SelectDialog.NoticeDialogListener{
	private ViewPager viewPager;
	private TabsAdapter mTabsAdapter;
	private ActionBar mActionBar;
	LocationManager lm;
	Dialog dialog;
	EditText newTitle;
	public static ArrayList<String> allLocs = new ArrayList<String>();
	public static LatLng markerLoc;
	public static String mTitle;
	
	//Titles -- Temporary, adding images instead after this works
	private int[] tabs = { R.drawable.list, R.drawable.map };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Crashlytics.start(this);
		setContentView(R.layout.activity_main_tabs);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
        //Initialize
        viewPager = (ViewPager) findViewById(R.id.pager);
        mActionBar = getActionBar();
        mTabsAdapter = new TabsAdapter(getSupportFragmentManager());
        
        viewPager.setAdapter(mTabsAdapter);
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setStackedBackgroundDrawable(getResources().getDrawable(R.drawable.back));
        
        for(int tab_icon: tabs) {
        	mActionBar.addTab(mActionBar.newTab().setIcon(tab_icon).setTabListener(this));
        }
        
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				mActionBar.setSelectedNavigationItem(arg0);
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_tabs, menu);
		return true;
	}
	
	public void addLoc(View view) {
        
		CustomDialog dialog = new CustomDialog();
		dialog.show(getSupportFragmentManager(), "addPlace");
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog, String title) {
		// TODO Auto-generated method stub
        Location location = MapFragment.currentLoc;
        
        if(location != null) {
                Bathroom newBath = new Bathroom();
                newBath.setTitle(title);
                newBath.setLat(location.getLatitude());
                newBath.setLong(location.getLongitude());
                newBath.setCount(1);
                
                //Add new bathroom to database
                BathroomContract contract = new BathroomContract();
                BathroomDbHelper mdbHelper = contract.new BathroomDbHelper(this);
                
                SQLiteDatabase db = mdbHelper.getWritableDatabase();
                
                //Content values
                ContentValues entry = new ContentValues();
                entry.put(BathroomEntry.TITLE_COL, title);
                entry.put(BathroomEntry.LAT_COL, location.getLatitude());
                entry.put(BathroomEntry.LONG_COL, location.getLongitude());
                entry.put(BathroomEntry.COUNT, newBath.getCount());
                
                //insert the newest row
                long newRowId;
                newRowId = db.insert(BathroomEntry.TABLE_NAME, null, entry);
        }
        else {
        	Toast.makeText(getApplicationContext(), "Unable to Find Location", Toast.LENGTH_LONG);
        }
        ListView locs = (ListView) findViewById(R.id.locList);
        HomeFragment home = new HomeFragment();
        home.setAdapter(locs, this);
        ((MyArrayAdapter) locs.getAdapter()).notifyDataSetChanged();
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog, int newcount, String title) {
		BathroomContract contract = new BathroomContract();
		BathroomDbHelper mDbHelper = contract.new BathroomDbHelper(this);
		
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		
		//projection of columns needed
		String[] projection = {
				BathroomEntry._ID,
				BathroomEntry.TITLE_COL,
				BathroomEntry.LAT_COL,
				BathroomEntry.LONG_COL, 
				BathroomEntry.COUNT };
		
		Cursor c = db.query(
				BathroomEntry.TABLE_NAME, 
				projection, 
				null, null, null, null, null);
		
		if(c.moveToFirst()) {
			do {
				if(c.getString(1).equals(title)) {
					Bathroom bath = new Bathroom();
					bath.setTitle(c.getString(1));
					bath.setLat(Double.parseDouble(c.getString(2)));
					bath.setLong(Double.parseDouble(c.getString(3)));
					bath.setCount(newcount);
					
					updateRow(bath);
				}
			} while(c.moveToNext());
		}
        ListView locs = (ListView) findViewById(R.id.locList);
        HomeFragment home = new HomeFragment();
        home.setAdapter(locs, this);
        ((MyArrayAdapter) locs.getAdapter()).notifyDataSetChanged();
	}
	
	public void updateRow(Bathroom bath) {
        //Add new bathroom to database
        BathroomContract contract = new BathroomContract();
        BathroomDbHelper mdbHelper = contract.new BathroomDbHelper(this);
        
        SQLiteDatabase db = mdbHelper.getWritableDatabase();
        
        db.delete(BathroomEntry.TABLE_NAME, BathroomEntry.TITLE_COL + "=?", new String[] {bath.getTitle()});
        
        //Content values
        ContentValues entry = new ContentValues();
        entry.put(BathroomEntry.TITLE_COL, bath.getTitle());
        entry.put(BathroomEntry.LAT_COL, bath.getLat());
        entry.put(BathroomEntry.LONG_COL, bath.getLong());
        entry.put(BathroomEntry.COUNT, bath.getCount());
        
        //insert the newest row
        long newRowId;
        newRowId = db.insert(BathroomEntry.TABLE_NAME, null, entry);
	}

	@Override
	public void onTabReselected(Tab arg0, android.app.FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab arg0, android.app.FragmentTransaction arg1) {
		viewPager.setCurrentItem(arg0.getPosition());
		
		//If maps tab is selected update the map
		if(arg0.getPosition() == 1) {
			TabsAdapter adapter = (TabsAdapter) viewPager.getAdapter();
			MapFragment mapFrag = (MapFragment) adapter.getItem(arg0.getPosition());
			mapFrag.onResume();
		}
	}

	@Override
	public void onTabUnselected(Tab arg0, android.app.FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

}
