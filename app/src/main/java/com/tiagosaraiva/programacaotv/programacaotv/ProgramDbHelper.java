package com.tiagosaraiva.programacaotv.programacaotv;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by tfsar on 28/10/2016.
 */

public class ProgramDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;
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
                    ProgramEntry.COLUMN_NAME_SHORT_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_IMDB_INFOCOMPLETE + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_IMDB_TITLE + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_IMDB_YEAR + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_IMDB_RATE + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_IMDB_RELEASED + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_IMDB_RUNTIME + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_IMDB_PLOT + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_IMDB_DIRECTOR + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_IMDB_GENRE + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_IMDB_ACTORS + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_IMDB_IMDBRATING + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_IMDB_IMDBID + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_IMDB_TYPE + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_IMDB_LANGUAGE + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_IMDB_COUNTRY + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_IMDB_AWARDS + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_IMDB_POSTER + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_IMDB_IMAGE_FILE + TEXT_TYPE  + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_TRAKT_IMAGE_FILE + TEXT_TYPE  + COMMA_SEP +
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

    public void programAddEntry(ProgramEntry program)
    {
        if (!isInDb(program.ProgramName)) {
            SQLiteDatabase writableCacheDatabase = this.getWritableDatabase();

            Log.d("PROGRAMDBHELPER", "programAddEntry: Add cache entry, channel: " + program.ChannelInitials + ", program: " + program.ProgramName + ", desc: " + program.ShortMeoDesc);

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(ProgramEntry.COLUMN_NAME_PROGRAM, program.ProgramName);
            values.put(ProgramEntry.COLUMN_NAME_CHANNEL, program.ChannelInitials);
            values.put(ProgramEntry.COLUMN_NAME_MEO_DESCRIPTION, program.MeoDesc);
            values.put(ProgramEntry.COLUMN_NAME_SHORT_DESCRIPTION, program.ShortMeoDesc);
            values.put(ProgramEntry.COLUMN_NAME_IMDB_INFOCOMPLETE, program.IMDBInfoComplete);
            values.put(ProgramEntry.COLUMN_NAME_IMDB_TITLE, program.IMDBTitle);
            values.put(ProgramEntry.COLUMN_NAME_IMDB_YEAR, program.IMDBYear);
            values.put(ProgramEntry.COLUMN_NAME_IMDB_RATE, program.IMDBRate);
            values.put(ProgramEntry.COLUMN_NAME_IMDB_RELEASED, program.IMDBReleased);
            values.put(ProgramEntry.COLUMN_NAME_IMDB_RUNTIME, program.IMDBRuntime);
            values.put(ProgramEntry.COLUMN_NAME_IMDB_PLOT, program.IMDBPlot);
            values.put(ProgramEntry.COLUMN_NAME_IMDB_DIRECTOR, program.IMDBDirector);
            values.put(ProgramEntry.COLUMN_NAME_IMDB_GENRE, program.IMDBGenre);
            values.put(ProgramEntry.COLUMN_NAME_IMDB_ACTORS, program.IMDBActors);
            values.put(ProgramEntry.COLUMN_NAME_IMDB_IMDBRATING, program.IMDBImdbrating);
            values.put(ProgramEntry.COLUMN_NAME_IMDB_IMDBID, program.IMDBImdbId);
            values.put(ProgramEntry.COLUMN_NAME_IMDB_TYPE, program.IMDBType);
            values.put(ProgramEntry.COLUMN_NAME_IMDB_LANGUAGE, program.IMDBLanguage);
            values.put(ProgramEntry.COLUMN_NAME_IMDB_COUNTRY, program.IMDBCountry);
            values.put(ProgramEntry.COLUMN_NAME_IMDB_AWARDS, program.IMDBAwards);
            values.put(ProgramEntry.COLUMN_NAME_IMDB_POSTER, program.IMDBPoster);
            values.put(ProgramEntry.COLUMN_NAME_IMDB_IMAGE_FILE, program.IMDBImageFile);
            values.put(ProgramEntry.COLUMN_NAME_TRAKT_IMAGE_FILE, program.TraktImageFile);
            values.put(ProgramEntry.COLUMN_NAME_TRAKT_URL, program.TraktUrl);
            // insert value
            writableCacheDatabase.replace(TABLE_NAME, null, values);
            writableCacheDatabase.close();
        }
    }

    public void resetDB()
    {
        Log.d("PROGRAMDBHELPER", "resetCache");
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
                ProgramEntry.COLUMN_NAME_SHORT_DESCRIPTION,
                ProgramEntry.COLUMN_NAME_IMDB_INFOCOMPLETE,
                ProgramEntry.COLUMN_NAME_IMDB_TITLE,
                ProgramEntry.COLUMN_NAME_IMDB_YEAR,
                ProgramEntry.COLUMN_NAME_IMDB_RATE,
                ProgramEntry.COLUMN_NAME_IMDB_RELEASED,
                ProgramEntry.COLUMN_NAME_IMDB_RUNTIME,
                ProgramEntry.COLUMN_NAME_IMDB_PLOT,
                ProgramEntry.COLUMN_NAME_IMDB_DIRECTOR,
                ProgramEntry.COLUMN_NAME_IMDB_GENRE,
                ProgramEntry.COLUMN_NAME_IMDB_ACTORS,
                ProgramEntry.COLUMN_NAME_IMDB_IMDBRATING,
                ProgramEntry.COLUMN_NAME_IMDB_IMDBID,
                ProgramEntry.COLUMN_NAME_IMDB_TYPE,
                ProgramEntry.COLUMN_NAME_IMDB_LANGUAGE,
                ProgramEntry.COLUMN_NAME_IMDB_COUNTRY,
                ProgramEntry.COLUMN_NAME_IMDB_AWARDS,
                ProgramEntry.COLUMN_NAME_IMDB_POSTER,
                ProgramEntry.COLUMN_NAME_IMDB_IMAGE_FILE,
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
            ProgramEntry ret = new ProgramEntry(c.getString(0),
                                                c.getString(1),
                                                c.getString(2),
                                                c.getString(3),
                                                c.getString(4),
                                                c.getString(5),
                                                c.getString(6),
                                                c.getString(7),
                                                c.getString(8),
                                                c.getString(9),
                                                c.getString(10),
                                                c.getString(11),
                                                c.getString(12),
                                                c.getString(13),
                                                c.getString(14),
                                                c.getString(15),
                                                c.getString(16),
                                                c.getString(17),
                                                c.getString(18),
                                                c.getString(19),
                                                c.getString(20),
                                                c.getString(21),
                                                c.getString(22),
                                                c.getString(23));
            Log.d("PROGRAMDBHELPER", "getProgram: title: " + ret.ProgramName + ", channel: " + ret.ChannelInitials);
            return ret;

        }
        return null;
    }


    public boolean isInDb(String programname) {
        SQLiteDatabase readableCacheDatabase;

        readableCacheDatabase = this.getReadableDatabase();

        String selection = ProgramEntry.COLUMN_NAME_PROGRAM + " = ?";
        String sortOrder = null;
        String[] selectionArgs = { programname};
        String[] projection = {
                ProgramEntry.COLUMN_NAME_PROGRAM,
                ProgramEntry.COLUMN_NAME_IMDB_INFOCOMPLETE
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
            String infocomplete = "";
            String title = "";
            try {
                title = c.getString(0);
            } catch (CursorIndexOutOfBoundsException ex) {
                Log.e("PROGRAMDBHELPER", "error in reading IMDB information from DB (title)");
            }
            if (title.compareTo(programname) == 0) {
                Log.d("PROGRAMDBHELPER", "program is in database");

                try {
                    infocomplete = c.getString(1);
                } catch (CursorIndexOutOfBoundsException ex) {
                    Log.e("PROGRAMDBHELPER", "error in reading IMDB information from DB (info complete)");
                }
                if (infocomplete.compareTo("Info") == 0) {
                    Log.d("PROGRAMDBHELPER", "imdb info is present");
                    return true;
                } else if ((infocomplete.compareTo("NoInfo") == 0) || infocomplete.compareTo("InfoInvalid") ==0 ) {
                    Log.d("PROGRAMDBHELPER", "imdb info is not available");
                    return true;
                } else {
                    Log.d("PROGRAMDBHELPER", "program is in database but invalid");
                    return false;
                }
            }
        }
        Log.d("PROGRAMDBHELPER", "program is not in database");
        return false;
    }
}
