package com.trackmapoop.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
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

import com.trackmapoop.Managers.DatabaseManager;
import com.trackmapoop.activities.R;
import com.trackmapoop.data.Bathroom;
import com.trackmapoop.data.MyArrayAdapter;
import com.trackmapoop.dialog.SelectDialog;

public class HomeFragment extends Fragment{
	ListView locs;

    private Context mContext;

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

        return V;
    }
	
	//Method that fills the given list's array adapter
	public void setAdapter(ListView list, Activity activity) {
        DatabaseManager manager = DatabaseManager.openDatabase(getActivity());
        
        List<Bathroom> baths = manager.getAllBathrooms();
        List<String> titles = getTitles(baths);
        List<String> locations = getLocs(baths);
        List<String> counts = getCounts(baths);

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
