package lk.smarthome.smarthomeagent.controller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import lk.smarthome.smarthomeagent.model.DataContract;

/**
 * Created by charitha on 9/13/16.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SmartHome.db";

    private static final String SQL_CREATE_REGION =
            "CREATE TABLE " + DataContract.RegionEntry.TABLE_NAME + " (" +
                    DataContract.RegionEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                    DataContract.RegionEntry.COLUMN_NAME + " TEXT," +
                    DataContract.RegionEntry.COLUMN_MAJOR + " INTEGER," +
                    DataContract.RegionEntry.COLUMN_MINOR + " INTEGER)";
    private static final String SQL_CREATE_DEVICE =
            "CREATE TABLE " + DataContract.DeviceEntry.TABLE_NAME + " (" +
                    DataContract.DeviceEntry.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    DataContract.DeviceEntry.COLUMN_NAME + " TEXT, " +
                    DataContract.DeviceEntry.COLUMN_REGION_ID + " INTEGER)";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_REGION);
        sqLiteDatabase.execSQL(SQL_CREATE_DEVICE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
