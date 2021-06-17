package com.example.odometer;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class OdometerDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "odometer";
    private static final int DB_VERSION = 1;

    public OdometerDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0, DB_VERSION);
    }

    private static void insertOdometer(SQLiteDatabase db, String name,
                                       int meters, String times, String description){
        ContentValues odometerValues = new ContentValues();
        odometerValues.put("NAME", name);
        odometerValues.put("METERS", meters);
        odometerValues.put("TIMES", times);
        odometerValues.put("DESCRIPTION", description);
        db.insert("ODOMETER", null, odometerValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
            db.execSQL("CREATE TABLE ODOMETER (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "NAME TEXT, "
                    + "METERS INTEGER, "
                    + "TIMES TEXT, "
                    + "DESCRIPTION TEXT);");
            insertOdometer(db,
                    "Пробежка 1",
                    1000,
                    "00:06:00",
                    "Пол круга вокруг ЭЛЕКТРОН");
            insertOdometer(db,
                    "Пробежка 2",
                    2100,
                    "00:12:00",
                    "Круг вокруг ЭЛЕКТРОН");
            insertOdometer(db,
                    "Пробежка 3",
                    2500,
                    "00:20:00",
                    "Большой круг");
        }
        if (oldVersion < 2){
            db.execSQL("ALTER TABLE ODOMETER ADD COLUMN FAVORITE NUMERIC;");
        }
    }
}
