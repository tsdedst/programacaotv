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
    String Id;

    public EpisodeEntry(String prog,
                        String starttime,
                        String endtime,
                        String programseason,
                        String programepisode,
                        String id)
    {
        this.Program =  prog;
        this.StartTime =  starttime;
        this.EndTime =  endtime;
        this.ProgramSeason =  programseason;
        this.ProgramEpisode =  programepisode;
        this.Id =  id;
        processProgramName(prog);

    }
    public void processProgramName(String programName)
    {

        Matcher seasonepisode_match = Pattern.compile("(\\s+)T(\\d+)(\\s+)-(\\s+)(Ep\\.)(\\s+)(\\d+)").matcher(programName);
        if (seasonepisode_match.find( )) {
            // Now create matcher object.
            Matcher getseasonpart_match = Pattern.compile("(\\s+)T(\\d+)").matcher(seasonepisode_match.group(0));
            if (getseasonpart_match.find())
            {
                Matcher replaceseasonpart_match = Pattern.compile("(\\s+)T").matcher(getseasonpart_match.group(0));
                if (replaceseasonpart_match.find()){
                    this.ProgramSeason = replaceseasonpart_match.replaceAll("");
                }
            }
        }

        Matcher episode_match = Pattern.compile("(\\s+)-(\\s+)(Ep\\.)(\\s+)(\\d+)").matcher(programName);
        if (episode_match.find( )) {

            // Now create matcher object.
            Matcher replaceeppart_match = Pattern.compile("(\\s+)-(\\s+)(Ep\\.)(\\s+)").matcher(episode_match.group(0));
            if (replaceeppart_match.find()) {
                this.ProgramEpisode = replaceeppart_match.replaceAll("");
            }
        }
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