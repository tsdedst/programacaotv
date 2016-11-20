package com.tiagosaraiva.programacaotv.programacaotv;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * ProgramacaoTV
 * <p>
 * <p>
 * Created by tfsar on Novembro/2016.
 */

public class ChannelListAdapter extends ArrayAdapter<ChannelEntry> {
    List<ChannelEntry> Items;

    public ChannelListAdapter(Context c, List<ChannelEntry> items) {
        super(c, 0, items);
        this.Items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item;

        ChannelEntry ce = getItem(position);

        if (ce.IsDivider)
        {
            ChannelListDividerView itemView;
            if ((convertView != null) && (convertView instanceof ChannelListDividerView)) {
                itemView = (ChannelListDividerView) convertView;
            }
            else {
                itemView = ChannelListDividerView.inflate(parent);
            }
            itemView.setItem(ce.ChannelName);
            item = itemView;
        }
        else {
            ChannelListRowView itemView;
            if ((convertView != null) && (convertView instanceof ChannelListRowView)) {
                itemView = (ChannelListRowView) convertView;
            }
            else {
                itemView = ChannelListRowView.inflate(parent);
            }
            itemView.setItem(ce);
            item = itemView;
        }
        return item;
    }

    @Override
    public int getCount() {
        int ret = 0;
        if (Items != null)
            ret = Items.size();
        return ret;
    }
}

