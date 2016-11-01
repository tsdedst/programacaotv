package com.tiagosaraiva.programacaotv.programacaotv;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tfsaraiva on 31/10/2016.
 */

public class ProgramEntry {

    static final String IMDBBASEURL = "http://www.omdbapi.com/";
    static final String COLUMN_NAME_PROGRAM = "PROGRAMNAME";
    static final String COLUMN_NAME_CHANNEL = "PROGRAMCHANNEL";
    static final String COLUMN_NAME_MEO_DESCRIPTION = "DESCRIPTION";
    static final String COLUMN_NAME_SHORT_DESCRIPTION = "SHORTDESCRIPTION";
    static final String COLUMN_NAME_IMDB_INFOCOMPLETE= "IMDBINFOCOMPLETE";
    static final String COLUMN_NAME_IMDB_IMAGE_FILE = "PROGRAMIMAGE";
    static final String COLUMN_NAME_IMDB_TITLE = "IMDBTITLE";
    static final String COLUMN_NAME_IMDB_YEAR = "IMDBYEAR";
    static final String COLUMN_NAME_IMDB_RATE = "IMDBRATE";
    static final String COLUMN_NAME_IMDB_RELEASED = "IMDBRELEASED";
    static final String COLUMN_NAME_IMDB_RUNTIME = "IMDBRUNTIME";
    static final String COLUMN_NAME_IMDB_PLOT = "IMDBPLOT";
    static final String COLUMN_NAME_IMDB_DIRECTOR = "IMDBDIRECTOR";
    static final String COLUMN_NAME_IMDB_GENRE = "IMDBGENRE";
    static final String COLUMN_NAME_IMDB_ACTORS = "IMDBACTORS";
    static final String COLUMN_NAME_IMDB_IMDBRATING = "IMDBIMDBRATING";
    static final String COLUMN_NAME_IMDB_IMDBID = "IMDBIMDBID";
    static final String COLUMN_NAME_IMDB_TYPE = "IMDBTYPE";
    static final String COLUMN_NAME_IMDB_LANGUAGE = "IMDBLANGUAGE";
    static final String COLUMN_NAME_IMDB_COUNTRY = "IMDBCOUNTRY";
    static final String COLUMN_NAME_IMDB_AWARDS = "IMDBAWARDS";
    static final String COLUMN_NAME_IMDB_POSTER = "IMDBPOSTER";
    static final String COLUMN_NAME_TRAKT_IMAGE_FILE = "TRAKTIMAGE";
    static final String COLUMN_NAME_TRAKT_URL = "TRAKTID";

    String ProgramName;
    String ChannelInitials;
    String ShortMeoDesc;
    String MeoDesc;
    String IMDBInfoComplete;
    String IMDBTitle;
    String IMDBYear;
    String IMDBRate;
    String IMDBReleased;
    String IMDBRuntime;
    String IMDBPlot;
    String IMDBDirector;
    String IMDBGenre;
    String IMDBActors;
    String IMDBImdbrating;
    String IMDBImdbId;
    String IMDBType;
    String IMDBLanguage;
    String IMDBCountry;
    String IMDBAwards;
    String IMDBPoster;
    String IMDBImageFile;
    String TraktImageFile;
    String TraktUrl;

    ProgramEntry(String programname, String channelinitials, String shortmeodesc, String meodesc, String imdbinfocomplete, String imdbtitle, String imdbyear, String imdbrate, String imdbreleased, String imdbruntime, String imdbplot, String imdbdirector, String imdbgenre, String imdbactors, String imdbimdbrating, String imdbimdbid, String imdbtype, String imdblanguage, String imdbcountry, String imdbawards, String imdbposter, String imdbimagefile, String traktimagefile, String trakturl )
    {
        this.ProgramName = getSafeProgramName(programname);
        this.ChannelInitials = channelinitials;
        this.ShortMeoDesc = shortmeodesc;
        this.MeoDesc = meodesc;
        this.IMDBInfoComplete = imdbinfocomplete;
        this.IMDBTitle = imdbtitle;
        this.IMDBYear = imdbyear;
        this.IMDBRate = imdbrate;
        this.IMDBReleased = imdbreleased;
        this.IMDBRuntime = imdbruntime;
        this.IMDBPlot = imdbplot;
        this.IMDBDirector = imdbdirector;
        this.IMDBGenre = imdbgenre;
        this.IMDBActors = imdbactors;
        this.IMDBImdbrating = imdbimdbrating;
        this.IMDBImdbId = imdbimdbid;
        this.IMDBType = imdbtype;
        this.IMDBLanguage = imdblanguage;
        this.IMDBCountry = imdbcountry;
        this.IMDBAwards = imdbawards;
        this.IMDBPoster = imdbposter;
        this.IMDBImageFile = imdbimagefile;
        this.TraktImageFile = traktimagefile;
        this.TraktUrl = trakturl;
    }
    ProgramEntry(String programname, String channelinitials, String meodesc, String shortdesc)
    {
        this.ProgramName = getSafeProgramName(programname);
        this.ChannelInitials = channelinitials;
        this.MeoDesc = meodesc;
        this.ShortMeoDesc = shortdesc;
        this.TraktImageFile = null;
        this.TraktUrl = null;

        processIMDBDescription(this.ProgramName);

        // todo: the rest
        this.IMDBImageFile = null;
        this.TraktImageFile = null;
        this.TraktUrl = null;


    }

    private String getSafeProgramName(String programName)
    {
        String ret = programName;
        Matcher seasonepisode_match = Pattern.compile("(\\s+)T(\\d+)(\\s+)-(\\s+)(Ep\\.)(\\s+)(\\d+)").matcher(programName);
        if (seasonepisode_match.find()) {
            ret = seasonepisode_match.replaceAll("");
        }

        Matcher episode_match = Pattern.compile("(\\s+)-(\\s+)(Ep\\.)(\\s+)(\\d+)").matcher(ret);
        if (episode_match.find( )) {
            ret = episode_match.replaceAll("");
        }

        return ret;
    }

    public String processIMDBDescription(String programName)
    {
        JSONObject info = GetIMDBInfo(programName);
        if (info != null)
        {
            Log.d("PROGRAMENTRY",info.toString());
            if (GetJSONString(info, "Response").compareTo("True") == 0) {
                this.IMDBInfoComplete = "Info";
                this.IMDBTitle = GetJSONString(info, "Title");
                this.IMDBYear = GetJSONString(info,"Year");
                this.IMDBRate = GetJSONString(info, "Rated");
                this.IMDBReleased = GetJSONString(info, "Released");
                this.IMDBRuntime = GetJSONString(info, "Runtime");
                this.IMDBPlot = GetJSONString(info, "Plot");
                this.IMDBDirector = GetJSONString(info, "Director");
                this.IMDBGenre = GetJSONString(info, "Genre");
                this.IMDBActors = GetJSONString(info, "Actors");
                this.IMDBImdbrating = GetJSONString(info, "imdbRating");
                this.IMDBImdbId = GetJSONString(info, "imdbID");
                this.IMDBType = GetJSONString(info, "Type");
                this.IMDBLanguage = GetJSONString(info, "Language");
                this.IMDBCountry = GetJSONString(info, "Country");
                this.IMDBAwards = GetJSONString(info, "Awards");
                this.IMDBPoster = GetJSONString(info, "Poster");
            }
            else
            {
                Log.d("PROGRAMENTRY", "IMDB information not found");
                this.IMDBInfoComplete = "NoInfo";
            }
        }
        else {
            this.IMDBInfoComplete = "InfoInvalid";
        }
        return null;
    }
    public JSONObject GetIMDBInfo(String programName) {
        try {
            String action = IMDBBASEURL;
            action += "?";
            action += "t=" + URLEncoder.encode(programName, "UTF-8");
            action += "&";
            action += "y=" + "";
            action += "&";
            action += "plot=" + "full";
            action += "&";
            action += "r=" + "json";
            return DownloadJSONAsync.downloadFromDaInterwebz(action);
        } catch (UnsupportedEncodingException ex)
        {
            Log.e("PROGRAMENTRY", "error encoding string: " + programName + ", error: " + ex.toString());
            return null;
        }

    }


    @Override
    public String toString() {
        //return super.toString();
        String ret = "Program Name: '" + ProgramName + "', Channel Initials: '" + ChannelInitials + "', Short Description: '" + ShortMeoDesc + "', Description: '" + MeoDesc + "'. ";
        if (IMDBPlot != null)
        ret += " IMDB Plot: " + IMDBPlot;

        return ret;
    }

    private String GetJSONString(JSONObject obj, String name) {
        try {
            String ret = obj.getString(name);
            return ret;
        }
        catch (JSONException ex)
        {
            Log.e("PROGRAMENTRY", "Get JSON String failed on object: " + obj.toString());
            return "";
        }
    }
}
