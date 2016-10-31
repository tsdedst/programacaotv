package com.tiagosaraiva.programacaotv.programacaotv;

/**
 * Created by tfsaraiva on 31/10/2016.
 */

public class ProgramEntry {

    static final String COLUMN_NAME_PROGRAM = "PROGRAMNAME";
    static final String COLUMN_NAME_CHANNEL = "PROGRAMCHANNEL";
    static final String COLUMN_NAME_MEO_DESCRIPTION = "DESCRIPTION";
    static final String COLUMN_NAME_SHORT_DESCRIPTION = "SHORTDESCRIPTION";
    static final String COLUMN_NAME_IMDB_DESCRIPTION = "IMDBDESCRIPTION";
    static final String COLUMN_NAME_IMDB_IMAGE_FILE = "PROGRAMIMAGE";
    static final String COLUMN_NAME_IMDB_URL = "PROGRAMIMDB";
    static final String COLUMN_NAME_TRAKT_IMAGE_FILE = "PROGRAMIMAGE";
    static final String COLUMN_NAME_TRAKT_URL = "PROGRAMIMDB";

    String ProgramName;
    String ChannelInitials;
    String ShortMeoDesc;
    String MeoDesc;
    String IMDBDesc;
    String IMDBImageFile;
    String IMDBUrl;
    String TraktImageFile;
    String TraktUrl;

    ProgramEntry(String programname, String channelinitials, String meodesc, String shortdesc, String imdbdesc, String imdbimagefile, String imdburl, String traktimagefile, String trakturl)
    {
        this.ProgramName = programname;
        this.ChannelInitials = channelinitials;
        this.MeoDesc = meodesc;
        this.ShortMeoDesc = shortdesc;
        this.IMDBDesc = imdbdesc;
        this.IMDBImageFile = imdbimagefile;
        this.IMDBUrl =imdburl;
        this.TraktImageFile = traktimagefile;
        this.TraktUrl = trakturl;
    }
    ProgramEntry(String programname, String channelinitials, String meodesc, String shortdesc)
    {
        this.ProgramName = programname;
        this.ChannelInitials = channelinitials;
        this.MeoDesc = meodesc;
        this.ShortMeoDesc = shortdesc;
        // todo: the rest
        this.IMDBDesc = null;
        this.IMDBImageFile = null;
        this.IMDBUrl =null;
        this.TraktImageFile = null;
        this.TraktUrl = null;
    }

    @Override
    public String toString() {
        //return super.toString();
        String ret = "Program Name: '" + ProgramName + "', Channel Initials: '" + ChannelInitials + "', Short Description: '" + ShortMeoDesc + "', Description: '" + MeoDesc + "'. ";
        if (IMDBDesc != null)
        ret += " IMDB Description: " + IMDBDesc;

        return ret;
    }
}
