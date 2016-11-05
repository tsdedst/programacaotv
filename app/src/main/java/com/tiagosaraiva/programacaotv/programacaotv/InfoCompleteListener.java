package com.tiagosaraiva.programacaotv.programacaotv;

/**
 * Created by tfsar on 05/11/2016.
 */

public interface InfoCompleteListener {

    public void basicInfoDownloadCompleteCallback(ProgramEntry program, String result);
    public void imdbInfoDownloadCompleteCallback(ProgramEntry program, String result);
    public void allInfoDownloadCompleteCallback(ProgramEntry program, String result);
    public void imagesDownloadCompleteCallback(ProgramEntry program, String result);
    public void infoDownloadFailedCallback(ProgramEntry program, String result);
    public void imagesDownloadFailedCallback(ProgramEntry program, String result);
}

