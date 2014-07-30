package com.trackmapoop.data;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.trackmapoop.activities.R;

public class MyArrayAdapter extends ArrayAdapter<String>{
	private final Context mContext;
	private final ArrayList<String> mTitles;
	private final ArrayList<String> mLocs;
	private final ArrayList<String> mCounts;
	private String mPicName;
	
	public MyArrayAdapter(Context context, ArrayList<String> titles, 
			ArrayList<String> locs, ArrayList<String> counts) {
	    super(context, R.layout.rowlayout);
	    this.mContext = context;
	    this.mTitles = titles;
	    this.mLocs = locs;
	    this.mCounts = counts;
    }
	
	@Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) mContext
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
	    TextView titleView = (TextView) rowView.findViewById(R.id.title);
	    TextView locView = (TextView) rowView.findViewById(R.id.location);
	    TextView countView = (TextView) rowView.findViewById(R.id.counts);
	    ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
	    imageView.setImageResource(R.drawable.ic_launcher);
	    mPicName = getPicName(mTitles.get(position));
	    if(mPicName.length() > 0) {
	    	setPic(mPicName, imageView);
	    }
	    titleView.setText(mTitles.get(position));
	    locView.setText(mLocs.get(position));
	    countView.setText(mCounts.get(position));
	    //Set the image
	    return rowView;
	  }
	
	@Override
	public int getCount() {
	    // TODO Auto-generated method stub
	    return mTitles.size();
	}
	
	public int getCount(int index) {
		String scount = mCounts.get(index);
		for(int i = 0; i<scount.length(); i++) {
			char c = scount.charAt(i);
			if(c >= '0' && c <= '9') {
				String tmp = String.valueOf(c);
				if(scount.charAt(i+1) >= '0' && scount.charAt(i+1) <= '9') {
					tmp = tmp + String.valueOf(scount.charAt(i+1));
				}
				return Integer.valueOf(tmp);
			}
		}
		return 0;
	}
	
	public String getTitle(int index) {
		return mTitles.get(index);
	}
	
	//Gets the picture name based on the title of the bathroom
	public String getPicName(String title) {
		File picDir = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES);
		File storageDir = new File(picDir, "com.bathroomtracker");
		if(storageDir.exists()) {
			File[] files = storageDir.listFiles();
			for(int i = 0; i < files.length; i++) {
				File tmp = files[i];
				if(tmp.getAbsolutePath().contains(title)) {
					return tmp.getAbsolutePath();
				}
			}
		}
		return "";
	}
	
	//If file exists, sets the picture for imageView
	public void setPic(String file, ImageView view) {
		// Get the dimensions of the View
	    int targetW = 50; //Width of ImageView
	    int targetH = 65; //Height of ImageView
	    
	    //Get the dimensions of bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(file, bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;
	    
	    //Determines how much to scale image by
	    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
	    
	    //Decode the image file
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;
	    
	    Bitmap bitmap = BitmapFactory.decodeFile(file, bmOptions);
	    view.setImageBitmap(bitmap);
	    
	}

}
