package com.tiagosaraiva.programacaotv.programacaotv;

import android.content.Context;
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
        ChannelListRowView itemView = (ChannelListRowView) convertView;
        if (itemView == null)
            itemView = ChannelListRowView.inflate(parent);
        ChannelEntry ce = getItem(position);
        itemView.setItem(ce);
        return itemView;
    }

    @Override
    public int getCount() {
        int ret = 0;
        if (Items != null)
            ret = Items.size();
        return ret;
    }
}
