package com.trackmapoop.Managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.trackmapoop.data.Bathroom;
import com.trackmapoop.data.NearestBathroomLocs;
import com.trackmapoop.database.BathroomsTable;
import com.trackmapoop.database.NearestBathroomsTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mike on 8/13/14.
 */
public class DatabaseManager extends SQLiteOpenHelper{
    public static final String TAG = "DatabaseManager";
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "TrackBathroom.db";

    private final SQLiteDatabase db;

    private static Context mContext;
    private static DatabaseManager instance;

    private DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        mContext = context.getApplicationContext();
        db = getWritableDatabase();
    }

    public static synchronized DatabaseManager openDatabase(Context context)
    {
        if (instance == null)
        {
            instance = new DatabaseManager(context);
        }

        return instance;
    }

    public SQLiteDatabase getDatabase()
    {
        return db;
    }

    public void onCreate(SQLiteDatabase db) {
        for (int i = 1; i <= DATABASE_VERSION; i++)
        {
            try
            {
                this.upgradeDb(db, i);
            }
            catch (IOException e)
            {
                Log.d(TAG, e.getMessage());
            }
        }
    }

    private void upgradeDb(SQLiteDatabase db, int version) throws IOException
    {
        InputStream stream = null;
        BufferedReader bufferedReader = null;
        InputStreamReader reader = null;

        try
        {
            int resId = mContext.getResources().getIdentifier("db" + version, "raw", mContext.getPackageName());
            stream = mContext.getResources().openRawResource(resId);

            reader = new InputStreamReader(stream);
            bufferedReader = new BufferedReader(reader);

            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                line = line.trim();

                if (line.length() > 0)
                {
                    db.execSQL(line);
                }
            }
        }
        catch (Exception e)
        {
            StringBuilder stringBuilder =
                    new StringBuilder("Exception while upgrading database to version: ").append(version);
            Log.d(TAG, stringBuilder.toString());
        }
        finally
        {
            if (stream != null)
            {
                stream.close();
            }
            if (bufferedReader != null)
            {
                bufferedReader.close();
            }
            if (reader != null)
            {
                reader.close();
            }
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
//        db.execSQL(SQL_DELETE);
//        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void save(Bathroom bathroom)
    {
        if (bathroom != null)
        {
            try
            {
                db.beginTransaction();

                //Content values
                ContentValues entry = new ContentValues();
                entry.put(BathroomsTable.TITLE_COL, bathroom.getTitle());
                entry.put(BathroomsTable.LAT_COL, bathroom.getLat());
                entry.put(BathroomsTable.LONG_COL, bathroom.getLong());
                entry.put(BathroomsTable.COUNT, bathroom.getCount());

                //insert the newest row
                int newRowId = db.update(BathroomsTable.TABLE_NAME, entry, BathroomsTable.TITLE_COL + " = ?", new String[] {bathroom.getTitle()});

                if (newRowId == 0)
                {
                    db.insertOrThrow(BathroomsTable.TABLE_NAME, null, entry);
                }

                db.setTransactionSuccessful();
            }
            finally
            {
                db.endTransaction();
            }
        }
    }

    public void saveNearestLocations(List<NearestBathroomLocs> nearestList)
    {
        if (nearestList != null && nearestList.size() > 0)
        {
            for (NearestBathroomLocs nearest : nearestList) {
                save(nearest);
            }
        }
    }

    public void save(NearestBathroomLocs nearest)
    {
        if (nearest != null)
        {
            try
            {
                db.beginTransaction();

                //Content values
                ContentValues entry = new ContentValues();
                entry.put(NearestBathroomsTable.COLUMN_FEE, nearest.getFee());
                entry.put(NearestBathroomsTable.COLUMN_LAT, nearest.getLatitude());
                entry.put(NearestBathroomsTable.COLUMN_LONG, nearest.getLongitude());
                entry.put(NearestBathroomsTable.COLUMN_ADDRESS, nearest.getStreetAddress());

                int wheelChair = 0;
                if (nearest.isWheelchair())
                {
                    wheelChair = 1;
                }
                entry.put(NearestBathroomsTable.COLUMN_WHEEL_CHAIR, wheelChair);

                //insert the newest row
                int newRowId = db.update(NearestBathroomsTable.TABLE_NAME, entry, NearestBathroomsTable.COLUMN_ADDRESS + " = ?", new String[] {nearest.getStreetAddress()});

                if (newRowId == 0)
                {
                    db.insertOrThrow(NearestBathroomsTable.TABLE_NAME, null, entry);
                }

                db.setTransactionSuccessful();
            }
            finally
            {
                db.endTransaction();
            }
        }
    }

    public List<NearestBathroomLocs> getNearestBathrooms()
    {
        Cursor cursor = null;
        List<NearestBathroomLocs> nearestList = new ArrayList<NearestBathroomLocs>();

        try
        {
            cursor = db.query(NearestBathroomsTable.TABLE_NAME, null, null, null, null, null, NearestBathroomsTable.COLUMN_ADDRESS + " DESC");

            while (cursor.moveToNext())
            {
                NearestBathroomLocs nearest = createNearestFromCursor(cursor);

                if (nearest != null)
                {
                    nearestList.add(nearest);
                }
            }
        }
        finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
        }

        return nearestList;
    }

    public List<Bathroom> getAllBathrooms()
    {
        Cursor cursor = null;
        List<Bathroom> bathrooms = new ArrayList<Bathroom>();

        try
        {
            cursor = db.query(BathroomsTable.TABLE_NAME, null, null, null, null, null, BathroomsTable.TITLE_COL + " DESC");

            while (cursor.moveToNext())
            {
                Bathroom newBr = createBathroomFromCursor(cursor);

                if (newBr != null)
                {
                    bathrooms.add(newBr);
                }
            }
        }
        finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
        }

        return bathrooms;
    }

    public Bathroom createBathroomFromCursor(Cursor cursor)
    {
        Bathroom br = null;

        if (cursor != null)
        {
            try
            {
                br = new Bathroom();

                br.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(BathroomsTable.TITLE_COL)));
                br.setLat(cursor.getDouble(cursor.getColumnIndexOrThrow(BathroomsTable.LAT_COL)));
                br.setLong(cursor.getDouble(cursor.getColumnIndexOrThrow(BathroomsTable.LONG_COL)));
                br.setCount(cursor.getInt(cursor.getColumnIndexOrThrow(BathroomsTable.COUNT)));
            }
            catch (IllegalArgumentException e)
            {
                Log.d(TAG, "Unable to read bathroom from cursor");
                br = null;
            }
        }

        return br;
    }

    public NearestBathroomLocs createNearestFromCursor(Cursor cursor)
    {
        NearestBathroomLocs br = null;

        if (cursor != null)
        {
            try
            {
                br = new NearestBathroomLocs();

                br.setStreetAddress(cursor.getString(cursor.getColumnIndexOrThrow(NearestBathroomsTable.COLUMN_ADDRESS)));
                br.setFee(cursor.getString(cursor.getColumnIndexOrThrow(NearestBathroomsTable.COLUMN_FEE)));
                br.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(NearestBathroomsTable.COLUMN_LAT)));
                br.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(NearestBathroomsTable.COLUMN_LONG)));
                boolean wheelchair = cursor.getInt(cursor.getColumnIndexOrThrow(NearestBathroomsTable.COLUMN_WHEEL_CHAIR)) > 0;

                br.setWheelchair(wheelchair);
            }
            catch (IllegalArgumentException e)
            {
                Log.d(TAG, "Unable to read nearest bathroom from cursor");
                br = null;
            }
        }

        return br;
    }

    public Bathroom getBathroom(String title)
    {
        Cursor cursor = db.query(BathroomsTable.TABLE_NAME,
                null,
                BathroomsTable.TABLE_NAME + "=?",
                new String[] {title},
                null, null, null);

        cursor.moveToFirst();

        Bathroom br = createBathroomFromCursor(cursor);

        cursor.close();
        return br;
    }

    public void deleteBathroom(String title)
    {
        boolean newTransaction = !db.inTransaction();
        if (newTransaction)
        {
            db.beginTransaction();
        }

        try
        {
            int count = db.delete(BathroomsTable.TABLE_NAME, BathroomsTable.TITLE_COL + " = ?", new String[] {title});
            Log.d(TAG, "Deleted " + title + " bathroom");

            if (newTransaction) db.setTransactionSuccessful();
        }
        finally
        {
            if (newTransaction) db.endTransaction();
        }
    }

    public void deleteNearBathrooms()
    {
        boolean newTransaction = !db.inTransaction();
        if (newTransaction)
        {
            db.beginTransaction();
        }

        try
        {
            int count = db.delete(NearestBathroomsTable.TABLE_NAME, null, null);
            Log.d(TAG, "Deleted " + count + " nearest bathrooms");

            if (newTransaction) db.setTransactionSuccessful();
        }
        finally
        {
            if (newTransaction) db.endTransaction();
        }
    }

    public void deleteBathrooms()
    {
        boolean newTransaction = !db.inTransaction();
        if (newTransaction)
        {
            db.beginTransaction();
        }

        try
        {
            int count = db.delete(BathroomsTable.TABLE_NAME, null, null);
            Log.d(TAG, "Deleted " + count + " bathrooms");

            if (newTransaction) db.setTransactionSuccessful();
        }
        finally
        {
            if (newTransaction) db.endTransaction();
        }
    }
}
