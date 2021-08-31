package com.vosxvo.weatherforecast.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

/**
 * <h1>Location Database Helper</h1>
 * This class help to create location_saved.sqlite and
 *
 */
public class LocationDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "location_saved.sqlite";

    private LocationDbHelper dbHelper;
    private SQLiteDatabase database;

    public LocationDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        dbHelper = this;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " +
                LocationEntry.TABLE_NAME + " ( " +
                LocationEntry._ID + " INTEGER PRIMARY KEY, " +
                LocationEntry.COLUMN_NAME_LATITUDE + " REAL, " +
                LocationEntry.COLUMN_NAME_LONGITUDE + " REAL," +
                LocationEntry.COLUMN_NAME_ADDRESS + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME + ";");
        onCreate(db);
    }

    public long insertRecord(ContentValues values) {
        database = dbHelper.getWritableDatabase();
        return database.insert(LocationEntry.TABLE_NAME, null, values);
    }

    /**
     *
     * @param latitude
     * @param longitude
     * @param address
     * @return
     */
    public long insertRecord(Double latitude, Double longitude, String address) {
        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_NAME_LATITUDE, latitude);
        values.put(LocationEntry.COLUMN_NAME_LONGITUDE, longitude);
        values.put(LocationEntry.COLUMN_NAME_ADDRESS, address);
        return database.insert(LocationEntry.TABLE_NAME, null, values);
    }

    public Cursor selectRecord() {
        database = dbHelper.getReadableDatabase();
        String[] projection = {
                LocationEntry._ID,
                LocationEntry.COLUMN_NAME_LATITUDE,
                LocationEntry.COLUMN_NAME_LONGITUDE,
                LocationEntry.COLUMN_NAME_ADDRESS
        };

        return database.query(
                LocationEntry.TABLE_NAME,
                projection,
                null, null, null, null, null
        );
    }

    public int deleteRecord(String id) {
        String selection = LocationEntry._ID + " LIKE ?";
        String[] args = {id};
        database = dbHelper.getWritableDatabase();
        return database.delete(LocationEntry.TABLE_NAME, selection, args);
    }

    public static class LocationEntry implements BaseColumns {
        public static final String TABLE_NAME = "location_saved";
        public static final String COLUMN_NAME_LATITUDE = "lat";
        public static final String COLUMN_NAME_LONGITUDE = "lon";
        public static final String COLUMN_NAME_ADDRESS = "address";
    }

    public String getTableName() {
        return LocationEntry.TABLE_NAME;
    }

    public String getColumnNameLatitude() {
        return LocationEntry.COLUMN_NAME_LATITUDE;
    }

    public String getColumnNameLongitude() {
        return LocationEntry.COLUMN_NAME_LONGITUDE;
    }

    public String getColumnNameAddress() {
        return LocationEntry.COLUMN_NAME_ADDRESS;
    }
}
