package com.tiagosaraiva.programacaotv.programacaotv;

import android.content.Context;
import android.support.annotation.NonNull;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.Date;

/**
 * Created by tfsar on 31/10/2016.
 */

public class SapoEPGHelper {

    private CacheHelper mCache;
    private Context mContext;

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
    public class Program {
        String Sigla;
        String ChannelName;
        String Description;
        String StartTime;
        String EndTime;
        String ShortDescription;
        String ProgramName;
        String ProgramSeason;
        String ProgramEpisode;
        String Id;

        public Program(String sigla,
                       String channelname,
                       String description,
                       String starttime,
                       String endtime,
                       String shortdesc,
                       String programname,
                       String programseason,
                       String programepisode,
                       String id)
        {
            this.Sigla =  sigla;
            this.ChannelName =  channelname;
            this.Description =  description;
            this.StartTime =  starttime;
            this.EndTime =  endtime;
            this.ShortDescription =  shortdesc;
            this.ProgramName =  programname;
            this.ProgramSeason =  programseason;
            this.ProgramEpisode =  programepisode;
            this.Id =  id;
        }
    }

    public SapoEPGHelper(Context context) {
        this.mContext = context;
        this.mCache = new CacheHelper(context);
        this.ChannelMap = GetChannelArrayList();
    }

    public JSONObject GetChannelList()
    {
        return GetChannelList(false);
    }
    public JSONObject GetChannelList(boolean forceUpdate) {
        String action = BASEURL + "GetDetailedChannelListJSON";
        Date lastUpdateDate = mCache.getUpdateDate(CHANNELLISTNAME);

        Calendar updateThresholdDate = Calendar.getInstance();
        updateThresholdDate.add(Calendar.DAY_OF_MONTH, -updateChannelListAge);

        // Last update was before the time threshold for updating, OR forceUpdate is true
        if ((lastUpdateDate.getTime() - updateThresholdDate.getTime().getTime() < 0) || forceUpdate)
        {
            Log.d("PROG", "Date of last update: " + DateHelper.getDateString(lastUpdateDate) +
                    ", Update threshold date: " +DateHelper.getDateString(updateThresholdDate.getTime()) +
                    " (" + updateChannelListAge + " days ago.)");

            JSONObject newChannelList = downloadFromDaInterwebz(action);
            writeToFile(newChannelList.toString(), mContext, CHANNELLISTNAME);
            mCache.cacheAddEntry(CHANNELLISTNAME);
            return newChannelList;
        }
        else
        {
            Log.d("PROG", "No need to update Channel List");
            try {
                JSONObject channelList = new JSONObject(readFromFile(mContext, CHANNELLISTNAME));
                return channelList;
            }
            catch (JSONException ex)
            {
                //todo: handle you shiiiiit
                Log.e("PROG", "getChannelList Cannto read file, trying to update from web");
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

        return downloadFromDaInterwebz(action);
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
            Log.d("PROG", "Channel: "+ channelInitials+
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
            Log.d("PROG", "No need to update Program List for: "+ channelInitials);
            try {
                JSONObject channelList = new JSONObject(readFromFile(mContext, channelInitials));
                return channelList;
            }
            catch (JSONException ex)
            {
                //todo: handle you shiiiiit
                Log.e("PROG", "getProgramList Cannot read file, trying to update from web");
                return GetProgramList(channelInitials, true);
            }
        }

    }

    public List<Program> GetProgramArrayList(String channelInitials) {
        return GetProgramArrayList(channelInitials, false);
    }
    public List<Program> GetProgramArrayList(String channelInitials, boolean forceUpdate) {
        List<Program> ret = new ArrayList<Program>();
        JSONObject jsonlist = GetProgramList(channelInitials, forceUpdate);
        Log.d("SAPOEPGHELPER", "JSON LIST" + jsonlist.toString());
        try {
            JSONObject response = jsonlist.getJSONObject("GetChannelByDateIntervalResponse");
            JSONObject result = response.getJSONObject("GetChannelByDateIntervalResult");
            JSONObject programs = result.getJSONObject("Programs");
            JSONArray list = programs.getJSONArray("Program");
            for(int i = 0; i < list.length(); i++){
                String sigla = GetJSONString(list.getJSONObject(i), "ChannelSigla");
                String channelname = GetJSONString(list.getJSONObject(i),"ChannelName");
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
                    Log.e("SAPOEPGHELPER", "Problem getting season or object");
                }

                Log.d("PROGRAM", "sigla " + sigla +" channelname " + channelname +" description " + description +" starttime " + starttime +" endtime " + endtime +" programseries " + programseason +" programepisode " + programepisode);
                ret.add(new Program(sigla, channelname, description, starttime,endtime,shortdesc,programname,programseason,programepisode,id));
            }
            return ret;

        } catch (JSONException ex) {
            //todo: handle you shiiiiit
            Log.e("SAPOEPGHELPER", "getChannelListStringArray Cannot get JSON object" );
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
            Log.e("EPGHELPER", "Get JSON String failed on object: " + obj.toString());
            return "";
        }
    }
    private void writeToFile(String data,Context context, String filename) {
        try {
            Log.d("PROG", "Trying to write to file: "+ filename);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("PROG", "File write failed: " + e.toString());
        }
    }
    private String readFromFile(Context context, String filename) {

        String ret = "";
        Log.d("PROG", "Trying to read from file: "+ filename);

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
            Log.e("PROG", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("PROG", "Can not read file: " + e.toString());
        }

        return ret;
    }
    private JSONObject downloadFromDaInterwebz(String url) {
        try {
            URL urlAction = new URL(url);
            Log.d("EPGHELPER", "downloadFromDaInterwebz: string: " + url);
            DownloadJSONAsync j = new DownloadJSONAsync();

            j.execute(urlAction);
            try{
                String result = j.get();
                Log.d("EPGHELPER", "downloadFromDaInterwebz: result: " + result);
                try
                {
                    JSONObject ret = new JSONObject(result);
                    return ret;
                } catch (JSONException ex)
                {
                    //todo: handle you shiiiiit
                    Log.e("EPGHELPER", "handle your shit m8");
                    return null;
                }
            } catch(InterruptedException ex)
            {
                //todo: handle you shiiiiit
                Log.e("EPGHELPER", "GetChannelByDateInterval MalformedURLException");
                return null;
            } catch (ExecutionException ex)
            {
                //todo: handle you shiiiiit
                Log.e("EPGHELPER", "GetChannelByDateInterval ExecutionException");
                return null;
            }
        }
        catch (MalformedURLException ex)
        {
            //todo: handle you shiiiiit
            Log.e("EPGHELPER", "GetChannelByDateInterval MalformedURLException");
            return null;
        }
    }
}
