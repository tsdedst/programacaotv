package com.tiagosaraiva.programacaotv.programacaotv;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by tfsar on 31/10/2016.
 */

public class DateHelper {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public static String getCurrentDateTimeString() {
        Date date = new Date();
        Log.d("DATEHELPER", "getCurrentDateTimeString, Date: "+ date.toString() + ", Returning: "+ dateFormat.format(date));
        return dateFormat.format(date);
    }

    public static Date getCurrentDateTime() {
        Date date = new Date();
        Log.d("DATEHELPER", "getCurrentDateTime, Date: "+ date.toString() + ", Returning: "+ dateFormat.format(date));
        return date;
    }

    public static Date getConvertedDateTime(String dateString) {
        Date convertedDate = new Date(0);
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            Log.d("DATEHELPER", "getConvertedDateTime, Date not parseable: "+ dateString +", Returning: "+ convertedDate.toString());
        }
        return convertedDate;
    }

    public static String getDateTimeString(Date dateString) {
        Log.d("DATEHELPER", "getConvertedDateTimeString, Date: "+ dateString.toString());

        return dateFormat.format(dateString);
    }

    public static String getDateString(Date dateString) {
        Log.d("DATEHELPER", "getConvertedDateTimeString, Date: "+ dateString.toString());

        return dateFormat.format(dateString).split("\\s+")[0];
    }
}
