package com.tiagosaraiva.programacaotv.programacaotv;

import java.util.Objects;

/**
 * Created by tfsaraiva on 01/11/2016.
 */

class EpisodeEntry {
    static final String COLUMN_NAME_PROGRAM = "PROGRAM";
    static final String COLUMN_NAME_STARTDATE = "STARTDATE";
    static final String COLUMN_NAME_ENDDATE = "ENDDATE";
    static final String COLUMN_NAME_SEASON = "SEASON";
    static final String COLUMN_NAME_EPISODE = "EPISODE";
    static final String COLUMN_NAME_CHANNEL = "CHANNEL";
    String Program;
    String StartTime;
    String EndTime;
    String ProgramSeason;
    String ProgramEpisode;
    String Channel;

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

    private void processProgramName(String programName)
    {
        MeoName meoname =new MeoName(programName);
        Program = meoname.Title;
        ProgramSeason = meoname.Season;
        ProgramEpisode = meoname.Episode;
    }




    @Override
    public String toString() {
        // return super.toString();
        String ret = Program + ": Start time: " + StartTime + ", End Time: " + EndTime;
        if ((ProgramSeason != null) && (!Objects.equals(ProgramSeason, "")))
            ret += ", Season: " + ProgramSeason;
        if ((ProgramEpisode != null) && (!Objects.equals(ProgramEpisode, "")))
            ret += ", Episode: " +ProgramEpisode;
        return ret;
    }
}