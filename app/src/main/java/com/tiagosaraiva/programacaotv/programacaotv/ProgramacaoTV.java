package com.tiagosaraiva.programacaotv.programacaotv;

import android.app.Application;
import android.content.Context;

/**
 * Created by tfsar on 28/10/2016.
 */

public class ProgramacaoTV extends Application {

        private static Context context;

    public void onCreate() {
        super.onCreate();
        ProgramacaoTV.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return ProgramacaoTV.context;
    }


}
