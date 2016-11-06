package com.tiagosaraiva.programacaotv.programacaotv;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * Created by tfsar on 31/10/2016.
 */

class DownloadJSONAsync extends AsyncTask<URL, Void, String> {

    private HttpURLConnection urlConnection;
    //private Context mContext;
    //private ProgressDialog mDialog;
    //private TaskListener  mListener;
    private JSONObject mJsonLogin = null;
    private String mToken = null;

    private DownloadJSONAsync() {

    }

    private DownloadJSONAsync(JSONObject jsonlogin, String token) {
        this.mJsonLogin = jsonlogin;
        this.mToken = token;

    }
    public DownloadJSONAsync(JSONObject jsonlogin) {
        this.mJsonLogin = jsonlogin;
    }

    public static JSONObject downloadFromURL(String url) {
        return downloadFromURL(url, null, null);
    }

    private static JSONObject downloadFromURL(String url, String token) {
        return downloadFromURL(url, null, token);
    }

    private static JSONObject downloadFromURL(String url, JSONObject jsonlogin) {
        return downloadFromURL(url, jsonlogin, null);
    }

    private static JSONObject downloadFromURL(String url, JSONObject jsonlogin, String token) {
        try {
            URL urlAction = new URL(url);
            //Log.d("DOWNLOADJSON", "downloadFromDaInterwebz: string: " + url);
            DownloadJSONAsync j = new DownloadJSONAsync(jsonlogin, token);

            j.execute(urlAction);
            try{
                String result = j.get();
                if (result != null && !Objects.equals(result, "")) {
                    try {
                        return new JSONObject(result);
                    } catch (JSONException ex) {
                        Log.e("DOWNLOADJSON", "downloadFromURL JSONException, result from url: '" + url + "' is: '" + result + "'. Error: " + ex.toString());
                        return null;
                    }
                }
                else
                {
                    Log.d("DOWNLOADJSON", "downloadFromURL empty result from url: '" + url + "' :(");
                    return null;
                }
            } catch(InterruptedException ex)
            {
                Log.e("DOWNLOADJSON", "downloadFromURL InterruptedException: "+ ex.toString());
                return null;
            } catch (ExecutionException ex)
            {
                Log.e("DOWNLOADJSON", "downloadFromURL ExecutionException: "+ ex.toString());
                return null;
            }
        }
        catch (MalformedURLException ex)
        {
            Log.e("DOWNLOADJSON", "downloadFromURL MalformedURLException: " + url + ", exception: " + ex.toString());
            return null;
        }
    }

    private static String getNewTVDBToken(String apikey)
    {
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(ProgramacaoTV.getAppContext());
        String token;
        try {
            JSONObject json = new JSONObject();

            json.accumulate("apikey", apikey);
            JSONObject tokenresponse = downloadFromURL("https://api.thetvdb.com/login", json);
            try {
                token = tokenresponse.getString("token");
                SharedPreferences.Editor editor = shp.edit();

                editor.putString("TVDBToken", token);
                editor.commit();
                return token;
            } catch (JSONException ex)
            {
                Log.e("DOWNLOADJSON", "getTVDBToken JSON Exception on getting token: "+ ex.toString());
                return null;
            }
        } catch(JSONException ex) {
            Log.e("DOWNLOADJSONASYNC", "getTVDBToken failed to create json object: " + ex.toString());
            return null;
        }
    }

    public static JSONObject downloadFromURLWithToken(String apikey, String url)
    {
        //SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(ProgramacaoTV.getAppContext());
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(ProgramacaoTV.getAppContext());
        String token = shp.getString("TVDBToken", "");
        JSONObject ret;
        if (!Objects.equals(token, "")) {
            //Log.d("DOWNLOADJSON", "downloadFromURLWithToken: using an old token");
            // try to download with our existing token
            ret = downloadFromURL(url, token);
            String error;
            if (ret != null)
            {
                try {
                    error = ret.getString("Error");
                    if (error != null ){
                        // we have connection but it reports an error (maybe we need a new token)
                        ret = downloadFromURL(url, getNewTVDBToken(apikey));
                    }
                } catch ( JSONException ex) {
                    //everything is going as planed (we have a response and it has no error, we can return it)
                    //Log.d("DOWNLOADJSON", "downloadFromURLWithToken: Error trying to get 'Error' object from url: " + url + ", with token: "+ token);
                }
            }
        }
        else {
            //Log.e("DOWNLOADJSON", "downloadFromURLWithToken: Getting a new token");
            ret = downloadFromURL(url, getNewTVDBToken(apikey));
        }
        return ret;

    }

    public static String parseIMDBid(String url)
    {
        // for a web page that looks like the following:
        //<table class="findList">
        //<tr class="findResult odd"> <td class="primary_photo"> <a href="/title/tt1119646/?ref_=fn_al_tt_1" >
        //// <img src="https://images-na.ssl-images-amazon.com/images/M/MV5BMTU1MDA1MTYwMF5BMl5BanBnXkFtZTcwMDcxMzA1Mg@@._V1_UX32_CR0,0,32,44_AL_.jpg" /></a>
        //// </td> <td class="result_text"> <a href="/title/tt1119646/?ref_=fn_al_tt_1" >The Hangover</a> (2009) <br/>aka <i>"A Ressaca"</i> </td> </tr><tr class="findResult even">
        //// <td class="primary_photo"> <a href="/title/tt1231587/?ref_=fn_al_tt_2" >
        //// <img src="https://images-na.ssl-images-amazon.com/images/M/MV5BMTQwMjExODA4Ml5BMl5BanBnXkFtZTcwNTYwMDYxMw@@._V1_UX32_CR0,0,32,44_AL_.jpg" /></a>

        String find_table = "<table class=\"findList\">";
        String find_id_start_pos = "<tr class=\"findResult odd\"> <td class=\"primary_photo\"> <a href=\"/title/";
        String find_id_end_pos = "/?ref_=fn_al_tt_1\" ><img src=";
        String webpage;
        try {
            URL urlAction = new URL(url);
            DownloadJSONAsync j = new DownloadJSONAsync();
//            Log.d("DOWNLOADJSON", "parseIMDBid executing a new Asynch task on: '" + url);
            j.execute(urlAction);
            try{
                String result = j.get();
                if (result != null && !Objects.equals(result, "")) {
                    webpage = result;
                }
                else
                {
                    Log.d("DOWNLOADJSON", "parseIMDBid empty restult from url: '" + url);
                    return null;
                }
            } catch(InterruptedException ex)
            {
                Log.e("DOWNLOADJSON", "parseIMDBid InterruptedException: "+ ex.toString());
                return null;
            } catch (ExecutionException ex)
            {
                Log.e("DOWNLOADJSON", "parseIMDBid ExecutionException: "+ ex.toString());
                return null;
            }
        }
        catch (MalformedURLException ex)
        {
            Log.e("DOWNLOADJSON", "parseIMDBid MalformedURLException: "+ ex.toString());
            return null;
        }

        // get index of IMDB ID in string
        int pos_table_start = webpage.indexOf(find_table);
        int pos_id_start = webpage.indexOf(find_id_start_pos, pos_table_start);
        int pos_id_end = webpage.indexOf(find_id_end_pos);
        String result = "";
        if (pos_id_end != -1 && pos_id_start != -1 && pos_id_start < pos_id_end && pos_table_start != -1)
            result = webpage.substring(pos_id_start+find_id_start_pos.length(), pos_id_end);
        else
            Log.e("DOWNLOADJSON", "parseIMDBid result not found: " + result);
        return result;
    }

    @Override
    protected String doInBackground(URL... params) {
        StringBuilder result = new StringBuilder();

        try {
            URL url = params[0];
//            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.0.0.1", 8080));

            urlConnection = (HttpURLConnection) url.openConnection(/*proxy*/);
            if (this.mToken != null && !Objects.equals(this.mToken, "")) {
                urlConnection.setRequestProperty("Authorization", "Bearer " + this.mToken);
                //Log.d("DOWNLOADJSONASYNC", "doInBackgorund detected tokenobject : "+ this.mToken+ ", trying connection to: "+ url.toString());

            }
            urlConnection.setDoInput(true);
            urlConnection.setConnectTimeout(2 * 1000);
            urlConnection.setReadTimeout(3 * 1000);

            if (this.mJsonLogin != null) {
                //Log.d("DOWNLOADJSONASYNC", "doInBackgorund detected json login object : "+ this.mJsonLogin.toString()+ ", trying connection to: "+ url.toString());
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("POST");
                OutputStream os = urlConnection.getOutputStream();
                os.write(this.mJsonLogin.toString().getBytes("UTF-8"));
                os.close();
            }

            //Log.d("DOWNLOADJSONASYNC", "doInBackground Header fields: "+ urlConnection.getHeaderFields().toString());
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        //Log.d("DOWNLOADJSON", "doInBackground, final string: " + result.toString());
        return result.toString();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Log.d("DOWNLOADJSON", "onPreExecute");
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //Log.d("DOWNLOADJSON", "onPostExecute");

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        // Log.d("DOWNLOADJSON", "onProgressUpdate");
    }


}
