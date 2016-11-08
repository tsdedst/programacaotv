package com.tiagosaraiva.programacaotv.programacaotv;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
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

public class ChannelListRowView extends LinearLayout {
    TextView mChannelNumberTextView ;
    TextView mChannelNameTextView ;
    ImageView mChannelFavoriteImageView ;

    public ChannelListRowView(Context context) {
        this(context, null);
    }
    public ChannelListRowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public ChannelListRowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.channel_list_row_layout, this, true);
        setupChildren();
    }

    public static ChannelListRowView inflate(ViewGroup parent) {
        ChannelListRowView itemView = (ChannelListRowView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.channel_list_row, parent, false);
        return itemView;
    }

    private void setupChildren() {
        mChannelNumberTextView = (TextView) findViewById(R.id.channel_number_textview);
        mChannelNameTextView = (TextView) findViewById(R.id.channel_name_textview);
        mChannelFavoriteImageView = (ImageView) findViewById(R.id.channel_favorite_imageview);
    }

    public void setItem(ChannelEntry item) {
        mChannelNumberTextView.setText(item.Number);
        mChannelNameTextView.setText(item.ChannelName);
        // TODO: set up image URL
    }

    public ImageView getChannelFavoriteImageView () {
        return mChannelFavoriteImageView;
    }

    public TextView getChannelNameTextView() {
        return mChannelNameTextView;
    }

    public TextView getChannelNumberTextView() {
        return mChannelNumberTextView;
    }
}
