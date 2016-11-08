package com.tiagosaraiva.programacaotv.programacaotv;

import android.content.Context;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * ProgramacaoTV
 * <p>
 * <p>
 * Created by tfsar on Novembro/2016.
 */

public class ChannelListAdapter extends ArrayAdapter<ChannelEntry> {

    public ChannelListAdapter(Context c, List<ChannelEntry> items) {
        super(c, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChannelListRowView itemView = (ChannelListRowView) convertView;
        if (null == itemView)
            itemView = ChannelListRowView.inflate(parent);
        itemView.setItem(getItem(position));
        return itemView;
    }

}
