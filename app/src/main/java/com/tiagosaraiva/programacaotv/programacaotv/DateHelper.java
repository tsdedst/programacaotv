package com.tiagosaraiva.programacaotv.programacaotv;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by tfsar on 31/10/2016.
 */

class DateHelper {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(ProgramacaoTV.getAppContext().getResources().getString(R.string.date_format), Locale.getDefault());

    public static String getCurrentDateTimeString() {
        Date date = new Date();
//        Log.d("DATEHELPER", "getCurrentDateTimeString, Date: "+ date.toString() + ", Returning: "+ dateFormat.format(date));
        return dateFormat.format(date);
    }

    public static Date getCurrentDateTime() {

//        Log.d("DATEHELPER", "getCurrentDateTime, Date: "+ date.toString() + ", Returning: "+ dateFormat.format(date));
        return new Date();
    }

    public static Date getConvertedDateTime(String dateString) {
        Date convertedDate = new Date(0);
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (java.text.ParseException e) {
            Log.d("DATEHELPER", "getConvertedDateTime, Date not parseable: "+ dateString +", Returning: "+ convertedDate.toString());
        }
        return convertedDate;
    }

    public static String getDateTimeString(Date dateString) {
//        Log.d("DATEHELPER", "getConvertedDateTimeString, Date: "+ dateString.toString());
        return dateFormat.format(dateString);
    }

    public static String getDateString(Date dateString) {
//        Log.d("DATEHELPER", "getConvertedDateTimeString, Date: "+ dateString.toString());
        return dateFormat.format(dateString).split("\\s+")[0];
    }
}
