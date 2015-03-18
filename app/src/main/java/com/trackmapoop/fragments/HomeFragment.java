package com.trackmapoop.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.trackmapoop.Managers.DatabaseManager;
import com.trackmapoop.Managers.ParseManager;
import com.trackmapoop.activities.R;
import com.trackmapoop.data.BRConstants;
import com.trackmapoop.data.Bathroom;
import com.trackmapoop.data.MyArrayAdapter;
import com.trackmapoop.dialog.SelectDialog;

public class HomeFragment extends Fragment{
    public static final String TAG = "HOME_FRAGMENT";

	ListView locs;
    List<Bathroom> bathroomList;

    private Context mContext;

    private BroadcastReceiver onBathroomsUpdatedComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Bathrooms have been updated");

            DatabaseManager manager = DatabaseManager.openDatabase(mContext);
            bathroomList = manager.getAllBathrooms();
        }
    };

    public static HomeFragment newInstance(Context context)
    {
        HomeFragment fragment = new HomeFragment();
        fragment.mContext = context;

        return fragment;
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View V = inflater.inflate(R.layout.home_view, container, false);
        
        locs = (ListView) V.findViewById(R.id.locList);
        
        setAdapter(locs, this.getActivity());
        
        locs.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	  public void onItemClick(AdapterView<?> parent, View view,
        	    int position, long id) {
        		MyArrayAdapter adapter = (MyArrayAdapter) parent.getAdapter();
        		SelectDialog dialog = SelectDialog.newInstance(adapter.getCount(position), 
        				adapter.getTitle(position));
                dialog.show(getFragmentManager(), "setSelect");
        	  }
        });

        registerForContextMenu(locs);

        return V;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        IntentFilter updatedBathroomFilter = new IntentFilter(BRConstants.BR_UPDATED_ACTION);
        LocalBroadcastManager.getInstance(mContext)
                .registerReceiver(onBathroomsUpdatedComplete, updatedBathroomFilter);
    }

    @Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(onBathroomsUpdatedComplete);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        if (v.getId() == R.id.locList)
        {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Bathroom selected = bathroomList.get(info.position);
            menu.setHeaderTitle(selected.getTitle());
            menu.add("Delete");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Bathroom selected = bathroomList.get(info.position);

        //Remove bathroom to database
        ParseManager.getInstance(getActivity()).deleteBathroom(selected.getTitle());

        //Update the ArrayAdapter
        this.setAdapter(locs, getActivity());
        ((MyArrayAdapter) locs.getAdapter()).notifyDataSetChanged();

        Intent intent = new Intent(BRConstants.BR_UPDATED_ACTION);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

        return true;
    }
	
	//Method that fills the given list's array adapter
	public void setAdapter(ListView list, Activity activity) {
        DatabaseManager manager = DatabaseManager.openDatabase(getActivity());
        
        bathroomList = manager.getAllBathrooms();
        List<String> titles = getTitles(bathroomList);
        List<String> locations = getLocs(bathroomList);
        List<String> counts = getCounts(bathroomList);

        MyArrayAdapter adapter = new MyArrayAdapter(activity, titles, locations, counts);
        list.setAdapter(adapter);
		
	}
	
	public List<String> getTitles(List<Bathroom> baths) {
		List<String> titles = new ArrayList<String>();
		
		for(int i = 0; i < baths.size(); i++) {
			Bathroom tmp = baths.get(i);
			
			titles.add(tmp.getTitle());
		}
		
		return titles;
	}
	
	public List<String> getLocs(List<Bathroom> baths) {
		List<String> locations = new ArrayList<String>();
		
		for(int i = 0; i < baths.size(); i++) {
			Bathroom tmp = baths.get(i);
			
			String tmpLoc = "Latitude: " + Double.toString(tmp.getLat()) +
					" Longitude: " + Double.toString(tmp.getLong());
			
			locations.add(tmpLoc);
		}
		return locations;
	}
	
	public List<String> getCounts(List<Bathroom> baths) {
		List<String> locations = new ArrayList<String>();
		
		for(int i = 0; i < baths.size(); i++) {
			Bathroom tmp = baths.get(i);
			String scount = Integer.toString(tmp.getCount());
			String time = (tmp.getCount() > 1) ? " times" : " time";
			String count = "Used this bathroom " + scount + time;
			locations.add(count);
		}
		return locations;
	}

}
