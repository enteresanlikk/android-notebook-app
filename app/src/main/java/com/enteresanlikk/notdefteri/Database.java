package com.enteresanlikk.notdefteri;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Database extends SQLiteOpenHelper {

    private static String DB_NAME = "Notebook.db";
    private static String TABLE_NAME = "tbl_note";

    public Database(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = String.format("CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, content TEXT NOT NULL, reminder INTEGER DEFAULT 0, reminder_date TEXT, reminder_time TEXT, status INTEGER DEFAULT 1, date TIMESTAMP DEFAULT CURRENT_TIMESTAMP);", TABLE_NAME);
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public Boolean add(String title, String content, Integer reminder, String reminder_date, String reminder_time) {
        Boolean retVal = false;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("title", title);
        cv.put("content", content);
        cv.put("reminder", reminder);
        cv.put("reminder_date", reminder_date);
        cv.put("reminder_time", reminder_time);

        long res = db.insert(TABLE_NAME, null, cv);

        if (res != -1) retVal = true;

        return retVal;
    }

    public ArrayList<HashMap<String, String>> list(String status) {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();

        Cursor cursor = db.rawQuery(String.format("select * from %s WHERE status = ? ORDER BY date DESC", TABLE_NAME),new String[]{
                status
            });
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                for(int i=0; i<cursor.getColumnCount();i++)
                {
                    map.put(cursor.getColumnName(i), cursor.getString(i));
                }

                items.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return items;
    }

    public HashMap<String, String> detail(Integer id) {
        HashMap<String,String> item = new HashMap<String,String>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE id="+id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0) {
            for(int i=0; i<cursor.getColumnCount(); i++)
            {
                item.put(cursor.getColumnName(i), cursor.getString(i));
            }
        }
        cursor.close();
        db.close();

        return item;
    }

    public HashMap<String, String> detailWithReminder(String reminder_date, String reminder_time) {
        HashMap<String,String> item = new HashMap<String,String>();
        String selectQuery = String.format("SELECT * FROM %s WHERE reminder_date LIKE '%s' AND reminder_time LIKE '%s'", TABLE_NAME, reminder_date, reminder_time);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0) {
            for(int i=0; i<cursor.getColumnCount(); i++)
            {
                item.put(cursor.getColumnName(i), cursor.getString(i));
            }
        }
        cursor.close();
        db.close();

        return item;
    }

    public Boolean edit(Integer id, String title, String content, Integer reminder, String reminder_date, String reminder_time) {
        Boolean retVal = false;

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();

            cv.put("title", title);
            cv.put("content", content);
            cv.put("reminder", reminder);
            cv.put("reminder_date", reminder_date);
            cv.put("reminder_time", reminder_time);
            cv.put("date", (new Timestamp((new Date()).getTime())).toString());


            long res = db.update(TABLE_NAME, cv, "id = ?", new String[] {
                    String.valueOf(id)
            });

            if (res != -1) retVal = true;
        } catch (Exception e) {
            Log.i("benim", e.toString());
        }

        return retVal;
    }

    public Boolean delete(int id) {
        Boolean retVal = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("status", 0);
        cv.put("date", (new Timestamp((new Date()).getTime())).toString());

        long res = db.update(TABLE_NAME, cv, "id = ?", new String[] {
                String.valueOf(id)
            });

        if (res != -1) retVal = true;

        return retVal;
    }

    public Boolean deleteAll() {
        Boolean retVal = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("status", 0);
        cv.put("date", (new Timestamp((new Date()).getTime())).toString());

        long res = db.update(TABLE_NAME, cv, "status = ?" ,new String[] {
                "1"
            });

        if (res != -1) retVal = true;

        return retVal;
    }

    public void deleteReminder(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("reminder", 0);
        cv.put("date", (new Timestamp((new Date()).getTime())).toString());

        db.update(TABLE_NAME, cv, "id = ?", new String[] {
                String.valueOf(id)
        });
    }
}
