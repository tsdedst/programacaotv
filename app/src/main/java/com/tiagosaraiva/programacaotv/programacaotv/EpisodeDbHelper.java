package com.tiagosaraiva.programacaotv.programacaotv;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by tfsar on 05/11/2016.
 */

public class EpisodeDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "episodes";
    private static final String TEXT_TYPE = " TEXT";
    private static final String DATETIME_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String TABLE_NAME = "episodes";
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    EpisodeEntry.COLUMN_NAME_PROGRAM+ TEXT_TYPE + COMMA_SEP +
                    EpisodeEntry.COLUMN_NAME_CHANNEL+ TEXT_TYPE + COMMA_SEP +
                    EpisodeEntry.COLUMN_NAME_STARTDATE + DATETIME_TYPE + " DEFAULT CURRENT_TIMESTAMP " + COMMA_SEP +
                    EpisodeEntry.COLUMN_NAME_ENDDATE + DATETIME_TYPE + " DEFAULT CURRENT_TIMESTAMP " + COMMA_SEP +
                    EpisodeEntry.COLUMN_NAME_SEASON+ TEXT_TYPE + COMMA_SEP +
                    EpisodeEntry.COLUMN_NAME_EPISODE+ TEXT_TYPE +
                    ")";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
    EpisodeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addEntry(EpisodeEntry ep)
    {
        SQLiteDatabase writableCacheDatabase = this.getWritableDatabase();

        if (!isInDB(ep)) {
            if (Objects.equals(ep.StartTime, "")) {
                ep.StartTime = DateHelper.getCurrentDateTimeString();
            }
            if (Objects.equals(ep.EndTime, "")) {
                ep.EndTime = DateHelper.getCurrentDateTimeString();
            }
//            Log.d("EPISODEDBHELPER", "addEntry: Add episode entry: Program: '" + ep.Program + "' on Network: '" + ep.Channel + "', Starting on: '" + ep.StartTime + "'");

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(EpisodeEntry.COLUMN_NAME_PROGRAM, ep.Program);
            values.put(EpisodeEntry.COLUMN_NAME_STARTDATE, ep.StartTime);
            values.put(EpisodeEntry.COLUMN_NAME_ENDDATE, ep.EndTime);
            values.put(EpisodeEntry.COLUMN_NAME_SEASON, ep.ProgramSeason);
            values.put(EpisodeEntry.COLUMN_NAME_EPISODE, ep.ProgramEpisode);
            values.put(EpisodeEntry.COLUMN_NAME_CHANNEL, ep.Channel);

            // insert value
            writableCacheDatabase.insert(TABLE_NAME, null, values);
         //   writableCacheDatabase.close();
        }

    }
    public List<EpisodeEntry> getEpisodes(ProgramEntry program)
    {
        MeoName meoname = new MeoName(program.ProgramName);
        String programname = meoname.Title;
        return getEpisodesOfProgram(programname);
    }

    public boolean isInDB(EpisodeEntry ep){
        return isInDB(ep.Program, ep.StartTime);
    }

    public boolean isInDB(String programName, String startdate)
    {
        boolean ret;
        SQLiteDatabase readableCacheDatabase;
        String selection = EpisodeEntry.COLUMN_NAME_PROGRAM + " = ? AND " + EpisodeEntry.COLUMN_NAME_STARTDATE + " = ?";
        String sortOrder = EpisodeEntry.COLUMN_NAME_STARTDATE;
        String[] selectionArgs = { programName , startdate};
        String[] projection = {
                EpisodeEntry.COLUMN_NAME_PROGRAM,
                EpisodeEntry.COLUMN_NAME_STARTDATE,
                EpisodeEntry.COLUMN_NAME_ENDDATE,
                EpisodeEntry.COLUMN_NAME_SEASON,
                EpisodeEntry.COLUMN_NAME_EPISODE,
                EpisodeEntry.COLUMN_NAME_CHANNEL
        };
        readableCacheDatabase = this.getReadableDatabase();
        Cursor c = readableCacheDatabase.query(
                TABLE_NAME,        // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        ret = c != null && c.moveToFirst();

        return ret;
    }

    public List<EpisodeEntry> getEpisodesOfProgram(String programName)
    {
        List<EpisodeEntry> ret = new ArrayList<>();
        SQLiteDatabase readableCacheDatabase;
        String selection = EpisodeEntry.COLUMN_NAME_PROGRAM + " = ? COLLATE NOCASE";
        String sortOrder = EpisodeEntry.COLUMN_NAME_STARTDATE;
        String[] selectionArgs = { programName };
        String[] projection = {
                EpisodeEntry.COLUMN_NAME_PROGRAM,
                EpisodeEntry.COLUMN_NAME_STARTDATE,
                EpisodeEntry.COLUMN_NAME_ENDDATE,
                EpisodeEntry.COLUMN_NAME_SEASON,
                EpisodeEntry.COLUMN_NAME_EPISODE,
                EpisodeEntry.COLUMN_NAME_CHANNEL
        };
        readableCacheDatabase = this.getReadableDatabase();
        Cursor c = readableCacheDatabase.query(
                TABLE_NAME,        // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        if (c != null && c.moveToFirst()) {
            do {
                // get the data into array, or class variable
                EpisodeEntry e = new EpisodeEntry(c.getString(0), c.getString(1), c.getString(2),
                                                  c.getString(3), c.getString(4), c.getString(5));
                ret.add(e);
            } while (c.moveToNext());
        }

        return ret;
    }
    public List<EpisodeEntry> getEpisodesOfChannel(String channelInitials)
    {
        List<EpisodeEntry> ret = new ArrayList<>();
        SQLiteDatabase readableCacheDatabase;
        String selection = EpisodeEntry.COLUMN_NAME_CHANNEL + " = ? COLLATE NOCASE";
        String sortOrder = EpisodeEntry.COLUMN_NAME_STARTDATE;
        String[] selectionArgs = { channelInitials };
        String[] projection = {
                EpisodeEntry.COLUMN_NAME_PROGRAM,
                EpisodeEntry.COLUMN_NAME_STARTDATE,
                EpisodeEntry.COLUMN_NAME_ENDDATE,
                EpisodeEntry.COLUMN_NAME_SEASON,
                EpisodeEntry.COLUMN_NAME_EPISODE,
                EpisodeEntry.COLUMN_NAME_CHANNEL
        };
        readableCacheDatabase = this.getReadableDatabase();
        Cursor c = readableCacheDatabase.query(
                TABLE_NAME,        // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        if (c != null && c.moveToFirst()) {
            do {
                // get the data into array, or class variable
                EpisodeEntry e = new EpisodeEntry(c.getString(0), c.getString(1), c.getString(2),
                        c.getString(3), c.getString(4), c.getString(5));
                ret.add(e);
            } while (c.moveToNext());
        }

        return ret;
    }

}
