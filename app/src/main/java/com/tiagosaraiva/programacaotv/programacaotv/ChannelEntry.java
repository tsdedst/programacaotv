package com.tiagosaraiva.programacaotv.programacaotv;

import java.util.Date;

/**
 * ProgramacaoTV
 * <p>
 * <p>
 * Created by tfsar on Novembro/2016.
 */

public class ChannelEntry {
    int Number;
    String Sigla;
    String ChannelName;
    Date UpdateDate;
    boolean Favorite;
    boolean IsDivider;
    ChannelDBHelper Database;

    public ChannelEntry(ChannelDBHelper database, int num, String sigla, String name, Date refreshed, boolean fav) {
        Number = num;
        Sigla = sigla;
        ChannelName = name;
        UpdateDate = refreshed;
        Favorite = fav;
        Database = database;
        IsDivider = false;
    }
    public ChannelEntry(String dividerText)
    {
        IsDivider = true;
        ChannelName = dividerText;
    }

    public void toggleFav() {
        Database.toggleFav(this);
    }


}
