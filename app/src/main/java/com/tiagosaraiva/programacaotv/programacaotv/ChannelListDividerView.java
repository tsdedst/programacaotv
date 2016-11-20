package com.tiagosaraiva.programacaotv.programacaotv;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * ProgramacaoTV
 * <p>
 * <p>
 * Created by tfsar on Novembro/2016.
 */

public class ChannelListDividerView extends LinearLayout implements View.OnClickListener {

    TextView mChannelNameTextView ;

    public ChannelListDividerView(Context context) {
        this(context, null);
    }
    public ChannelListDividerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public ChannelListDividerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.channel_list_separator_layout, this, true);
        setupDivider();
    }

    public static ChannelListDividerView inflate(ViewGroup parent) {
        ChannelListDividerView itemView = (ChannelListDividerView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.channel_list_separator, parent, false);
        return itemView;
    }
    private void setupDivider() {
        mChannelNameTextView = (TextView) findViewById(R.id.channel_list_divider_textview);
    }

    public void setItem(String dividerText) {
        mChannelNameTextView.setText(dividerText);
    }

    @Override
    public void onClick(View v) {

    }



}
