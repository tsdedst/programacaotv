package com.tiagosaraiva.programacaotv.programacaotv;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tfsar on 05/11/2016.
 */

public class MeoName {
    public String Episode;
    public String Season;
    public String Title;
    public String OriginalName;

    MeoName(String name){
        OriginalName = name;
        String ret = OriginalName;
        Matcher seasonepisode_match = Pattern.compile(ProgramacaoTV.getAppContext().getResources().getString(R.string.season_episode_match_regex_pattern)).matcher(ret);

        if (seasonepisode_match.find( )) {
            // Now create matcher object.
            String match1 =seasonepisode_match.group(0);
            ret = seasonepisode_match.replaceAll("");

            Matcher getseasonpart_match = Pattern.compile("(\\s+)T(\\d+)").matcher(match1);
            if (getseasonpart_match.find())
            {
                String match2 =getseasonpart_match.group(0);
                Matcher replaceseasonpart_match = Pattern.compile("(\\s+)T").matcher(match2);
                if (replaceseasonpart_match.find()){
                    this.Season = replaceseasonpart_match.replaceAll("");
                }
            }
        }

        Matcher episode_match = Pattern.compile(ProgramacaoTV.getAppContext().getResources()
                                                .getString(R.string.episode_match_regex_pattern)).matcher(ret);
        if (episode_match.find( )) {
            String episodepart = episode_match.group(0);
            ret = episode_match.replaceAll("");

            // Now create matcher object.

            Matcher replaceeppart_match = Pattern.compile(ProgramacaoTV.getAppContext().getResources()
                                                .getString(R.string.episode_match_remove_regex_pattern)).matcher(episodepart);
            if (replaceeppart_match.find()) {
                this.Episode = replaceeppart_match.replaceAll("");
            }
        }
        if (this.Season == null)
            this.Season = "";
        if (this.Episode == null)
            this.Episode = "";

        this.Title = ret;
    }

}
