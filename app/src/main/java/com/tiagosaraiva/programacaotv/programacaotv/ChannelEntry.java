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
    public ChannelEntry(int num, String sigla, String name, Date refreshed) {
        Number = num;
        Sigla = sigla;
        ChannelName = name;
        UpdateDate = refreshed;
    }


}
