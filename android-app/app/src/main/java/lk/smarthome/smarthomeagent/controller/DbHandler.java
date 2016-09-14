package lk.smarthome.smarthomeagent.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.smarthome.smarthomeagent.model.DataContract;
import lk.smarthome.smarthomeagent.model.SmartDevice;
import lk.smarthome.smarthomeagent.model.SmartRegion;

/**
 * Created by charitha on 9/13/16.
 */
public class DbHandler {

    private static DbHandler dbHandler;
    private DbHelper mDbHelper;

    private DbHandler(Context context) {
        mDbHelper = new DbHelper(context);
    }

    public static DbHandler getInstance(Context context) {
        if (dbHandler == null) {
            dbHandler = new DbHandler(context);
        }
        return dbHandler;
    }

    public void addRegion(SmartRegion region) {
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DataContract.RegionEntry.COLUMN_NAME, region.getName());
        values.put(DataContract.RegionEntry.COLUMN_MAJOR, region.getMajor());
        values.put(DataContract.RegionEntry.COLUMN_MINOR, region.getMinor());

        db.insert(DataContract.RegionEntry.TABLE_NAME, null, values);
    }

    public void addDevice(SmartDevice device) {
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DataContract.DeviceEntry.COLUMN_ID, device.getId());
        values.put(DataContract.DeviceEntry.COLUMN_NAME, device.getName());
        values.put(DataContract.DeviceEntry.COLUMN_REGION_ID, device.getRegionId());

        db.insert(DataContract.DeviceEntry.TABLE_NAME, null, values);
    }

    public List<SmartRegion> getRegions() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                DataContract.RegionEntry.COLUMN_ID,
                DataContract.RegionEntry.COLUMN_NAME,
                DataContract.RegionEntry.COLUMN_MAJOR,
                DataContract.RegionEntry.COLUMN_MINOR
        };

        Cursor cursor = db.query(
                DataContract.RegionEntry.TABLE_NAME, // The table to query
                projection,                          // The columns to return
                null,                                // The columns for the WHERE clause
                null,                                // The values for the WHERE clause
                null,                                // group the rows
                null,                                // filter by row groups
                null                                 // The sort order
        );

        List<SmartRegion> regions = new ArrayList<>();
        if (cursor.moveToFirst()) {
            SmartRegion region;
            do {
                region = new SmartRegion();
                region.setId(cursor.getInt(cursor.getColumnIndex(DataContract.RegionEntry.COLUMN_ID)));
                region.setName(cursor.getString(cursor.getColumnIndex(DataContract.RegionEntry.COLUMN_NAME)));
                region.setMajor(cursor.getInt(cursor.getColumnIndex(DataContract.RegionEntry.COLUMN_MAJOR)));
                region.setMinor(cursor.getInt(cursor.getColumnIndex(DataContract.RegionEntry.COLUMN_MINOR)));
                regions.add(region);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return regions;
    }

    public SmartRegion getRegion(int major, int minor) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                DataContract.RegionEntry.COLUMN_ID,
                DataContract.RegionEntry.COLUMN_NAME,
                DataContract.RegionEntry.COLUMN_MAJOR,
                DataContract.RegionEntry.COLUMN_MINOR
        };

        String selection = DataContract.RegionEntry.COLUMN_MAJOR + " = ? AND " + DataContract.RegionEntry.COLUMN_MINOR + " = ?";
        String[] selectionArgs = {String.valueOf(major), String.valueOf(minor)};

        Cursor cursor = db.query(
                DataContract.RegionEntry.TABLE_NAME, // The table to query
                projection,                          // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                                // The values for the WHERE clause
                null,                                // group the rows
                null,                                // filter by row groups
                null                                 // The sort order
        );

        SmartRegion region = null;
        if (cursor.moveToFirst()) {
            region = new SmartRegion();
            region.setId(cursor.getInt(cursor.getColumnIndex(DataContract.RegionEntry.COLUMN_ID)));
            region.setName(cursor.getString(cursor.getColumnIndex(DataContract.RegionEntry.COLUMN_NAME)));
            region.setMajor(cursor.getInt(cursor.getColumnIndex(DataContract.RegionEntry.COLUMN_MAJOR)));
            region.setMinor(cursor.getInt(cursor.getColumnIndex(DataContract.RegionEntry.COLUMN_MINOR)));
        }
        cursor.close();
        return region;
    }

    public SmartRegion getRegion(int id) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                DataContract.RegionEntry.COLUMN_ID,
                DataContract.RegionEntry.COLUMN_NAME,
                DataContract.RegionEntry.COLUMN_MAJOR,
                DataContract.RegionEntry.COLUMN_MINOR
        };

        String selection = DataContract.RegionEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query(
                DataContract.RegionEntry.TABLE_NAME, // The table to query
                projection,                          // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                                // The values for the WHERE clause
                null,                                // group the rows
                null,                                // filter by row groups
                null                                 // The sort order
        );

        SmartRegion region = null;
        if (cursor.moveToFirst()) {
            region = new SmartRegion();
            region.setId(cursor.getInt(cursor.getColumnIndex(DataContract.RegionEntry.COLUMN_ID)));
            region.setName(cursor.getString(cursor.getColumnIndex(DataContract.RegionEntry.COLUMN_NAME)));
            region.setMajor(cursor.getInt(cursor.getColumnIndex(DataContract.RegionEntry.COLUMN_MAJOR)));
            region.setMinor(cursor.getInt(cursor.getColumnIndex(DataContract.RegionEntry.COLUMN_MINOR)));
        }
        cursor.close();
        return region;
    }

    public List<SmartDevice> getDevices(int regionId) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                DataContract.DeviceEntry.COLUMN_ID,
                DataContract.DeviceEntry.COLUMN_NAME,
                DataContract.DeviceEntry.COLUMN_REGION_ID
        };

        String selection = DataContract.DeviceEntry.COLUMN_REGION_ID + " = ?";
        String[] selectionArgs = {String.valueOf(regionId)};

        Cursor cursor = db.query(
                DataContract.DeviceEntry.TABLE_NAME, // The table to query
                projection,                          // The columns to return
                selection,                           // The columns for the WHERE clause
                selectionArgs,                       // The values for the WHERE clause
                null,                                // group the rows
                null,                                // filter by row groups
                null                                 // The sort order
        );

        List<SmartDevice> devices = new ArrayList<>();
        if (cursor.moveToFirst()) {
            SmartDevice device;
            do {
                device = new SmartDevice();
                device.setId(cursor.getInt(cursor.getColumnIndex(DataContract.DeviceEntry.COLUMN_ID)));
                device.setName(cursor.getString(cursor.getColumnIndex(DataContract.DeviceEntry.COLUMN_NAME)));
                device.setRegionId(cursor.getInt(cursor.getColumnIndex(DataContract.DeviceEntry.COLUMN_REGION_ID)));
                devices.add(device);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return devices;
    }

}
