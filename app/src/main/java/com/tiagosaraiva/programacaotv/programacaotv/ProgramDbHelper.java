package com.tiagosaraiva.programacaotv.programacaotv;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tfsar on 28/10/2016.
 */


public class ProgramDbHelper extends SQLiteOpenHelper implements InfoCompleteListener {

    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "programinfo";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ", ";
    private static final String TABLE_NAME = "programinfo";
    /* Inner class that defines the table contents */
    private List<String> AddedShows;
    private List<String> AddingShows;

    static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" + "id INTEGER PRIMARY KEY, " +
                    ProgramEntry.COLUMN_NAME_PROGRAM +TEXT_TYPE + " unique" + COMMA_SEP +
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
                    ProgramEntry.COLUMN_NAME_TRAKT_URL + TEXT_TYPE  + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_TMDB_BACKDROP_PATH + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_TMDB_OVERVIEW + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_TMDB_POSTER_PATH + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_TMDB_POPULARITY + TEXT_TYPE + COMMA_SEP +
                    ProgramEntry.COLUMN_NAME_TVDB_BANNER + TEXT_TYPE +
                    " )";

    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    private synchronized boolean isShowBeingAdded(String name)
    {
        boolean ret = AddingShows.contains(name.toLowerCase());
//        String response = ret?"yes": "nope";
//        Log.d("DBHELPER", "is show '"+name.toLowerCase()+"' being added? " + response);
        return ret;
    }
    private synchronized void showWasAdded(String name)
    {
        if (isShowBeingAdded(name.toLowerCase()))
        {
//            Log.d("DBHELPER", "AddingShows list before removing '"+name.toLowerCase()+"' : " + AddingShows.toString());
            int index =  AddingShows.indexOf(name.toLowerCase());
//            Log.d("DBHELPER", "show '"+name.toLowerCase()+"' is in position: " + String.valueOf(index));
            AddingShows.remove(index);
            //Log.d("DBHELPER", "AddingShows list after removing '"+name.toLowerCase()+"' : " + AddingShows.toString());
        }
//        else
//            Log.d("DBHELPER", "showWasAdded: show '"+name.toLowerCase()+"' is already not being added. did something go wrong?");
        this.notifyAll();
    }
    private synchronized void showIsBeingAdded(String name)
    {
        if (!isShowBeingAdded(name.toLowerCase()))
        {
//            Log.d("DBHELPER", "AddingShows list before adding '"+name.toLowerCase()+"' : " + AddingShows.toString());
            AddingShows.add(name.toLowerCase());
//            Log.d("DBHELPER", "AddingShows list after adding '"+name.toLowerCase()+"' : " + AddingShows.toString());
        }
//        else
//            Log.d("DBHELPER", " showIsBeingAdded: show '"+name.toLowerCase()+"' is already being added. did something go wrong?");
        this.notifyAll();
    }

    ProgramDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        AddedShows = new ArrayList<>();
        AddingShows = new ArrayList<>();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.enableWriteAheadLogging();

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

    public void programAddEntry(final String name, final String shortdesc, final String meodesc)
    {
        String programname = (new MeoName(name)).Title;
        // get imdb, don't get tmdb
        programAddEntry(programname, shortdesc, meodesc, false, true, false, false, false);
    }
    public void programAddEntry(final String name, final String shortdesc, final String meodesc, final boolean force, final boolean getImdb, final boolean getTmdb, final boolean getImdbImages, final boolean getTmdbImages) {
        String programname = (new MeoName(name)).Title;
        if (!isInDb(programname) || (force && !isShowBeingAdded(programname)))
        {
            showIsBeingAdded(programname);
            ProgramEntry p = new ProgramEntry(this, programname, shortdesc, meodesc, getImdb, getTmdb, getImdbImages, getTmdbImages);
        }
    }


    public void programAddtoDB(ProgramEntry program) {
        Log.d("PROGRAMDBHELPER", "programAddtoDB: Add cache entry, program: " + program.ProgramName + ", imdb rating: " + program.IMDBImdbrating + ", desc: " + program.ShortMeoDesc);

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
        values.put(ProgramEntry.COLUMN_NAME_TRAKT_IMAGE_FILE, program.TMDBPosterFile);
        values.put(ProgramEntry.COLUMN_NAME_TRAKT_URL, program.TMDBBackdropFile);
        values.put(ProgramEntry.COLUMN_NAME_TMDB_BACKDROP_PATH, program.TMDBBackdropURL);
        values.put(ProgramEntry.COLUMN_NAME_TMDB_OVERVIEW, program.TMDBOverview);
        values.put(ProgramEntry.COLUMN_NAME_TMDB_POSTER_PATH, program.TMDBPosterURL);
        values.put(ProgramEntry.COLUMN_NAME_TMDB_POPULARITY, program.TMDBPopularity);
        values.put(ProgramEntry.COLUMN_NAME_TVDB_BANNER, program.TVDBBanner);
        // insert value
        SQLiteDatabase writableCacheDatabase = this.getWritableDatabase();
        long l = writableCacheDatabase.replaceOrThrow(TABLE_NAME, null, values);
        //writableCacheDatabase.close();


    }

    public void resetDB() {
        Log.d("PROGRAMDBHELPER", "resetCache");
        SQLiteDatabase writableCacheDatabase = this.getWritableDatabase();
        // delete all entries
        writableCacheDatabase.execSQL(ProgramDbHelper.SQL_DELETE_ENTRIES);
        // recreate table
        writableCacheDatabase.execSQL(ProgramDbHelper.SQL_CREATE_TABLE);
    }

    public synchronized ProgramEntry GetProgram(String name) {
        MeoName meoname = new MeoName(name);
        String programname = meoname.Title;
        while (isShowBeingAdded(programname))
        {
            try {
                this.wait();

            } catch (InterruptedException ignore) {
                // log.debug("interrupted: " + ignore.getMessage());
            }
        }

        SQLiteDatabase readableCacheDatabase;

        String selection = ProgramEntry.COLUMN_NAME_PROGRAM + " = ? COLLATE NOCASE OR " + ProgramEntry.COLUMN_NAME_IMDB_TITLE + "= ? COLLATE NOCASE ";
        String sortOrder = null;
        String[] selectionArgs = { programname , programname };
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
                ProgramEntry.COLUMN_NAME_TRAKT_URL,
                ProgramEntry.COLUMN_NAME_TMDB_BACKDROP_PATH,
                ProgramEntry.COLUMN_NAME_TMDB_OVERVIEW,
                ProgramEntry.COLUMN_NAME_TMDB_POSTER_PATH,
                ProgramEntry.COLUMN_NAME_TMDB_POPULARITY,
                ProgramEntry.COLUMN_NAME_TVDB_BANNER
        };

        //while (this.getReadableDatabase().isOpen());
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

        if (c != null) {
            ProgramEntry ret = null;
            try {
                c.moveToFirst();

                ret = new ProgramEntry(this,
                        c.getString(0),  c.getString(1),  c.getString(2),  c.getString(3),
                        c.getString(4),  c.getString(5),  c.getString(6),  c.getString(7),  c.getString(8),
                        c.getString(9),  c.getString(10), c.getString(11), c.getString(12), c.getString(13),
                        c.getString(14), c.getString(15), c.getString(16), c.getString(17), c.getString(18),
                        c.getString(19), c.getString(20), c.getString(21), c.getString(22), c.getString(23),
                        c.getString(24), c.getString(25), c.getString(26), c.getString(27), c.getString(28));

                Log.d("PROGRAMDBHELPER", "getProgram: title: " + ret.ProgramName + ", channel: " + ret.ChannelInitials);
            } catch (CursorIndexOutOfBoundsException ex) {
                Log.e("PROGRAMDBHELPER", "getProgram failed to get program '" + programname + "', maybe it's not in the database yet");
            }
            if (!c.isClosed()) c.close();
            return ret;

        }
        if (c != null && !c.isClosed()) c.close();
        return null;
    }

    public synchronized boolean isInDb(String name) {
        MeoName meoname = new MeoName(name);
        String programname = meoname.Title;
        while (isShowBeingAdded(programname))
        {
            try {
                this.wait();

            } catch (InterruptedException ignore) {
                // log.debug("interrupted: " + ignore.getMessage());
            }
        }

        SQLiteDatabase readableCacheDatabase;

        String selection = ProgramEntry.COLUMN_NAME_PROGRAM + " = ? COLLATE NOCASE OR " + ProgramEntry.COLUMN_NAME_IMDB_TITLE + "= ? COLLATE NOCASE ";
        String sortOrder = null;
        String[] selectionArgs = { programname , programname };
        String[] projection = {
                ProgramEntry.COLUMN_NAME_PROGRAM,
                ProgramEntry.COLUMN_NAME_IMDB_INFOCOMPLETE
        };

        //while (this.getReadableDatabase().isOpen());
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

        if (c != null) {
            try {
                c.moveToFirst();
                String infocomplete = "";
                String title = "";
                try {
                    title = c.getString(0);
                } catch (CursorIndexOutOfBoundsException ex) {
                    Log.d("PROGRAMDBHELPER", "error in reading Title from DB for: '" + programname + "', maybe it's not in the Database.");
                    if (!c.isClosed()) c.close();
                    return false;
                }
                if (title != null && title.compareTo(programname) == 0) {
                    Log.d("PROGRAMDBHELPER", "program '" + programname + "' is in database");
                    return true;


//                    try {
//                        infocomplete = c.getString(1);
//                    } catch (CursorIndexOutOfBoundsException ex) {
//                        Log.e("PROGRAMDBHELPER", "error in reading IMDB information from DB (info complete)");
//                    }
//
//                    if (!c.isClosed()) c.close();
//
//
//                    if (infocomplete != null && infocomplete.compareTo("Info") == 0) {
//                        Log.d("PROGRAMDBHELPER", "imdb info is present");
//                        return true;
//                    } else if (infocomplete != null && ((infocomplete.compareTo("NoInfo") == 0) || infocomplete.compareTo("InfoInvalid") == 0)) {
//                        Log.d("PROGRAMDBHELPER", "imdb info is not available");
//                        return true;
//                    } else {
//                        Log.d("PROGRAMDBHELPER", "program is in database but invalid");
//                        return false;
//                    }
                }
                else return false;
            } catch (Exception ex) {
                Log.e("PROGRAMDBHELPER", "IsInDB exception: "+ ex.toString());
                return false;
            }
        }
        Log.d("PROGRAMDBHELPER", "Program '"+programname+"' is not in database");
        if (c != null && !c.isClosed()) c.close();
        return false;
    }

    @Override
    public void allInfoDownloadCompleteCallback(ProgramEntry program, String result) {
        Log.d("PROGRAMDBHELPER", "infoDownloadCompleteCallback: Called back with message: " + result);
        showIsBeingAdded(program.ProgramName);
        programAddtoDB(program);
        showWasAdded(program.ProgramName);
    }
    @Override
    public void basicInfoDownloadCompleteCallback(ProgramEntry program, String result) {
        Log.d("PROGRAMDBHELPER", "basicInfoDownloadCompleteCallback: Called back with message: " + result);
        showIsBeingAdded(program.ProgramName);
        programAddtoDB(program);
        showWasAdded(program.ProgramName);
    }
    @Override
    public void imdbInfoDownloadCompleteCallback(ProgramEntry program, String result) {
        Log.d("PROGRAMDBHELPER", "imdbInfoDownloadCompleteCallback: Called back with message: " + result);
        showIsBeingAdded(program.ProgramName);
        programAddtoDB(program);
        showWasAdded(program.ProgramName);
    }

    @Override
    public void imagesDownloadCompleteCallback(ProgramEntry program, String result) {
        Log.d("PROGRAMDBHELPER", "imagesDownloadCompleteCallback: Called back with message: " + result);
        showIsBeingAdded(program.ProgramName);
        programAddtoDB(program);
        showWasAdded(program.ProgramName);
    }

    @Override
    public void infoDownloadFailedCallback(ProgramEntry program, String result) {
        Log.d("PROGRAMDBHELPER", "infoDownloadFailedCallback: Called back with message: " + result);
        showIsBeingAdded(program.ProgramName);
        programAddtoDB(program);
        showWasAdded(program.ProgramName);
    }

    @Override
    public void imagesDownloadFailedCallback(ProgramEntry program, String result) {
        Log.d("PROGRAMDBHELPER", "imagesDownloadFailedCallback: Called back with message: " + result);
        showIsBeingAdded(program.ProgramName);
        programAddtoDB(program);
        showWasAdded(program.ProgramName);
    }
}
