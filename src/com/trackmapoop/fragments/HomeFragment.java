package com.trackmapoop.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.trackmapoop.activities.R;
import com.trackmapoop.data.Bathroom;
import com.trackmapoop.data.BathroomContract;
import com.trackmapoop.data.BathroomContract.BathroomDbHelper;
import com.trackmapoop.data.BathroomContract.BathroomEntry;
import com.trackmapoop.data.MyArrayAdapter;
import com.trackmapoop.dialog.SelectDialog;

public class HomeFragment extends Fragment{
	ListView locs;
	
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

        return V;
    }
	
	//Method that fills the given list's array adapter
	public void setAdapter(ListView list, Activity activity) {
        
        ArrayList<Bathroom> baths = readLocs(activity);
        ArrayList<String> titles = getTitles(baths);
        ArrayList<String> locations = getLocs(baths);
        ArrayList<String> counts = getCounts(baths);

        MyArrayAdapter adapter = new MyArrayAdapter(activity, titles, locations, counts);
        list.setAdapter(adapter);
		
	}
	
	public ArrayList<Bathroom> readLocs(Activity activity) {
		ArrayList<Bathroom> locs = new ArrayList<Bathroom>();
		
		BathroomContract contract = new BathroomContract();
		BathroomDbHelper mDbHelper = contract.new BathroomDbHelper(activity);
		
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
				Bathroom bath = new Bathroom();
				bath.setTitle(c.getString(1));
				bath.setLat(Double.parseDouble(c.getString(2)));
				bath.setLong(Double.parseDouble(c.getString(3)));
				bath.setCount(Integer.parseInt(c.getString(4)));
				
				//Add new bath to array
				locs.add(bath);
			} while(c.moveToNext());
		}
		
		return locs;
	}
	
	public ArrayList<String> getTitles(ArrayList<Bathroom> baths) {
		ArrayList<String> titles = new ArrayList<String>();
		
		for(int i = 0; i < baths.size(); i++) {
			Bathroom tmp = baths.get(i);
			
			titles.add(tmp.getTitle());
		}
		
		return titles;
	}
	
	public ArrayList<String> getLocs(ArrayList<Bathroom> baths) {
		ArrayList<String> locations = new ArrayList<String>();
		
		for(int i = 0; i < baths.size(); i++) {
			Bathroom tmp = baths.get(i);
			
			String tmpLoc = "Latitude: " + Double.toString(tmp.getLat()) +
					" Longitude: " + Double.toString(tmp.getLong());
			
			locations.add(tmpLoc);
		}
		return locations;
	}
	
	public ArrayList<String> getCounts(ArrayList<Bathroom> baths) {
		ArrayList<String> locations = new ArrayList<String>();
		
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
