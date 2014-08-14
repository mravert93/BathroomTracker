package com.trackmapoop.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.trackmapoop.Managers.DatabaseManager;
import com.trackmapoop.activities.R;
import com.trackmapoop.data.MyArrayAdapter;
import com.trackmapoop.fragments.HomeFragment;

public class CountDialog extends DialogFragment{
	NumberPicker count;
	int mCount;
	String mTitle;
	
	public static CountDialog newInstance(int brCount, String title) {
		CountDialog c = new CountDialog();
		
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
	    
	    final View dialogView = inflater.inflate(R.layout.dialog_count, null);
	    
	    count = (NumberPicker) dialogView.findViewById(R.id.countPicker);
	    count.setMaxValue(100);
	    count.setMinValue(1);
	    count.setWrapSelectorWheel(false);
	    count.setValue(getArguments().getInt("count"));
        mTitle = getArguments().getString("title");
        TextView countTitle = (TextView) dialogView.findViewById(R.id.countTitle);
        countTitle.setText(mTitle);

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(dialogView)
	    // Add action buttons
	           .setPositiveButton("Update", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
                        int newcount = count.getValue();
                        
                        mListener.onDialogPositiveClick(CountDialog.this, newcount, mTitle);
	               }
	           })
	           .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   CountDialog.this.getDialog().cancel();
	               }
	           });      
	    LinearLayout remove = (LinearLayout) dialogView.findViewById(R.id.removeBr);
	    remove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
                //Add new bathroom to database
                DatabaseManager manager = DatabaseManager.openDatabase(getActivity());
                manager.deleteBathroom(mTitle);
                
                //Update the ArrayAdapter
                ListView locs = (ListView) getActivity().findViewById(R.id.locList);
                HomeFragment home = new HomeFragment();
                home.setAdapter(locs, getActivity());
                ((MyArrayAdapter) locs.getAdapter()).notifyDataSetChanged();
				
                CountDialog.this.getDialog().cancel();
			}
	    	
	    });

	    return builder.create();
	}

	
}
