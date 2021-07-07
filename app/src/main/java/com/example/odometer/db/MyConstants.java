package com.example.odometer.db;

public class MyConstants {
    public static final String TABLE_NAME = "my_table";
    public static final String _ID = "_id";
    public static final String TITLE = "title";
    public static final String METERS = "meters";
    public static final String TIMES = "times";
    public static final String DESCRIPTION = "description";
    public static final String DB_NAME = "odometer_db.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_STRUCTURE = "CREATE TABLE IF NOT EXISTS" +
            TABLE_NAME + " (" + _ID + "INTEGER PRIMARY KEY," + TITLE + " TEXT, " +
            METERS + " INTEGER, " + TIMES + " TEXT, "+ DESCRIPTION + " TEXT)";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS" + TABLE_NAME;
}
