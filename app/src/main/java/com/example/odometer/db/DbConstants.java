package com.example.odometer.db;

public class DbConstants {
    public static final String TABLE_NAME = "my_table";
    public static final String _ID = "_id";
    public static final String TITLE = "title";
    public static final String METERS = "meters";
    public static final String TIMES = "times";
    public static final String DESC = "desc";
    public static final String DB_NAME = "odometer_db.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_STRUCTURE = "CREATE TABLE IF NOT EXISTS" +
            TABLE_NAME + " (" + _ID + "INTEGER PRIMARY KEY," + TITLE + " TEXT, " +
            METERS + " INTEGER, " + TIMES + " TEXT, "+ DESC + " TEXT)";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS" + TABLE_NAME;
}
