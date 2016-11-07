package com.tiagosaraiva.programacaotv.programacaotv;

/**
 * ProgramacaoTV
 * <p>
 * <p>
 * Created by tfsar on Novembro/2016.
 */

public class ProgramEpisode {
    EpisodeEntry Episode;
    ProgramEntry Program;

    public ProgramEpisode(ProgramEntry p, EpisodeEntry e) {
        this.Program = p;
        this.Episode = e;
    }
}