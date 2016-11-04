package com.tiagosaraiva.programacaotv.programacaotv;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.tiagosaraiva.programacaotv.programacaotv", appContext.getPackageName());
    }


    @Test
    public void Tzsest01() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        CacheHelper c = new CacheHelper(appContext);

//        Log.d("TEST 1", "Test that initial dates are UNIX Epoch");
        String rtpUpdateDate = DateHelper.getDateTimeString(c.getUpdateDate("RTP"));
        String sicUpdateDate = DateHelper.getDateTimeString(c.getUpdateDate("SIC"));
        Log.d("TEST", "RTP update date before: "+ rtpUpdateDate);
        Log.d("TEST", "SIC update date before: "+ sicUpdateDate);
//        assertEquals("update dates before",rtpUpdateDate,  sicUpdateDate);


        Log.d("TEST 2", "Test that a an insert of the channel has the result of updating the time to the current time");
        c.cacheAddEntry("RTP");
        Thread.sleep(1000);
        c.cacheAddEntry("TVI");
        String newrtpUpdateDate = DateHelper.getDateTimeString(c.getUpdateDate("RTP"));
        String newsicUpdateDate = DateHelper.getDateTimeString(c.getUpdateDate("TVI"));
        Log.d("TEST", "new RTP update date : "+ newrtpUpdateDate);
        Log.d("TEST", "new TVI update date : "+ newsicUpdateDate);
        assertNotEquals("new RTP date is different from before ", newrtpUpdateDate,  rtpUpdateDate);
        assertNotEquals("new TVI date is different from before ", newsicUpdateDate,  sicUpdateDate);


        Log.d("TEST 3", "Test that a second update of the channel has the result of updating the time");
        Thread.sleep(1000);
        c.cacheAddEntry("TVI");
        String newnewsicUpdateDate = DateHelper.getDateTimeString(c.getUpdateDate("TVI"));
        Log.d("TEST", "new new TVI update date : "+ newnewsicUpdateDate);
        assertNotEquals("new TVI date is different from before ", newsicUpdateDate,  newnewsicUpdateDate);


        Log.d("TEST 4", "Json object");
        SapoEPGHelper s = new SapoEPGHelper(appContext);
        JSONObject hi = s.GetChannelByDateInterval("RTP1","2016-10-30","2016-11-04");

        if (hi != null ) Log.d("TEST", hi.toString());
    }

    @Test
    public void Test02() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        SapoEPGHelper s = new SapoEPGHelper(appContext);


        Log.d("TEST 5", "Json object List");
        Log.d("TEST", "Channel List: " + s.GetChannelList().toString());

        Log.d("TEST 6", "Json object PROGRAM LIST");
        Log.d("TEST", "PROGRAM List for SIC: " + s.GetProgramList("SIC").toString());



        Log.d("TEST", "CHANNEL LIST: " + s.GetChannelArrayList().toString());
        Log.d("TEST", "SIC LIST: " + s.GetProgramArrayList("SIC").toString());


    }

    @Test
    public void Test03() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        Log.d("TEST 7", "TVDB login");

        SapoEPGHelper s = new SapoEPGHelper(appContext);
        Log.d("TEST 7", "A vida é injusta");
        ProgramEntry pe1 = new ProgramEntry("A vida é injusta", "SIC", "empty meo desc", "Empty short desc" );

        Log.d("TEST", "Program Name: " +pe1.ProgramName+", IMDB rating: " + pe1.IMDBImdbrating);

        Log.d("TEST 7", "Stranger Things");
        ProgramEntry pe2 = new ProgramEntry("Stranger Things", "SIC", "empty meo desc", "Empty short desc" );
        Log.d("TEST", "Program Name: " +pe2.ProgramName+", IMDB rating: " + pe2.IMDBImdbrating);

        Log.d("TEST 7", "A Ressaca");
        ProgramEntry pe3 = new ProgramEntry("A Ressaca", "SIC", "empty meo desc", "Empty short desc" );
        Log.d("TEST", "Program Name: " +pe3.ProgramName+", IMDB rating: " + pe3.IMDBImdbrating);

        Log.d("TEST 7", "Foi assim que aconteceu");
        ProgramEntry pe4 = new ProgramEntry("Foi assim que aconteceu", "SIC", "empty meo desc", "Empty short desc" );
        Log.d("TEST", "Program Name: " +pe4.ProgramName+", IMDB rating: " + pe4.IMDBImdbrating);




    }
}
