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

public class ChannelGuideGetter extends AsyncTask<Void, Void, Object> {

    ChannelDBHelper mCache;
    Context mContext;
    boolean mForceUpdate;
    String mChannelInitials;
    List<EpisodeEntry> mGuide;
    List<ProgramEntry> mPrograms;
    private ProgramDbHelper mProgramCache;
    private EpisodeDbHelper mEpisodeCache;

    public ChannelGuideGetter(Context context, ChannelDBHelper cache, String channelInitials) {
        mCache = cache;
        mContext =context;
        mForceUpdate = false;
        mChannelInitials = channelInitials;
        mGuide = new ArrayList<>();
        mPrograms = new ArrayList<>();
        mProgramCache = new ProgramDbHelper(context);
        mEpisodeCache = new EpisodeDbHelper(context);
    }
    public ChannelGuideGetter(Context context, ChannelDBHelper cache, String channelInitials, boolean forceUpdate) {
        mCache = cache;
        mContext =context;
        mForceUpdate = forceUpdate;
        mChannelInitials = channelInitials;
        mGuide = new ArrayList<>();
        mPrograms = new ArrayList<>();
        mProgramCache = new ProgramDbHelper(context);
        mEpisodeCache = new EpisodeDbHelper(context);
    }

    @Override
    protected List<EpisodeEntry> doInBackground(Void... params) {
        InitializeCaches(mChannelInitials, mForceUpdate);
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

    public JSONObject DownloadChannelJSONGuideByDateInterval(String channel, Date startDate, Date endDate) {
        return DownloadChannelJSONGuideByDateInterval(channel, DateHelper.getDateString(startDate), DateHelper.getDateString(endDate));
    }

    public JSONObject DownloadChannelJSONGuideByDateInterval(String channel, String startDate, String endDate) {
        String action = ProgramacaoTV.getAppContext().getResources().getString(R.string.sapo_base_rul) + "GetChannelByDateIntervalJSON";
        action += "?";
        action += "channelSigla=" + channel;
        action += "&";
        action += "startDate=" + startDate.split("\\s+")[0];
        action += "&";
        action += "endDate=" + endDate.split("\\s+")[0];

        return DownloadJSONAsync.downloadFromURL(action);
    }

    public JSONObject RetrieveChannelGuide(String channelInitials) {
        return RetrieveChannelGuide(channelInitials, mForceUpdate);
    }

    public JSONObject RetrieveChannelGuide(String channelInitials, boolean forceUpdate) {
        Date lastUpdateDate = mCache.getChannelEntryFromInitials(channelInitials).UpdateDate;

        Calendar updateThresholdDate = Calendar.getInstance();
        updateThresholdDate.add(Calendar.DAY_OF_MONTH, -ProgramacaoTV.getAppContext().getResources().getInteger(R.integer.update_if_older_then));

        Calendar updateStart = Calendar.getInstance();
        updateStart.add(Calendar.DAY_OF_MONTH, -ProgramacaoTV.getAppContext().getResources().getInteger(R.integer.days_to_download_in_the_past));

        Calendar updateEnd = Calendar.getInstance();
        updateEnd.add(Calendar.DAY_OF_MONTH, ProgramacaoTV.getAppContext().getResources().getInteger(R.integer.days_to_download_in_the_future));

        // Last update was before the time threshold for updating, OR forceUpdate is true
        if ((lastUpdateDate.getTime() - updateThresholdDate.getTime().getTime() < 0) || forceUpdate) {
            JSONObject newProgramList = DownloadChannelJSONGuideByDateInterval(channelInitials, updateStart.getTime(), updateEnd.getTime());
            Utilities.writeToFile(newProgramList.toString(), mContext, channelInitials);
            mCache.cacheSetUpdatedNowfromInitials(channelInitials);
            return newProgramList;
        }
        else
        {
//            Log.d("SAPOEPGHELPER", "No need to update EpisodeEntry List for: "+ channelInitials);
            try {
                return new JSONObject(Utilities.readFromFile(mContext, channelInitials));
            }
            catch (JSONException ex)
            {
                Log.e("SAPOEPGHELPER", "getProgramList Cannot read file, trying to update from web");
                return RetrieveChannelGuide(channelInitials, true);
            }
        }

    }

    public void InitializeCaches(String channelInitials) {
        InitializeCaches(channelInitials, false);
    }

    public void InitializeCaches(String channelInitials, boolean forceUpdate) {
//        List<EpisodeEntry> ret = new ArrayList<EpisodeEntry>();
        JSONObject jsonlist = RetrieveChannelGuide(channelInitials, forceUpdate);

        try {
            JSONObject response = jsonlist.getJSONObject("GetChannelByDateIntervalResponse");
            JSONObject result = response.getJSONObject("GetChannelByDateIntervalResult");
            JSONObject programs = result.getJSONObject("Programs");
            JSONArray list = programs.getJSONArray("Program");
            for(int i = 0; i < list.length(); i++){
                String sigla = Utilities.GetJSONString(list.getJSONObject(i), "ChannelSigla");
                String description = Utilities.GetJSONString(list.getJSONObject(i),"Description");
                String starttime = Utilities.GetJSONString(list.getJSONObject(i),"StartTime");
                String endtime= Utilities.GetJSONString(list.getJSONObject(i),"EndTime");
                String shortdesc = Utilities.GetJSONString(list.getJSONObject(i),"ShortDescription");
                String programname = Utilities.GetJSONString(list.getJSONObject(i),"Title");
                String id = Utilities.GetJSONString(list.getJSONObject(i),"Id");
                String programepisode = "";
                String programseason = "";
                try {
                    JSONObject values = list.getJSONObject(i).getJSONObject("Values");
                    JSONArray valueArray = values.getJSONArray("Value");
                    JSONObject seriesObject = valueArray.getJSONObject(0);
                    JSONObject episodeObject = valueArray.getJSONObject(1);
                    programseason = Utilities.GetJSONString(seriesObject, "ValueOf");
                    programepisode = Utilities.GetJSONString(episodeObject, "ValueOf");
                } catch (JSONException ex) {
                    //todo: handle : Log.d("SAPOEPGHELPER", "Problem getting season or episode object");
                }
                EpisodeEntry ep = new EpisodeEntry(programname, starttime, endtime, channelInitials);
                mProgramCache.programAddEntry(programname, shortdesc, description);
                mEpisodeCache.addEntry(ep);
//                ret.add(ep);

            }
//            return ret;

        } catch (JSONException ex) {
            Log.e("SAPOEPGHELPER", "InitializeCaches Cannot get JSON object: " +ex.toString() );
//            return null;
        }
    }
//
//    public List<ProgramEpisode> GetProgramEpisodesByChannel(String channel) {
//        List<ProgramEpisode> ret = new ArrayList<>();
//        InitializeCaches(channel);
//        List<EpisodeEntry> listtvc = GetEpisodeCache().getEpisodesOfChannel("TVC1");
//        for (int i = 0; i < listtvc.size(); i++) {
//            EpisodeEntry e = listtvc.get(i);
//            ProgramEntry p = GetProgramCache().GetProgram(e.Program);
//            ret.add(new ProgramEpisode(p, e));
//        }
//        return ret;
//    }
//
//    public ProgramDbHelper GetProgramCache()
//    {
//        return mProgramCache;
//    }
//
//    public EpisodeDbHelper GetEpisodeCache()
//    {
//        return mEpisodeCache;
//    }
}
