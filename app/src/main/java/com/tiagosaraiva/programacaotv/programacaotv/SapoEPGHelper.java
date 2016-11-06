package com.tiagosaraiva.programacaotv.programacaotv;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by tfsar on 31/10/2016.
 */

class SapoEPGHelper {

    public List<Channel> ChannelMap;
    private CacheHelper mCache;
    private Context mContext;
    private ProgramDbHelper mProgramCache;
    private EpisodeDbHelper mEpisodeCache;
    public SapoEPGHelper(Context context) {
        this.mContext = context;
        this.mCache = new CacheHelper(context);
        this.mProgramCache = new ProgramDbHelper(context);
        this.mEpisodeCache = new EpisodeDbHelper(context);
        this.ChannelMap = GetChannelArrayList();
    }

    public JSONObject GetChannelList()
    {
        return GetChannelList(false);
    }

    public JSONObject GetChannelList(boolean forceUpdate) {
        String action = ProgramacaoTV.getAppContext().getResources().getString(R.string.sapo_base_rul) + "GetChannelListJSON";
        String CHANNELLISTNAME = "LIST";
        Date lastUpdateDate = mCache.getUpdateDate(CHANNELLISTNAME);

        Calendar updateThresholdDate = Calendar.getInstance();
        updateThresholdDate.add(Calendar.DAY_OF_MONTH, -ProgramacaoTV.getAppContext().getResources().getInteger(R.integer.update_if_older_then));

        // Last update was before the time threshold for updating, OR forceUpdate is true
        if ((lastUpdateDate.getTime() - updateThresholdDate.getTime().getTime() < 0) || forceUpdate)
        {
//            Log.d("SAPOEPGHELPER", "Date of last update: " + DateHelper.getDateString(lastUpdateDate) +
//                    ", Update threshold date: " +DateHelper.getDateString(updateThresholdDate.getTime()) +
//                    " (" + updateChannelListAge + " days ago.)");

            JSONObject newChannelList = DownloadJSONAsync.downloadFromURL(action);
            if (newChannelList != null) {
                writeToFile(newChannelList.toString(), mContext, CHANNELLISTNAME);
                mCache.cacheAddEntry(CHANNELLISTNAME);
                return newChannelList;
            }
            else
                return null;
        }
        else
        {
            Log.d("SAPOEPGHELPER", "No need to update Channel List");
            try {
                return new JSONObject(readFromFile(mContext, CHANNELLISTNAME));
            }
            catch (JSONException ex)
            {
                Log.e("SAPOEPGHELPER", "getChannelList Cannot read file, trying to update from web");
                return GetChannelList(true);
            }
        }
    }

    public List<Channel> GetChannelArrayList() { return GetChannelArrayList(false); }

    public List<Channel> GetChannelArrayList(boolean forceUpdate) {
        List<Channel> ret = new ArrayList<>();
        JSONObject jsonlist = GetChannelList(forceUpdate);
//        Log.d("SAPOEPGHELPER", "JSON LIST" + jsonlist.toString());
        try {
            JSONObject channelresponse = jsonlist.getJSONObject("GetChannelListResponse");
            JSONObject channelresult = channelresponse.getJSONObject("GetChannelListResult");
            JSONArray channelList = channelresult.getJSONArray("Channel");
            for(int i = 0; i < channelList.length(); i++){
                String channelName = channelList.getJSONObject(i).getString("Name");
                String channelInitials = channelList.getJSONObject(i).getString("Sigla");
                ret.add(new Channel(channelInitials, channelName));
            }
            return ret;

        } catch (JSONException ex) {
            Log.e("SAPOEPGHELPER", "GetChannelArrayList Cannot get JSON object" );
            return null;
        }
    }

    public JSONObject GetChannelByDateInterval(String channel, Date startDate, Date endDate) {
        return GetChannelByDateInterval(channel, DateHelper.getDateString(startDate), DateHelper.getDateString(endDate));
    }

    public JSONObject GetChannelByDateInterval(String channel, String startDate, String endDate) {
        String action = ProgramacaoTV.getAppContext().getResources().getString(R.string.sapo_base_rul) + "GetChannelByDateIntervalJSON";
        action += "?";
        action += "channelSigla=" + channel;
        action += "&";
        action += "startDate=" + startDate.split("\\s+")[0];
        action += "&";
        action += "endDate=" + endDate.split("\\s+")[0];

        return DownloadJSONAsync.downloadFromURL(action);
    }

    public JSONObject GetProgramList(String channelInitials) {
        return GetProgramList(channelInitials, false);
    }

    public JSONObject GetProgramList(String channelInitials, boolean forceUpdate) {
        Date lastUpdateDate = mCache.getUpdateDate(channelInitials);

        Calendar updateThresholdDate = Calendar.getInstance();
        updateThresholdDate.add(Calendar.DAY_OF_MONTH, -ProgramacaoTV.getAppContext().getResources().getInteger(R.integer.update_if_older_then));

        Calendar updateStart = Calendar.getInstance();
        updateStart.add(Calendar.DAY_OF_MONTH, -ProgramacaoTV.getAppContext().getResources().getInteger(R.integer.days_to_download_in_the_past));

        Calendar updateEnd = Calendar.getInstance();
        updateEnd.add(Calendar.DAY_OF_MONTH, ProgramacaoTV.getAppContext().getResources().getInteger(R.integer.days_to_download_in_the_future));

        // Last update was before the time threshold for updating, OR forceUpdate is true
        if ((lastUpdateDate.getTime() - updateThresholdDate.getTime().getTime() < 0) || forceUpdate) {
//            Log.d("SAPOEPGHELPER", "Channel: "+ channelInitials+
//                    ", Date of last update: " + DateHelper.getDateString(lastUpdateDate) +
//                    ", Update threshold date: " +DateHelper.getDateString(updateThresholdDate.getTime()) +
//                    " (" + updateChannelListAge + " days ago.)");

            JSONObject newProgramList = GetChannelByDateInterval(channelInitials, updateStart.getTime(), updateEnd.getTime());
            writeToFile(newProgramList.toString(), mContext, channelInitials);
            mCache.cacheAddEntry(channelInitials);
            return newProgramList;
        }
        else
        {
//            Log.d("SAPOEPGHELPER", "No need to update EpisodeEntry List for: "+ channelInitials);
            try {
                return new JSONObject(readFromFile(mContext, channelInitials));
            }
            catch (JSONException ex)
            {
                Log.e("SAPOEPGHELPER", "getProgramList Cannot read file, trying to update from web");
                return GetProgramList(channelInitials, true);
            }
        }

    }

    public void InitializeCaches(String channelInitials) {
        InitializeCaches(channelInitials, false);
    }

    public void InitializeCaches(String channelInitials, boolean forceUpdate) {
//        List<EpisodeEntry> ret = new ArrayList<EpisodeEntry>();
        JSONObject jsonlist = GetProgramList(channelInitials, forceUpdate);
//        Log.d("SAPOEPGHELPER", "JSON LIST" + jsonlist.toString());
        try {
            JSONObject response = jsonlist.getJSONObject("GetChannelByDateIntervalResponse");
            JSONObject result = response.getJSONObject("GetChannelByDateIntervalResult");
            JSONObject programs = result.getJSONObject("Programs");
            JSONArray list = programs.getJSONArray("Program");
            for(int i = 0; i < list.length(); i++){
                String sigla = GetJSONString(list.getJSONObject(i), "ChannelSigla");
                String description = GetJSONString(list.getJSONObject(i),"Description");
                String starttime = GetJSONString(list.getJSONObject(i),"StartTime");
                String endtime= GetJSONString(list.getJSONObject(i),"EndTime");
                String shortdesc = GetJSONString(list.getJSONObject(i),"ShortDescription");
                String programname = GetJSONString(list.getJSONObject(i),"Title");
                String id = GetJSONString(list.getJSONObject(i),"Id");
                String programepisode = "";
                String programseason = "";
                try {
                    JSONObject values = list.getJSONObject(i).getJSONObject("Values");
                    JSONArray valueArray = values.getJSONArray("Value");
                    JSONObject seriesObject = valueArray.getJSONObject(0);
                    JSONObject episodeObject = valueArray.getJSONObject(1);
                    programseason = GetJSONString(seriesObject, "ValueOf");
                    programepisode = GetJSONString(episodeObject, "ValueOf");
                } catch (JSONException ex) {
                    //todo: handle : Log.d("SAPOEPGHELPER", "Problem getting season or episode object");
                }

                mProgramCache.programAddEntry(programname, shortdesc, description);
                mEpisodeCache.addEntry(new EpisodeEntry(programname, starttime, endtime, channelInitials));
//                Log.d("SAPOEPGHELPER", "Episode: " +ep.toString());
//                ret.add(ep);
            }
//            return ret;

        } catch (JSONException ex) {
            Log.e("SAPOEPGHELPER", "InitializeCaches Cannot get JSON object: " +ex.toString() );
//            return null;
        }
    }

    public List<ProgramEpisode> GetProgramEpisodesByChannel(String channel) {
        List<ProgramEpisode> ret = new ArrayList<>();
        InitializeCaches(channel);
        List<EpisodeEntry> listtvc = GetEpisodeCache().getEpisodesOfChannel("TVC1");
        for (int i = 0; i < listtvc.size(); i++) {
            EpisodeEntry e = listtvc.get(i);
            ProgramEntry p = GetProgramCache().GetProgram(e.Program);
            ret.add(new ProgramEpisode(p, e));
        }
        return ret;
    }

    public ProgramDbHelper GetProgramCache()
    {
        return mProgramCache;
    }

    public EpisodeDbHelper GetEpisodeCache()
    {
        return mEpisodeCache;
    }

    private String GetJSONString(JSONObject obj, String name) {
        try {
            return obj.getString(name);
        }
        catch (JSONException ex)
        {
            Log.e("SAPOEPGHELPER", "Get JSON String failed on object: " + obj.toString());
            return "";
        }
    }

    private void writeToFile(String data,Context context, String filename) {
        try {
//            Log.d("SAPOEPGHELPER", "Trying to write to file: "+ filename);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("SAPOEPGHELPER", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(Context context, String filename) {

        String ret = "";
//        Log.d("SAPOEPGHELPER", "Trying to read from file: "+ filename);

        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("SAPOEPGHELPER", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("SAPOEPGHELPER", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public class Channel {
        String Sigla;
        String ChannelName;

        public Channel(String sigla, String name) {
            Sigla = sigla;
            ChannelName = name;
        }
    }

    public class ProgramEpisode {
        EpisodeEntry Episode;
        ProgramEntry Program;

        public ProgramEpisode(ProgramEntry p, EpisodeEntry e) {
            this.Program = p;
            this.Episode = e;
        }
    }

}
