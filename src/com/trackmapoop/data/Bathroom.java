package com.trackmapoop.data;

public class Bathroom {
	private String mtitle;
	private double mlongitude;
	private double mlatitude;
	private int mcount;
	
	public Bathroom() {}
	
	public void setTitle(String title) {
		mtitle = title;
	}
	
	public String getTitle() {
		return mtitle;
	}
	
	public void setLong(double longitude) {
		mlongitude = longitude;
	}
	
	public double getLong(){
		return mlongitude;
	}
	
	public void setLat(double latitude) {
		mlatitude = latitude;
	}
	
	public double getLat() {
		return mlatitude;
	}

	public int getCount() {
		return mcount;
	}

	public void setCount(int mcount) {
		this.mcount = mcount;
	}
}
