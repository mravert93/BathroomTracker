package com.trackmapoop.dialog;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.trackmapoop.Managers.AnalyticsManager;
import com.trackmapoop.activities.R;
import com.trackmapoop.data.MyArrayAdapter;
import com.trackmapoop.fragments.HomeFragment;

public class SelectDialog extends DialogFragment{
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPath;
	int mCount;
	String mTitle;
	
	public static SelectDialog newInstance(int brCount, String title) {
		SelectDialog c = new SelectDialog();
		
		Bundle args = new Bundle();
		args.putInt("count", brCount);
		args.putString("title", title);
		c.setArguments(args);
		
		return c;
	}
	
	/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, int newcount, String title);
    }
    
 // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;
    
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    
	    final View dialogView = inflater.inflate(R.layout.dialog_select, null);
	    
	    mCount = getArguments().getInt("count");
        mTitle = getArguments().getString("title");

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(dialogView);
	    
	    TextView addpic = (TextView) dialogView.findViewById(R.id.picID);
	    addpic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				takePic();

                // Log it!
                Map<String, String> attrs = new HashMap<String, String>();
                attrs.put("Bathroom Name", mTitle);
                AnalyticsManager.getInstance(getActivity()).tagEvent("Picture Taken", attrs);

                ListView locs = (ListView) getActivity().findViewById(R.id.locList);
                HomeFragment home = new HomeFragment();
                home.setAdapter(locs, getActivity());
                ((MyArrayAdapter) locs.getAdapter()).notifyDataSetChanged();
				
                SelectDialog.this.getDialog().cancel();
			}
	    	
	    });

	    TextView brinfo = (TextView) dialogView.findViewById(R.id.countID);
	    brinfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
        		CountDialog dialog = CountDialog.newInstance(mCount, mTitle);
                dialog.show(getFragmentManager(), "setCount");
                SelectDialog.this.getDialog().cancel();
			}
	    	
	    });

	    return builder.create();
	}
	
	public void takePic() {
		Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		//Make sure the camera intent is there
		if(takePicIntent.resolveActivity(getActivity().getPackageManager()) != null) {
			File photoFile = null;
			try {
				photoFile = createImageFile();
			}
			catch(IOException ex) {
				//Do something here
			}
			//Continue only if the file was created
			if (photoFile != null) {
				takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
				startActivityForResult(takePicIntent, REQUEST_TAKE_PHOTO);
			}
			
		}
		
	}
	
	private File createImageFile() throws IOException {
		//Create image file name
		String imageFileName = "JPEG_" + mTitle + "_";
		File picDir = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES);
		File storageDir = new File(picDir, "com.bathroomtracker");
		if(!storageDir.exists()) {
			storageDir.mkdirs();
		}
		checkFileExists(storageDir);
		File image = File.createTempFile(imageFileName, ".jpg", storageDir);
		
		//Save a file : path for use with ACTION_VIEW intents
		mCurrentPath = "file:" + image.getAbsolutePath();
		return image;
	}
	
	//Check if a picture file already exists
	public boolean checkFileExists(File storageDir) {
		if(storageDir.exists()) {
			File[] files = storageDir.listFiles();
			for(int i = 0; i < files.length; i++) {
				File tmp = files[i];
				if(tmp.getAbsolutePath().contains(mTitle)) {
					tmp.delete();
				}
			}
		}
		return true;
	}

	
}
