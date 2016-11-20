package com.tiagosaraiva.programacaotv.programacaotv;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    String CHANNELLISTNAME = "LIST";
    private Context mContext;
    private ChannelDBHelper mCache;
    private MainActivity mMainActivity;

    public ChannelListGetter(MainActivity mainActivity) {
        mContext = mainActivity.getApplicationContext();
        mCache = new ChannelDBHelper(mainActivity.getApplicationContext());
        mMainActivity = mainActivity;
    }

//    public List<String> GetChannelStringList()
//    {
//        List<String> ret = new ArrayList<>();
//        List<ChannelEntry> cl = GetCache().getChannelList();
//        for (int i =1; i < cl.size(); i++)
//        {
//            String name = cl.get(i).ChannelName;
//            ret.add(name);
//        }
//        return ret;
//    }
//    public ChannelEntry GetChannelFromId(int in)
//    {
//        return GetCache().getChannelList().get(in+1);
//    }

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
            Log.d("ChannelListGetter", "Downloading list from the web");
            JSONObject newChannelList = DownloadJSONAsync.downloadFromURL(action);
            if (newChannelList != null) {
                Utilities.writeToFile(newChannelList.toString(), mContext, CHANNELLISTNAME);
                mCache.cacheAddEntry(0, CHANNELLISTNAME, CHANNELLISTNAME, DateHelper.getCurrentDateTime(), false);
                Log.d("ChannelListGetter", "Done downloading list from the web");
                return newChannelList;
            }
            else
                return null;
        }
        else
        {
            Log.d("ChannelListGetter", "Reading list from cached file");
            try {
                String file = Utilities.readFromFile(mContext, CHANNELLISTNAME);
                JSONObject jo = new JSONObject(file);
                Log.d("ChannelListGetter", "Done reading list from cached file");
                return jo;
            }
            catch (JSONException ex)
            {
                Log.e("ChannelListGetter", "getChannelList Cannot read file, trying to update from web");
                return RetrieveChannelList(true);
            }
        }
    }

    private Date getLastUpdateDate() {
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
                for(int i = 0; i < channelList.length(); i++){
                    String channelName = channelList.getJSONObject(i).getString("Name");
                    String channelInitials = channelList.getJSONObject(i).getString("Sigla");
                    ChannelEntry entry = new ChannelEntry(mCache, i + 1, channelName, channelInitials, DateHelper.getCurrentDateTime(), false);
                    mCache.cacheAddEntry(entry);
                }
            } catch (JSONException ex) {
                Log.e("ChannelListGetter", "GetChannelArrayList Cannot get JSON object: " +ex.toString());
            }
        }
    }

    public ChannelDBHelper GetCache()
    {
        return mCache;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d("ChannelListGetter","starting background activities");
        StoreChannelListIfNecessary();
        Log.d("ChannelListGetter","finished background activities");
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d("ChannelListGetter","post execution of background activities");
        mMainActivity.populateListview("finished getting list");
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    public List<ChannelEntry> getChannelMap(boolean favs) {
        List<ChannelEntry> temp = mCache.getChannelList(favs);
        return temp;
    }

    public List<ChannelEntry> getChannelMap() {
        List<ChannelEntry> temp = mCache.getChannelList();
        temp.remove(0);
        return temp;
    }


}
