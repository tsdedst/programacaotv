package com.tiagosaraiva.programacaotv.programacaotv;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * ProgramacaoTV
 *
 *
 * Created by tfsar on Novembro/2016.
 *
 */

class ChannelDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "cache";
    private static final String TEXT_TYPE = " TEXT";
    private static final String DATETIME_TYPE = " DATETIME";
    private static final String COMMA_SEP = ",";
    private static final String TABLE_NAME = "cache";
    private static final String COLUMN_NAME_CHANNEL = "CHANNEL";
    private static final String COLUMN_NAME_SIGLA = "SIGLA";
    private static final String COLUMN_NAME_DATE = "DATE";
    private static final String COLUMN_NAME_CHANNEL_NUMBER = "NUM";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME_CHANNEL_NUMBER + " INTEGER PRIMARY KEY" + COMMA_SEP +
                    COLUMN_NAME_CHANNEL + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_SIGLA + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_DATE + DATETIME_TYPE + " DEFAULT CURRENT_TIMESTAMP )";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    ChannelDBHelper(Context context) {
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

    public void cacheAddEntry(int num, String channelname, String channelInitials, Date updatedate) {
        ChannelEntry out = new ChannelEntry(num, channelname, channelInitials, updatedate);
        cacheAddEntry(out);
    }
    public void cacheAddEntry(ChannelEntry channelEntry)
    {
        int num = channelEntry.Number;
        String channel = channelEntry.ChannelName;
        String initials = channelEntry.Sigla;
        Date date = channelEntry.UpdateDate;
        SQLiteDatabase writableCacheDatabase = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_CHANNEL_NUMBER, num);
        values.put(COLUMN_NAME_CHANNEL, channel);
        values.put(COLUMN_NAME_SIGLA, initials);
        values.put(COLUMN_NAME_DATE, DateHelper.getDateTimeString(date));
        // insert value
        writableCacheDatabase.update(TABLE_NAME, values, COLUMN_NAME_CHANNEL_NUMBER+"="+Integer.toString(num), null);
        //writableCacheDatabase.close();
    }

    public void cacheSetUpdatedNowfromInitials(String initials)
    {
        ChannelEntry in = getChannelEntryFromInitials(initials);
        in.UpdateDate = DateHelper.getCurrentDateTime();
        cacheAddEntry(in);
    }


    public void resetCache()
    {
        Log.d("CACHEHELPER", "resetCache");
        SQLiteDatabase writableCacheDatabase = this.getWritableDatabase();
        // delete all entries
        writableCacheDatabase.execSQL(ChannelDBHelper.SQL_DELETE_ENTRIES);
        // recreate table
        writableCacheDatabase.execSQL(ChannelDBHelper.SQL_CREATE_TABLE);
    }

    public ChannelEntry getChannelEntryFromInitials(String intials)
    {
        SQLiteDatabase readableCacheDatabase;

        readableCacheDatabase = this.getReadableDatabase();

        String selection = COLUMN_NAME_CHANNEL + " = ?";
        String sortOrder = COLUMN_NAME_CHANNEL_NUMBER + " ASC";
        String[] selectionArgs = { intials };
        String[] projection = {
                COLUMN_NAME_CHANNEL_NUMBER,
                COLUMN_NAME_CHANNEL,
                COLUMN_NAME_SIGLA,
                COLUMN_NAME_DATE
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
        ChannelEntry ret = null;

        if (c != null) {
            c.moveToFirst();
            try {
                ret = new ChannelEntry(c.getInt(0),c.getString(1),c.getString(2), DateHelper.getConvertedDateTime(c.getString(3)));
            } catch (CursorIndexOutOfBoundsException ex) {
                Log.d("CACHEHELPER", "No Channel List present (first run?)");
            }
//            Log.d("CACHEHELPER", "getUpdateDate: channel: " + channel + ", date: " + date);
        }
        return ret;
    }

    public List<ChannelEntry> getChannelList()
    {
        SQLiteDatabase readableCacheDatabase;

        readableCacheDatabase = this.getReadableDatabase();
        Cursor cur = readableCacheDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        ArrayList<ChannelEntry> temp = new ArrayList();
        if (cur != null) {
            if (cur.moveToFirst()) {
                do {
                    int num = cur.getInt(cur.getColumnIndex(COLUMN_NAME_CHANNEL_NUMBER));
                    String name = cur.getString(cur.getColumnIndex(COLUMN_NAME_CHANNEL));
                    String sigla = cur.getString(cur.getColumnIndex(COLUMN_NAME_SIGLA));
                    Date date = DateHelper.getConvertedDateTime(cur.getString(cur.getColumnIndex(COLUMN_NAME_DATE)));
                    temp.add(new ChannelEntry(num, name, sigla, date));
                } while (cur.moveToNext());
            }
        }
        return temp;
    }
}
