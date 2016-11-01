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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.Date;

/**
 * Created by tfsar on 31/10/2016.
 */

public class SapoEPGHelper {

    private CacheHelper mCache;
    private Context mContext;
    private ProgramDbHelper mProgramCache;

    private final String BASEURL = "http://services.sapo.pt/EPG/";
    private final String CHANNELLISTNAME = "LIST";
    private final int updateChannelListAge = 5; // days
    private final int PROGRAMFUTUREDAYS = 15; // days
    private final int PROGRAMPASTDAYS = 7; // days

    public List<Channel> ChannelMap;
    public class Channel {
        String Sigla;
        String ChannelName;
        public Channel(String sigla, String name)
        {
            Sigla = sigla;
            ChannelName = name;
        }
    }


    public SapoEPGHelper(Context context) {
        this.mContext = context;
        this.mCache = new CacheHelper(context);
        this.mProgramCache = new ProgramDbHelper(context);
        this.ChannelMap = GetChannelArrayList();
    }

    public JSONObject GetChannelList()
    {
        return GetChannelList(false);
    }
    public JSONObject GetChannelList(boolean forceUpdate) {
        String action = BASEURL + "GetChannelListJSON";
        Date lastUpdateDate = mCache.getUpdateDate(CHANNELLISTNAME);

        Calendar updateThresholdDate = Calendar.getInstance();
        updateThresholdDate.add(Calendar.DAY_OF_MONTH, -updateChannelListAge);

        // Last update was before the time threshold for updating, OR forceUpdate is true
        if ((lastUpdateDate.getTime() - updateThresholdDate.getTime().getTime() < 0) || forceUpdate)
        {
            Log.d("SAPOEPGHELPER", "Date of last update: " + DateHelper.getDateString(lastUpdateDate) +
                    ", Update threshold date: " +DateHelper.getDateString(updateThresholdDate.getTime()) +
                    " (" + updateChannelListAge + " days ago.)");

            JSONObject newChannelList = DownloadJSONAsync.downloadFromDaInterwebz(action);
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
                JSONObject channelList = new JSONObject(readFromFile(mContext, CHANNELLISTNAME));
                return channelList;
            }
            catch (JSONException ex)
            {
                //todo: handle you shiiiiit
                Log.e("SAPOEPGHELPER", "getChannelList Cannot read file, trying to update from web");
                return GetChannelList(true);
            }
        }
    }

    public List<Channel> GetChannelArrayList() { return GetChannelArrayList(false); }
    public List<Channel> GetChannelArrayList(boolean forceUpdate) {
        List<Channel> ret  = new ArrayList<Channel>();
        JSONObject jsonlist = GetChannelList(forceUpdate);
        Log.d("SAPOEPGHELPER", "JSON LIST" + jsonlist.toString());
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
            //todo: handle you shiiiiit
            Log.e("SAPOEPGHELPER", "getChannelListStringArray Cannot get JSON object" );
            return null;
        }
    }

    public JSONObject GetChannelByDateInterval(String channel, Date startDate, Date endDate) {
        return GetChannelByDateInterval(channel, DateHelper.getDateString(startDate), DateHelper.getDateString(endDate));
    }
    public JSONObject GetChannelByDateInterval(String channel, String startDate, String endDate) {
        String action = BASEURL + "GetChannelByDateIntervalJSON";
        action += "?";
        action += "channelSigla=" + channel;
        action += "&";
        action += "startDate=" + startDate.split("\\s+")[0];
        action += "&";
        action += "endDate=" + endDate.split("\\s+")[0];

        return DownloadJSONAsync.downloadFromDaInterwebz(action);
    }

    public JSONObject GetProgramList(String channelInitials) {
        return GetProgramList(channelInitials, false);
    }
    public JSONObject GetProgramList(String channelInitials, boolean forceUpdate) {
        Date lastUpdateDate = mCache.getUpdateDate(channelInitials);

        Calendar updateThresholdDate = Calendar.getInstance();
        updateThresholdDate.add(Calendar.DAY_OF_MONTH, -updateChannelListAge);

        Calendar updateStart = Calendar.getInstance();
        updateStart.add(Calendar.DAY_OF_MONTH, -PROGRAMPASTDAYS);

        Calendar updateEnd = Calendar.getInstance();
        updateEnd.add(Calendar.DAY_OF_MONTH, PROGRAMFUTUREDAYS);

        // Last update was before the time threshold for updating, OR forceUpdate is true
        if ((lastUpdateDate.getTime() - updateThresholdDate.getTime().getTime() < 0) || forceUpdate) {
            Log.d("SAPOEPGHELPER", "Channel: "+ channelInitials+
                    ", Date of last update: " + DateHelper.getDateString(lastUpdateDate) +
                    ", Update threshold date: " +DateHelper.getDateString(updateThresholdDate.getTime()) +
                    " (" + updateChannelListAge + " days ago.)");

            JSONObject newChannelList = GetChannelByDateInterval(channelInitials, updateStart.getTime(), updateEnd.getTime());
            writeToFile(newChannelList.toString(), mContext, channelInitials);
            mCache.cacheAddEntry(channelInitials);
            return newChannelList;
        }
        else
        {
            Log.d("SAPOEPGHELPER", "No need to update EpisodeEntry List for: "+ channelInitials);
            try {
                JSONObject channelList = new JSONObject(readFromFile(mContext, channelInitials));
                return channelList;
            }
            catch (JSONException ex)
            {
                //todo: handle you shiiiiit
                Log.e("SAPOEPGHELPER", "getProgramList Cannot read file, trying to update from web");
                return GetProgramList(channelInitials, true);
            }
        }

    }

    public List<EpisodeEntry> GetProgramArrayList(String channelInitials) {
        return GetProgramArrayList(channelInitials, false);
    }
    public List<EpisodeEntry> GetProgramArrayList(String channelInitials, boolean forceUpdate) {
        List<EpisodeEntry> ret = new ArrayList<EpisodeEntry>();
        JSONObject jsonlist = GetProgramList(channelInitials, forceUpdate);
        Log.d("SAPOEPGHELPER", "JSON LIST" + jsonlist.toString());
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

                mProgramCache.programAddEntry(new ProgramEntry(programname, sigla, description, shortdesc));
                EpisodeEntry ep = new EpisodeEntry(programname, starttime,endtime,programseason,programepisode,id);
                Log.d("SAPOEPGHELPER", "Episode: " +ep.toString());
                ret.add(ep);
            }
            return ret;

        } catch (JSONException ex) {
            //todo: handle you shiiiiit
            Log.e("SAPOEPGHELPER", "GetProgramArrayList Cannot get JSON object: " +ex.toString() );
            return null;
        }
    }

    private String GetJSONString(JSONObject obj, String name) {
        try {
            String ret = obj.getString(name);
            return ret;
        }
        catch (JSONException ex)
        {
            Log.e("SAPOEPGHELPER", "Get JSON String failed on object: " + obj.toString());
            return "";
        }
    }
    private void writeToFile(String data,Context context, String filename) {
        try {
            Log.d("SAPOEPGHELPER", "Trying to write to file: "+ filename);
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
        Log.d("SAPOEPGHELPER", "Trying to read from file: "+ filename);

        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
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

}
