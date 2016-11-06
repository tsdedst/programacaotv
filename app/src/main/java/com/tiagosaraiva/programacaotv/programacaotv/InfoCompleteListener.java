package com.tiagosaraiva.programacaotv.programacaotv;

/**
 * Created by tfsar on 05/11/2016.
 */

interface InfoCompleteListener {

    void basicInfoDownloadCompleteCallback(ProgramEntry program, String result);

    void imdbInfoDownloadCompleteCallback(ProgramEntry program, String result);

    void allInfoDownloadCompleteCallback(ProgramEntry program, String result);

    void imagesDownloadCompleteCallback(ProgramEntry program, String result);

    void infoDownloadFailedCallback(ProgramEntry program, String result);

    void imagesDownloadFailedCallback(ProgramEntry program, String result);
}

