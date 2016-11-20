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

public class ChannelListRowView extends LinearLayout implements View.OnClickListener {

    TextView mChannelNameTextView ;
    ImageView mChannelFavoriteImageView ;
    ChannelEntry mChannelEntry;

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
        mChannelNameTextView = (TextView) findViewById(R.id.channel_name_textview);
        mChannelNameTextView.setOnClickListener(this);
        mChannelFavoriteImageView = (ImageView) findViewById(R.id.channel_favorite_imageview);
        mChannelFavoriteImageView.setOnClickListener(this);
    }

    public void setItem(ChannelEntry item) {
        mChannelEntry = item;
        mChannelNameTextView.setText(mChannelEntry.Number + ". " + mChannelEntry.ChannelName);
        // TODO: set up image URL\
        Drawable id;
        if (mChannelEntry.Favorite)
            id = getResources().getDrawable(R.drawable.ic_star_black_48px);
        else
            id = getResources().getDrawable(R.drawable.ic_star_border_black_48px);

        mChannelFavoriteImageView.setImageDrawable(id);
    }

    @Override
    public void onClick(View v) {
        if (v == mChannelFavoriteImageView) {
            //do something with myString
            Log.d("ChannelListRowView", "item favorite was clicked: " + mChannelEntry.ChannelName);
            mChannelEntry.toggleFav();
            setItem(mChannelEntry);
        } else {
            //do something with myString
            Log.d("ChannelListRowView", "item text or empty space was clicked: " + mChannelEntry.ChannelName);
        }
    }

    public ImageView getChannelFavoriteImageView () {
        return mChannelFavoriteImageView;
    }

    public TextView getChannelNameTextView() {
        return mChannelNameTextView;
    }


}
