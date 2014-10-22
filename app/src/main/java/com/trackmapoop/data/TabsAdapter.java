package com.trackmapoop.data;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.trackmapoop.fragments.HomeFragment;
import com.trackmapoop.fragments.MapFragment;

public class TabsAdapter extends FragmentPagerAdapter {
	private HomeFragment home;
	private MapFragment map;
	
	public TabsAdapter(FragmentManager fm, Context context) {
		super(fm);
		home = HomeFragment.newInstance(context);
		map = MapFragment.newInstance(context);
	}
	
	@Override
	public Fragment getItem(int index) {
		switch(index) {
		case 0:
			return home;
		default:
			return map;
		}
	}
	
	@Override
	public int getCount() {
		return 2;
	}

}
