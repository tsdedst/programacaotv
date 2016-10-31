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


    static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    ProgramEntry.COLUMN_NAME_PROGRAM + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_CHANNEL + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_MEO_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_IMDB_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_IMDB_IMAGE_FILE + TEXT_TYPE  + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_IMDB_URL + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_TRAKT_IMAGE_FILE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_TRAKT_URL + TEXT_TYPE  +
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
    public void programAddEntry(ProgramEntry entry)
    {
        programAddEntry(entry.ProgramName, entry.ChannelInitials, entry.MeoDesc, entry.ShortMeoDesc, entry.IMDBDesc, entry.IMDBImageFile, entry.IMDBUrl, entry.TraktImageFile, entry.TraktUrl);
    }
    public void programAddEntry(String programName, String channel, String description, String shortdescription, String imdbdesc, String imagefile, String imdblink, String traktimage, String trakturl)
    {
        SQLiteDatabase writableCacheDatabase = this.getWritableDatabase();

        Log.d("PROGRAMDBHELPER", "cacheAddEntry: Add cache entry, channel: "+channel+", program: "+ programName + ", desc: " +description);

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ProgramEntry.COLUMN_NAME_PROGRAM, programName);
        values.put(ProgramEntry.COLUMN_NAME_CHANNEL, channel);
        values.put(ProgramEntry.COLUMN_NAME_MEO_DESCRIPTION, description);
        values.put(ProgramEntry.COLUMN_NAME_SHORT_DESCRIPTION, shortdescription);
        values.put(ProgramEntry.COLUMN_NAME_IMDB_DESCRIPTION, imdbdesc);
        values.put(ProgramEntry.COLUMN_NAME_IMDB_IMAGE_FILE, imagefile);
        values.put(ProgramEntry.COLUMN_NAME_IMDB_URL, imdblink);
        values.put(ProgramEntry.COLUMN_NAME_TRAKT_IMAGE_FILE, traktimage);
        values.put(ProgramEntry.COLUMN_NAME_TRAKT_URL, trakturl);
        // insert value
        writableCacheDatabase.replace(TABLE_NAME, null, values);
        writableCacheDatabase.close();
    }

    public void resetDB()
    {
        Log.d("CACHEHELPER", "resetCache");
        SQLiteDatabase writableCacheDatabase = this.getWritableDatabase();
        // delete all entries
        writableCacheDatabase.execSQL(ProgramDbHelper.SQL_DELETE_ENTRIES);
        // recreate table
        writableCacheDatabase.execSQL(ProgramDbHelper.SQL_CREATE_TABLE);
    }

    public ProgramEntry getProgram(String programName) {
        SQLiteDatabase readableCacheDatabase;

        readableCacheDatabase = this.getReadableDatabase();

        String selection = ProgramEntry.COLUMN_NAME_PROGRAM + " = ?";
        String sortOrder = null;
        String[] selectionArgs = { programName };
        String[] projection = {
                ProgramEntry.COLUMN_NAME_PROGRAM,
                ProgramEntry.COLUMN_NAME_CHANNEL,
                ProgramEntry.COLUMN_NAME_MEO_DESCRIPTION,
                ProgramEntry.COLUMN_NAME_IMDB_DESCRIPTION,
                ProgramEntry.COLUMN_NAME_IMDB_IMAGE_FILE,
                ProgramEntry.COLUMN_NAME_IMDB_URL,
                ProgramEntry.COLUMN_NAME_TRAKT_IMAGE_FILE,
                ProgramEntry.COLUMN_NAME_TRAKT_URL
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

        if (c != null) {
            c.moveToFirst();
            ProgramEntry ret = new ProgramEntry(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8));
            Log.d("PROGRAMDBHELPER", "getProgram: title: " + ret.ProgramName + ", channel: " + ret.ChannelInitials);
            return ret;

        }
        return null;
    }
}
