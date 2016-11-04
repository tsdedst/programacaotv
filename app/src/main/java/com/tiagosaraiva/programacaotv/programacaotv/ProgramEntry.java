package com.tiagosaraiva.programacaotv.programacaotv;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
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
    static final String IMDBPARSEURL = "http://www.imdb.com/find";
    static final String TVDBSEARCHBASEURL =  "https://api.thetvdb.com/search/series";
    static final String TVDBSEARCHmovieBASEURL =  "https://api.thetvdb.com/search/movies";
    static final String THEMOVIEDBBASEURL =  "https://api.themoviedb.org/3/search/movie";
    static final String THEMOVIEDBFINDURL =  "https://api.themoviedb.org/3/find/";
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
    static final String COLUMN_NAME_TMDB_BACKDROP_PATH = "TMDBBACKDROP_PATH";
    static final String COLUMN_NAME_TMDB_OVERVIEW = "TMDBOVERVIEW";
    static final String COLUMN_NAME_TMDB_POSTER_PATH = "TMDBPOSTER_PATH";
    static final String COLUMN_NAME_TMDB_POPULARITY = "TMDBPOPULARITY";
    static final String COLUMN_NAME_TVDB_BANNER = "TVDBBANNER";

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
    String TMDBBackdropPath;
    String TMDBOverview;
    String TMDBPosterPath;
    String TMDBPopularity;
    String TVDBBanner;

    ProgramEntry(String programname,
                 String channelinitials,
                 String shortmeodesc,
                 String meodesc,
                 String imdbinfocomplete,
                 String imdbtitle,
                 String imdbyear,
                 String imdbrate,
                 String imdbreleased,
                 String imdbruntime,
                 String imdbplot,
                 String imdbdirector,
                 String imdbgenre,
                 String imdbactors,
                 String imdbimdbrating,
                 String imdbimdbid,
                 String imdbtype,
                 String imdblanguage,
                 String imdbcountry,
                 String imdbawards,
                 String imdbposter,
                 String imdbimagefile,
                 String traktimagefile,
                 String trakturl,
                 String tmdbbackdroppath,
                 String tmdboverview,
                 String tmdbposterpath,
                 String tmdbpopularity,
                 String tvdbbanner)
    {
        this.ProgramName = stripEpSeasonInfoFromProgramName(programname);
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
        this.TMDBBackdropPath = tmdbbackdroppath;
        this.TMDBOverview = tmdboverview;
        this.TMDBPosterPath = tmdbposterpath;
        this.TMDBPopularity = tmdbpopularity;
        this.TVDBBanner = tvdbbanner;
    }

    ProgramEntry(String programname, String channelinitials, String meodesc, String shortdesc)
    {

        this.ProgramName = stripEpSeasonInfoFromProgramName(programname);
        this.ChannelInitials = channelinitials;
        this.MeoDesc = meodesc;
        this.ShortMeoDesc = shortdesc;
        this.TraktImageFile = null;
        this.TraktUrl = null;

        ProcessIMDBDInfo();
        ProcessTheMovieDBInfo();
        ProcessTVDBInfo();

        this.IMDBImageFile = null;
        this.TraktImageFile = null;
        this.TraktUrl = null;

    }

    private void ProcessIMDBDInfo()
    {
        this.IMDBImdbId = GetIMDBId();
        Log.d("PROGRAMENTRY","IMDB id: " + this.IMDBImdbId.toString());

        JSONObject info = GetIMDBInfo();

        if (info != null)
        {
            Log.d("PROGRAMENTRY","IMDB info: " + info.toString());
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
    }

    // TODO
    private void ProcessTVDBInfo()
    {
        String tvdbbanner="";
        JSONObject response = GetTVDBInfo();
        if (response != null)
        {
            Log.d("PROGRAMENTRY","TheMovieDB info: "+response.toString());
            JSONArray info = null;
            try {
                info = response.getJSONArray("data");
            } catch (JSONException ex) {
                Log.d("PROGRAMENTRY", "TVDB cannot get object 'data'");
            }
            if (info != null) {
                try {
                    tvdbbanner= GetJSONString(info.getJSONObject(0), "banner");

                } catch (JSONException ex) {
                    Log.d("PROGRAMENTRY", "TVDB cannot get object 'backdrop_path'");
                }
            }

        }
        else {
            //this.TVDBBanner = "InfoInvalid";
        }
        this.TVDBBanner = tvdbbanner;
    }

    // TODO
    private void ProcessTheMovieDBInfo()
    {
        String tmdbbackdroppath="";
        String tmdboverview="";
        String tmdbposterpath="";
        String tmdbpopularity="";
        JSONObject response = GetTheMovieDBInfo();
        if (response != null)
        {
            Log.d("PROGRAMENTRY","TheMovieDB info: "+response.toString());
            JSONArray info = null;
            try {
                info = response.getJSONArray("movie_results");
            } catch (JSONException ex) {
                Log.d("PROGRAMENTRY","TheMovieDB cannot get object 'movie_results'");
            }
            if (info == null) {
                try {
                    info = response.getJSONArray("tv_results");
                } catch (JSONException ex) {
                    Log.d("PROGRAMENTRY", "TheMovieDB cannot get object 'tv_results'");
                }
            }
            if (info != null) {
                try {
                    tmdbbackdroppath= GetJSONString(info.getJSONObject(0), "backdrop_path");
                    tmdboverview   = GetJSONString(info.getJSONObject(0), "overview");
                    tmdbposterpath = GetJSONString(info.getJSONObject(0), "poster_path");
                    tmdbpopularity = GetJSONString(info.getJSONObject(0), "popularity");
                } catch (JSONException ex) {
                    Log.d("PROGRAMENTRY", "TheMovieDB cannot get object 'tv_results'");
                }
            }

        }
        else {
            //this.TMDBInfoComplete = "InfoInvalid";
        }
        this.TMDBBackdropPath = tmdbbackdroppath;
        this.TMDBOverview = tmdboverview   ;
        this.TMDBPosterPath = tmdbposterpath ;
        this.TMDBPopularity = tmdbpopularity ;

    }

    private String GetIMDBId() {
        try {
            String action = IMDBPARSEURL;
            action += "?";
            action += "ref_=" + "nv_sr_fn";
            action += "&";
            action += "q=" + URLEncoder.encode(this.ProgramName, "UTF-8");
            action += "&";
            action += "s="+ "all";
            Log.d("PROGRAMENTRY", "GetIMDBId Getting IMDB ID from:: " + action.toString());
            return DownloadJSONAsync.parseIMDBid(action);
        } catch (UnsupportedEncodingException ex)
        {
            Log.e("PROGRAMENTRY", "GetIMDBId error encoding string: " + this.ProgramName + ", error: " + ex.toString());
            return null;
        }
    }
    private JSONObject GetIMDBInfo() {
        String action;
        // if we don't have an ID, we search based on name
        if (this.IMDBImdbId == null || this.IMDBImdbId == "") {
            try {
                action = IMDBBASEURL;
                action += "?";
                action += "s=" + URLEncoder.encode(this.ProgramName, "UTF-8");
                action += "&";
                action += "plot=" + "full";
                action += "&";
                action += "r=" + "json";
            } catch (UnsupportedEncodingException ex) {
                Log.e("PROGRAMENTRY", "GetIMDBInfo error encoding string: " + this.ProgramName + ", error: " + ex.toString());
                return null;
            }
            // if we get a search result based on the name of the show we try to get the first result's ID
            Log.d("PROGRAMENTRY","Get IMDB Description from name: "+action.toString());
            JSONObject search_result = DownloadJSONAsync.downloadFromURL(action);
            if (search_result != null) {
                JSONArray info = null;
                try {
                    info = search_result.getJSONArray("Search");
                    this.IMDBImdbId = GetJSONString(info.getJSONObject(0), "imdbID");
                } catch (JSONException ex)
                {
                    JSONObject temp;
                    try {
                        temp = search_result.getJSONObject("Response");
                        Log.d("PROGRAMENTRY","Process IMDB Description: "+temp.getJSONObject("Error").toString());
                    }catch (JSONException ex1) {
                        Log.d("PROGRAMENTRY", "Process IMDB Description. Fail to get 'Search' object from: "+ search_result.toString() + ": " + ex.toString() + " - "  +ex1.toString());
                    }
                }
            }
        }

        // since we now almost certainly have an ID, either based on Name or from parsing the site:
        if (this.IMDBImdbId != null && this.IMDBImdbId != "") {
            try {
                String action2 = IMDBBASEURL;
                action2 += "?";
                action2 += "i=" + URLEncoder.encode(this.IMDBImdbId, "UTF-8");
                action2 += "&";
                action2 += "plot=" + "full";
                action2 += "&";
                action2 += "r=" + "json";
                Log.d("PROGRAMENTRY","Get IMDB Description from ID: "+action2.toString());
                return DownloadJSONAsync.downloadFromURL(action2);
            } catch (UnsupportedEncodingException ex) {
                Log.e("PROGRAMENTRY", "GetIMDBInfo error encoding string: " + this.IMDBImdbId + ", error: " + ex.toString());

            }
        }
        Log.e("PROGRAMENTRY", "GetIMDBInfo cannot find ANY IMDB Id for : " + this.ProgramName );
        return null;

    }
    private JSONObject GetTheMovieDBInfo() {
        String api_key = ProgramacaoTV.getAppContext().getResources().getString(R.string.tmdb_api_key);
        try {
            String action;
            if (this.IMDBImdbId != null && this.IMDBImdbId != "") {
                action = THEMOVIEDBFINDURL + URLEncoder.encode(this.IMDBImdbId, "UTF-8");
                action += "?api_key=" + URLEncoder.encode(api_key, "UTF-8");
                action += "&language=pt-PT";
                action += "&external_source=imdb_id";
                Log.d("PROGRAMENTRY","Get TheMovieDB Description: "+action.toString());
                return DownloadJSONAsync.downloadFromURL(action);
            } else {
                action = THEMOVIEDBBASEURL;
                action += "?api_key=" + URLEncoder.encode(api_key, "UTF-8");
                action += "&";
                if (this.IMDBTitle == null || this.IMDBTitle == "") {
                    action += "query=" + URLEncoder.encode(this.ProgramName, "UTF-8");
                } else {
                    action += "query=" + URLEncoder.encode(this.IMDBTitle, "UTF-8");
                }

                action += "&language=pt-PT";
                Log.d("PROGRAMENTRY","Get TheMovieDB Description: "+action.toString());
                JSONObject result = DownloadJSONAsync.downloadFromURL(action);
                JSONArray info;
                try {
                    info = result.getJSONArray("results");
                    return info.getJSONObject(0);
                }
                catch (JSONException ex)
                {
                    Log.d("PROGRAMENTRY","Get TheMovieDB Description. Fail to get 'results' object from: "+ result.toString());
                    return null;
                }
            }
        } catch (UnsupportedEncodingException ex)
        {
            Log.e("PROGRAMENTRY", "GetTheMovieDBInfo error encoding string: " + this.ProgramName + ", error: " + ex.toString());
            return null;
        }
    }
    private JSONObject GetTVDBInfo() {
        String action = TVDBSEARCHBASEURL;
        String api_key = ProgramacaoTV.getAppContext().getResources().getString(R.string.tvdb_api_key);

        action += "?";
        String actionimdbid = "";
        String actionimdbname = "";
        String actionmeoname = "";

        if (this.IMDBImdbId != null && this.IMDBImdbId != "") {
            try {
                actionimdbid = action + "imdbId=" + URLEncoder.encode(this.IMDBImdbId, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Log.e("PROGRAMENTRY", "GetTVDBInfo error encoding string: " + this.ProgramName + ", error: " + ex.toString());
            }
        } else if (this.IMDBTitle != null && this.IMDBTitle != "") {
            try {
                actionimdbname = action +  "name=" + URLEncoder.encode(this.IMDBTitle, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Log.e("PROGRAMENTRY", "GetTVDBInfo error encoding string: " + this.ProgramName + ", error: " + ex.toString());
            }
        } else {
            try {
                actionmeoname = action + "name=" + URLEncoder.encode(this.ProgramName, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Log.e("PROGRAMENTRY", "GetTVDBInfo error encoding string: " + this.ProgramName + ", error: " + ex.toString());
            }
        }

        JSONObject ret = null;
        if (actionimdbid.compareTo("") != 0) {
            Log.d("PROGRAMENTRY", "Get TVDB Description from imdbId: " + actionimdbid.toString());
            ret = DownloadJSONAsync.downloadFromURLWithToken(api_key, actionimdbid);
        }
        if (ret == null && actionimdbname.compareTo("") != 0) {
            Log.d("PROGRAMENTRY", "Get TVDB Description from imdb name: " + actionimdbname.toString());
            ret = DownloadJSONAsync.downloadFromURLWithToken(api_key, actionimdbname);
        }
        if (ret == null && actionmeoname.compareTo("") != 0) {
            Log.d("PROGRAMENTRY", "Get TVDB Description form meo name: " + actionmeoname.toString());
            ret = DownloadJSONAsync.downloadFromURLWithToken(api_key, actionmeoname);
        }

        return ret;
    }
    private String stripEpSeasonInfoFromProgramName(String programName) {
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
            Log.e("PROGRAMENTRY", "Get JSON String failed to get property '"+name+"' from object: " + obj.toString());
            return "";
        }
    }
}
