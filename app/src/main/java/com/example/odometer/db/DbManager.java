package com.example.odometer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.odometer.adapter.ListItem;

import java.util.ArrayList;
import java.util.List;

public class DbManager {
    private final DbHelper dbHelper;
    private SQLiteDatabase db;

    public DbManager(Context context) {
        dbHelper = new DbHelper(context);
    }

    public void openDb(){
        db = dbHelper.getWritableDatabase();
    }

    public void insertToDb(String title, String meters, String times, String desc){
        ContentValues cv = new ContentValues();
        cv.put(DbConstants.TITLE, title);
        cv.put(DbConstants.METERS, meters);
        cv.put(DbConstants.TIMES, times);
        cv.put(DbConstants.DESC, desc);
        db.insert(DbConstants.TABLE_NAME, null, cv);
    }

    public List<ListItem> getFromDb(){
        List<ListItem> tempList = new ArrayList<>();
        Cursor cursor = db.query(DbConstants.TABLE_NAME, null,
                null, null, null, null, null);

        while (cursor.moveToNext()){
            ListItem item = new ListItem();
            String title = cursor.getString(cursor.getColumnIndex(DbConstants.TITLE));
            String meters = cursor.getString(cursor.getColumnIndex(DbConstants.METERS));
            String times = cursor.getString(cursor.getColumnIndex(DbConstants.TIMES));
            String desc = cursor.getString(cursor.getColumnIndex(DbConstants.DESC));
            item.setTitle(title);
            item.setMeters(meters);
            item.setTimes(times);
            item.setDesc(desc);
            tempList.add(item);
        }
        cursor.close();
        return  tempList;
    }

    public void closeDb(){
        dbHelper.close();
    }
}

