package com.trackmapoop.data;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.trackmapoop.fragments.HomeFragment;
import com.trackmapoop.fragments.MapFragment;

public class TabsAdapter extends FragmentPagerAdapter {
	
	public TabsAdapter(FragmentManager fm) {
		super(fm);
	}
	
	@Override
	public Fragment getItem(int index) {
		switch(index) {
		case 0:
			return new HomeFragment();
		default:
			return new MapFragment();
		}
	}
	
	@Override
	public int getCount() {
		return 2;
	}

}
