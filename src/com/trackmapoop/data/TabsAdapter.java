package com.trackmapoop.data;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.trackmapoop.fragments.HomeFragment;
import com.trackmapoop.fragments.MapFragment;

public class TabsAdapter extends FragmentPagerAdapter {
	private HomeFragment home;
	private MapFragment map;
	
	public TabsAdapter(FragmentManager fm) {
		super(fm);
		home = new HomeFragment();
		map = new MapFragment();
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
