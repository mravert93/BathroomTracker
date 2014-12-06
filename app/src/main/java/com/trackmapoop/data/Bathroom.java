package com.trackmapoop.data;

import java.util.Date;

public class Bathroom {
	private String mtitle;
	private double mlongitude;
	private double mlatitude;
	private int mcount;
    private String mParseObjectId;
    private Date mParseCreatedAt;
    private Date mParseLastUpdated;
	
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

    public String getParseObjectId()
    {
        return mParseObjectId;
    }

    public void setParObjectId(String id)
    {
        mParseObjectId = id;
    }

    public Date getParseCreatedAt()
    {
        return mParseCreatedAt;
    }

    public void setParseCreatedAt(Date createdAt)
    {
        mParseCreatedAt = createdAt;
    }

    public Date getParseLastUpdated()
    {
        return mParseLastUpdated;
    }

    public void setParseLastUpdated(Date lastUpdated)
    {
        mParseLastUpdated = lastUpdated;
    }
}
