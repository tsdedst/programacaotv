package com.tiagosaraiva.programacaotv.programacaotv;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tfsaraiva on 01/11/2016.
 */

public class EpisodeEntry {
    String Program;
    String StartTime;
    String EndTime;
    String ProgramSeason;
    String ProgramEpisode;
    String Channel;
    static final String COLUMN_NAME_PROGRAM = "PROGRAM";
    static final String COLUMN_NAME_STARTDATE = "STARTDATE";
    static final String COLUMN_NAME_ENDDATE = "ENDDATE";
    static final String COLUMN_NAME_SEASON = "SEASON";
    static final String COLUMN_NAME_EPISODE = "EPISODE";
    static final String COLUMN_NAME_CHANNEL = "CHANNEL";

    public EpisodeEntry(String prog,
                        String starttime,
                        String endtime,
                        String programseason,
                        String programepisode,
                        String channel)
    {
        MeoName meoname =new MeoName(prog);
        this.Program =  meoname.Title;
        this.StartTime =  starttime;
        this.EndTime =  endtime;
        this.ProgramSeason =  programseason;
        this.ProgramEpisode =  programepisode;
        this.Channel =  channel;
        processProgramName(prog);
    }
    public EpisodeEntry(String prog,
                        String starttime,
                        String endtime,
                        String channel)
    {
        this.Program =  prog;
        this.StartTime =  starttime;
        this.EndTime =  endtime;
        this.Channel =  channel;
        processProgramName(prog);
    }

    public void processProgramName(String programName)
    {
        MeoName meoname =new MeoName(programName);
        Program = meoname.Title;
        ProgramSeason = meoname.Season;
        ProgramEpisode = meoname.Episode;
    }




    @Override
    public String toString() {
        // return super.toString();
        String ret = Program.toString() + ": Start time: " + StartTime + ", End Time: " + EndTime;
        if ((ProgramSeason != null) && (ProgramSeason != ""))
            ret += ", Season: " + ProgramSeason;
        if ((ProgramEpisode != null) && (ProgramEpisode != ""))
            ret += ", Episode: " +ProgramEpisode;
        return ret;
    }
}