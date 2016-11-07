package com.tiagosaraiva.programacaotv.programacaotv;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * ProgramacaoTV
 * <p>
 * <p>
 * Created by tfsar on Novembro/2016.
 */

public class ChannelListGetter extends AsyncTask<Void, Void, Void>{
    public List<ChannelEntry> ChannelMap;

    private Context mContext;
    private ChannelDBHelper mCache;
    private MainActivity mMainActivity;
    String CHANNELLISTNAME = "LIST";

    public ChannelListGetter(MainActivity mainActivity) {
        mContext = mainActivity.getApplicationContext();
        mCache = new ChannelDBHelper(mainActivity.getApplicationContext());
        mMainActivity = mainActivity;
    }

    public List<String> GetChannelStringList()
    {
        List<String> ret = new ArrayList<>();
        for (int i =0; i < ChannelMap.size(); i++)
        {
            ret.add(ChannelMap.get(i).ChannelName);
        }
        return ret;
    }

    private JSONObject RetrieveChannelList()
    {
        return RetrieveChannelList(false);
    }

    private JSONObject RetrieveChannelList(boolean forceUpdate) {
        String action = ProgramacaoTV.getAppContext().getResources().getString(R.string.sapo_base_rul) + "GetChannelListJSON";

        Date lastUpdateDate = getLastUpdateDate();

        Calendar updateThresholdDate = Calendar.getInstance();
        updateThresholdDate.add(Calendar.DAY_OF_MONTH, -ProgramacaoTV.getAppContext().getResources().getInteger(R.integer.update_if_older_then));

        // Last update was before the time threshold for updating, OR forceUpdate is true
        if ((lastUpdateDate.getTime() - updateThresholdDate.getTime().getTime() < 0) || forceUpdate)
        {
            JSONObject newChannelList = DownloadJSONAsync.downloadFromURL(action);
            if (newChannelList != null) {
                Utilities.writeToFile(newChannelList.toString(), mContext, CHANNELLISTNAME);
                mCache.cacheAddEntry(0, CHANNELLISTNAME, CHANNELLISTNAME, DateHelper.getCurrentDateTime());
                return newChannelList;
            }
            else
                return null;
        }
        else
        {
            try {
                return new JSONObject(Utilities.readFromFile(mContext, CHANNELLISTNAME));
            }
            catch (JSONException ex)
            {
                Log.e("ChannelListGetter", "getChannelList Cannot read file, trying to update from web");
                return RetrieveChannelList(true);
            }
        }
    }
    private Date getLastUpdateDate()
    {
        Date ret = new Date(0);
        ChannelEntry list = mCache.getChannelEntryFromInitials(CHANNELLISTNAME);
        if (list != null)
            ret = list.UpdateDate;
        return ret;

    }
    private void StoreChannelListIfNecessary() { StoreChannelListIfNecessary(false); }

    private void StoreChannelListIfNecessary(boolean forceUpdate) {
        Date lastUpdateDate = getLastUpdateDate();
        Calendar updateThresholdDate = Calendar.getInstance();
        updateThresholdDate.add(Calendar.DAY_OF_MONTH, -ProgramacaoTV.getAppContext().getResources().getInteger(R.integer.update_if_older_then));

        // Last update was before the time threshold for updating, OR forceUpdate is true
        if ((lastUpdateDate.getTime() - updateThresholdDate.getTime().getTime() < 0) || forceUpdate) {

            JSONObject jsonlist = RetrieveChannelList(forceUpdate);

            try {
                JSONObject channelresponse = jsonlist.getJSONObject("GetChannelListResponse");
                JSONObject channelresult = channelresponse.getJSONObject("GetChannelListResult");
                JSONArray channelList = channelresult.getJSONArray("Channel");
                for(int i = 1; i <= channelList.length(); i++){
                    String channelName = channelList.getJSONObject(i).getString("Name");
                    String channelInitials = channelList.getJSONObject(i).getString("Sigla");
                    ChannelEntry entry = new ChannelEntry(i, channelInitials, channelName, DateHelper.getCurrentDateTime());
                    mCache.cacheAddEntry(entry);
                }
            } catch (JSONException ex) {
                Log.e("ChannelListGetter", "GetChannelArrayList Cannot get JSON object" );
            }
        }
    }

    private void RefreshChannelMap()
    {
        this.ChannelMap = mCache.getChannelList();
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d("ChannelListGetter","starting background activities");
        StoreChannelListIfNecessary();
        RefreshChannelMap();
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.d("ChannelListGetter","no?");
        Log.d("ChannelListGetter","finished getting channel list: " + GetChannelStringList().toString());
        Log.d("ChannelListGetter","calling back to mainactivity");

        mMainActivity.populateListview("ok");
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }




}
