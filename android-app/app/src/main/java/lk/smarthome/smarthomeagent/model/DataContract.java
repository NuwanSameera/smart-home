package lk.smarthome.smarthomeagent.model;

import android.provider.BaseColumns;

/**
 * Created by charitha on 9/13/16.
 */
public final class DataContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DataContract() {
    }

    /* Inner class that defines the table contents */
    public static class RegionEntry implements BaseColumns {
        public static final String TABLE_NAME = "beacon";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_MAJOR = "major";
        public static final String COLUMN_MINOR = "minor";
    }

    /* Inner class that defines the table contents */
    public static class DeviceEntry implements BaseColumns {
        public static final String TABLE_NAME = "device";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_REGION_ID = "region_id";
    }

}
