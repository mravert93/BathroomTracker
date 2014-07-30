package com.trackmapoop.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public final class BathroomContract {
	
	public BathroomContract(){}
	
	//Contains all of the table's contents
	public static abstract class BathroomEntry implements BaseColumns {
		public static final String TABLE_NAME = "bathroomDB";
        public static final String TITLE_COL = "title";
        public static final String LONG_COL = "longitude";
        public static final String LAT_COL = "latitude";
        public static final String COUNT = "count";
        
	}

    private static final String TEXT = " TEXT";
    private static final String COMMA = ",";
    private static final String SQL_CREATE = 
        "CREATE TABLE " + BathroomEntry.TABLE_NAME + " (" +
        BathroomEntry._ID + " INTEGER PRIMARY KEY," +
        BathroomEntry.TITLE_COL + TEXT + COMMA +
        BathroomEntry.LONG_COL + TEXT + COMMA +
        BathroomEntry.LAT_COL + TEXT + COMMA +
        BathroomEntry.COUNT + TEXT + " )";
    private static final String SQL_DELETE = 
    	"DROP TABLE IF EXISTS " + BathroomEntry.TABLE_NAME;	
    
    //Database helper class
    public class BathroomDbHelper extends SQLiteOpenHelper {
    	public static final int DATABASE_VERSION = 1;
    	public static final String DATABASE_NAME = "TrackBathroom.db";
    	
    	public BathroomDbHelper(Context context) {
    		super(context, DATABASE_NAME, null, DATABASE_VERSION);
    	}
    	
    	public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

}
