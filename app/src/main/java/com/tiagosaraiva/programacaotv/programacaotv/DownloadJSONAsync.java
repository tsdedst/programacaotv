package com.tiagosaraiva.programacaotv.programacaotv;

import android.os.AsyncTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by tfsar on 31/10/2016.
 */

public class DownloadJSONAsync extends AsyncTask<URL, Void, String> {

    private HttpURLConnection urlConnection;
    //private Context mContext;
    //private ProgressDialog mDialog;
    //private TaskListener  mListener;

    public DownloadJSONAsync() {

    }

    @Override
    protected String doInBackground(URL... params) {
        StringBuilder result = new StringBuilder();

        try {
            URL url = params[0];
//            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.0.0.1", 8080));
            urlConnection = (HttpURLConnection) url.openConnection(/*proxy*/);
            urlConnection.setDoInput(true);
            urlConnection.setConnectTimeout(2 * 1000);
            urlConnection.setReadTimeout(2 * 1000);

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

        Log.d("DOWNLOADJSON", "doInBackground, final string: " + result.toString());
        return result.toString();
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d("DOWNLOADJSON", "onPreExecute");
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d("DOWNLOADJSON", "onPostExecute");

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        Log.d("DOWNLOADJSON", "onProgressUpdate");
    }

    public static JSONObject downloadFromDaInterwebz(String url) {
        try {
            URL urlAction = new URL(url);
            Log.d("DOWNLOADJSON", "downloadFromDaInterwebz: string: " + url);
            DownloadJSONAsync j = new DownloadJSONAsync();

            j.execute(urlAction);
            try{
                String result = j.get();

                try
                {
                    JSONObject ret = new JSONObject(result);
                    return ret;
                } catch (JSONException ex)
                {
                    //todo: handle you shiiiiit
                    Log.e("DOWNLOADJSON", "GetChannelByDateInterval JSON Exception, result from url: '" + url + "' is: '" + result + "'");
                    return null;
                }
            } catch(InterruptedException ex)
            {
                //todo: handle you shiiiiit
                Log.e("DOWNLOADJSON", "GetChannelByDateInterval MalformedURLException");
                return null;
            } catch (ExecutionException ex)
            {
                //todo: handle you shiiiiit
                Log.e("DOWNLOADJSON", "GetChannelByDateInterval ExecutionException");
                return null;
            }
        }
        catch (MalformedURLException ex)
        {
            //todo: handle you shiiiiit
            Log.e("DOWNLOADJSON", "GetChannelByDateInterval MalformedURLException");
            return null;
        }
    }
}
