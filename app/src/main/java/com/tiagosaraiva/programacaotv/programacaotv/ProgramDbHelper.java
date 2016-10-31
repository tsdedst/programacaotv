package com.tiagosaraiva.programacaotv.programacaotv;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;

/**
 * Created by tfsar on 28/10/2016.
 */

public class ProgramDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "programinfo";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String TABLE_NAME = "programinfo";
    /* Inner class that defines the table contents */
    public static class ProgramEntry  {

        static final String COLUMN_NAME_PROGRAM = "PROGRAMNAME";
        static final String COLUMN_NAME_CHANNEL = "PROGRAMCHANNEL";
        static final String COLUMN_NAME_DESCRIPTION = "DESCRIPTION";
        static final String COLUMN_NAME_IMAGE_FILE = "PROGRAMIMAGE";
        static final String COLUMN_NAME_IMDB_URL = "PROGRAMIMDB";
    }

    static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    ProgramEntry.COLUMN_NAME_PROGRAM + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_CHANNEL + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_IMAGE_FILE + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_IMDB_URL + TEXT_TYPE  +
                    " )";

    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    ProgramDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean programAddEntry(String programName, String channel, String description, String imagefile, String imdblink)
    {
        SQLiteDatabase writableCacheDatabase = this.getWritableDatabase();

        if (date == "")
        {
            date = DateHelper.getCurrentDateTimeString();
        }
        Log.d("CACHEHELPER", "cacheAddEntry: Add cache entry, channel: "+channel+", date: "+ date);

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(CacheEntry.COLUMN_NAME_CHANNEL, channel);
        values.put(CacheEntry.COLUMN_NAME_DATE, date);
        // insert value
        writableCacheDatabase.replace(TABLE_NAME, null, values);
        writableCacheDatabase.close();
        String verify = DateHelper.getDateTimeString(getUpdateDate(channel));

        return (date == verify);
    }


    public boolean cacheAddEntry(String channel)
    {
        return cacheAddEntry(channel, "");
    }


    public void resetCache()
    {
        Log.d("CACHEHELPER", "resetCache");
        SQLiteDatabase writableCacheDatabase = this.getWritableDatabase();
        // delete all entries
        writableCacheDatabase.execSQL(ProgramDbHelper.SQL_DELETE_ENTRIES);
        // recreate table
        writableCacheDatabase.execSQL(ProgramDbHelper.SQL_CREATE_TABLE);
    }

    public Date getUpdateDate(String channel) {
        SQLiteDatabase readableCacheDatabase;

        readableCacheDatabase = this.getReadableDatabase();

        String selection = CacheEntry.COLUMN_NAME_CHANNEL + " = ?";
        String sortOrder = CacheEntry.COLUMN_NAME_DATE + " DESC";
        String[] selectionArgs = { channel };
        String[] projection = {
                CacheEntry.COLUMN_NAME_CHANNEL,
                CacheEntry.COLUMN_NAME_DATE
        };
        Cursor c = readableCacheDatabase.query(
                TABLE_NAME,        // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        String date = "";
        //Cursor c = readableCacheDatabase.rawQuery("select * from " + TABLE_NAME + " where " + CacheEntry.COLUMN_NAME_CHANNEL + " = '" + channel + "'", null);
        if (c != null) {
            c.moveToFirst();

            try {
                date = c.getString(1);
            } catch (CursorIndexOutOfBoundsException ex) {

                Log.e("CACHEHELPER", "Hello... is it me you're looking for?");
                //todo: handle your shit
            }
            Log.d("CACHEHELPER", "getUpdateDate: channel: " + channel + ", date: " + date);
        }
        return DateHelper.getConvertedDateTime(date);
    }
}
