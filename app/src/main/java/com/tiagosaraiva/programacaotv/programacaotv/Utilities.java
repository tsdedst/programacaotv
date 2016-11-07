package com.tiagosaraiva.programacaotv.programacaotv;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * ProgramacaoTV
 * <p>
 * <p>
 * Created by tfsar on Novembro/2016.
 */

public class Utilities {

    public static void writeToFile(String data, Context context, String filename) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Utilities", "File write failed: " + e.toString());
        }
    }

    public static String readFromFile(Context context, String filename) {

        String ret = "";

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
            Log.e("Utilities", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("Utilities", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public static String GetJSONString(JSONObject obj, String name) {
        try {
            return obj.getString(name);
        }
        catch (JSONException ex)
        {
            Log.e("SAPOEPGHELPER", "Get JSON String failed on object: " + obj.toString());
            return "";
        }
    }

}
