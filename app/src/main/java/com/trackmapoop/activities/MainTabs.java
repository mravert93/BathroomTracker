package com.trackmapoop.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.model.LatLng;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.trackmapoop.Managers.DatabaseManager;
import com.trackmapoop.Managers.ParseManager;
import com.trackmapoop.Managers.WebCallsManager;
import com.trackmapoop.data.BRConstants;
import com.trackmapoop.data.Bathroom;
import com.trackmapoop.data.MyArrayAdapter;
import com.trackmapoop.data.TabsAdapter;
import com.trackmapoop.dialog.CountDialog;
import com.trackmapoop.dialog.CustomDialog;
import com.trackmapoop.dialog.SelectDialog;
import com.trackmapoop.fragments.HomeFragment;
import com.trackmapoop.fragments.MapFragment;
import com.trackmapoop.web.NearestBathroomsResponse;

public class MainTabs extends FragmentActivity implements CustomDialog.NoticeDialogListener, ActionBar.TabListener,
								CountDialog.NoticeDialogListener, SelectDialog.NoticeDialogListener{
    public static final String TAG = "MAIN_TABS";

    public static final String UNSYNCED_BATHROOMS = "unsynced_bathrooms";

	private ViewPager viewPager;
	private TabsAdapter mTabsAdapter;
	private ActionBar mActionBar;
	LocationManager lm;
	Dialog dialog;
	EditText newTitle;
	public static ArrayList<String> allLocs = new ArrayList<String>();
	public static LatLng markerLoc;
	public static String mTitle;

    private List<Bathroom> unsyncedBathrooms;

	private int[] tabs = { R.drawable.list, R.drawable.map };

    BroadcastReceiver unsyncedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showUnsyncedDialog();
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_tabs);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
        //Initialize
        viewPager = (ViewPager) findViewById(R.id.pager);
        mActionBar = getActionBar();
        mTabsAdapter = new TabsAdapter(getSupportFragmentManager(), this);
        
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

        checkUnsyncedBRs();
	}

    @Override
    public void onResume()
    {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(unsyncedReceiver, new IntentFilter(UNSYNCED_BATHROOMS));
    }

    @Override
    public void onPause()
    {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(unsyncedReceiver);
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
        Location location = MapFragment.currentLoc;
        
        if(location != null)
        {
            Bathroom newBath = new Bathroom();
            newBath.setTitle(title);
            newBath.setLat(location.getLatitude());
            newBath.setLong(location.getLongitude());
            newBath.setCount(1);

            ParseManager.getInstance(MainTabs.this).saveBathroom(newBath);
        }
        else
        {
        	Toast.makeText(getApplicationContext(), "Unable to Find Location", Toast.LENGTH_LONG);
        }
        ListView locs = (ListView) findViewById(R.id.locList);
        HomeFragment home = new HomeFragment();
        home.setAdapter(locs, this);
        ((MyArrayAdapter) locs.getAdapter()).notifyDataSetChanged();
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog, int newcount, String title) {
        DatabaseManager manager = DatabaseManager.openDatabase(this);
        Bathroom bathroom = manager.getBathroom(title);

        bathroom.setCount(newcount);

        ParseManager.getInstance(this).saveBathroom(bathroom);

        // Update list on home tab
        ListView locs = (ListView) findViewById(R.id.locList);
        HomeFragment home = new HomeFragment();
        home.setAdapter(locs, this);
        ((MyArrayAdapter) locs.getAdapter()).notifyDataSetChanged();
	}
	
	@Override
	public void onTabReselected(Tab arg0, android.app.FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab arg0, android.app.FragmentTransaction arg1) {
		viewPager.setCurrentItem(arg0.getPosition());
	}

	@Override
	public void onTabUnselected(Tab arg0, android.app.FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

    private void checkUnsyncedBRs()
    {
        // Check if there are any un synced bathrooms in the background and if so throw up a popup
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Bathroom> unsyncedBrs = DatabaseManager.openDatabase(MainTabs.this).getUnsyncedBathrooms();

                if (!unsyncedBrs.isEmpty())
                {
                    unsyncedBathrooms = unsyncedBrs;

                    Intent i = new Intent(UNSYNCED_BATHROOMS);
                    LocalBroadcastManager.getInstance(MainTabs.this).sendBroadcast(i);
                }
            }
        }).start();
    }

    private void showUnsyncedDialog()
    {
        new AlertDialog.Builder(this)
                .setTitle(R.string.unsynced_dialog_title)
                .setMessage(R.string.unsynced_dialog_desc)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // save in background
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (Bathroom br : unsyncedBathrooms)
                                {
                                    ParseManager.getInstance(MainTabs.this).saveBathroom(br);
                                }
                            }
                        }).start();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // no-op
                    }
                }).show();
    }
}
