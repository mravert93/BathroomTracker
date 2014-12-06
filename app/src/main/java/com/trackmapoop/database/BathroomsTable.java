package com.trackmapoop.database;

/**
 * Created by mike on 8/13/14.
 */
public class BathroomsTable {
    public static final String TABLE_NAME = "bathrooms";
    public static final String TITLE_COL = "title";
    public static final String LONG_COL = "longitude";
    public static final String LAT_COL = "latitude";
    public static final String COUNT = "count";
    public static final String PARSE_OBJECT_ID = "parseObjectId";
    public static final String PARSE_CREATED_AT = "parseCreatedAt";
    public static final String PARSE_LAST_UPDATED = "parseLastUpdated";

    // Should never get called
    private BathroomsTable() {}
}
