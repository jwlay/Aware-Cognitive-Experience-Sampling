package com.aware.plugin.cognitive_esm.extraESM;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.ESM;
import com.aware.providers.ESM_Provider;
import com.aware.ui.ESM_Queue;
import com.aware.ui.esms.ESMFactory;
import com.aware.ui.esms.ESM_Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jan Wohlfahrt-Laymann on 2017-07-05.
 */

public class MyESM extends ESM {
    private static String TAG = "MyESM";


    public static class MyESMMonitor extends ESMMonitor {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(ESM.ACTION_AWARE_TRY_ESM)) {
                queueESM(context, intent.getStringExtra(ESM.EXTRA_ESM), true);
            }

            if (intent.getAction().equals(ESM.ACTION_AWARE_QUEUE_ESM)) {
                queueESM(context, intent.getStringExtra(ESM.EXTRA_ESM), false);
            }

            if (intent.getAction().equals(ESM.ACTION_AWARE_ESM_ANSWERED)) {
                //Check if there is a flow to follow
                processFlow(context, intent.getStringExtra(EXTRA_ANSWER));

                if (ESM_Queue.getQueueSize(context) > 0) {
                    Intent intent_ESM = new Intent(context, ESM_Queue.class);
                    intent_ESM.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent_ESM);
                } else {
                    if (Aware.DEBUG) Log.d(TAG, "ESM Queue is done!");
                    Intent esm_done = new Intent(ESM.ACTION_AWARE_ESM_QUEUE_COMPLETE);
                    context.sendBroadcast(esm_done);
                }
            }

            if (intent.getAction().equals(ESM.ACTION_AWARE_ESM_DISMISSED)) {
                Cursor esm = context.getContentResolver().query(ESM_Provider.ESM_Data.CONTENT_URI, null, ESM_Provider.ESM_Data.STATUS + " IN (" + ESM.STATUS_NEW + "," + ESM.STATUS_VISIBLE + ")", null, null);
                if (esm != null && esm.moveToFirst()) {
                    do {
                        ContentValues rowData = new ContentValues();
                        rowData.put(ESM_Provider.ESM_Data.ANSWER_TIMESTAMP, System.currentTimeMillis());
                        rowData.put(ESM_Provider.ESM_Data.STATUS, ESM.STATUS_DISMISSED);
                        context.getContentResolver().update(ESM_Provider.ESM_Data.CONTENT_URI, rowData, ESM_Provider.ESM_Data._ID + "=" + esm.getInt(esm.getColumnIndex(ESM_Provider.ESM_Data._ID)), null);
                    } while (esm.moveToNext());
                }
                if (esm != null && !esm.isClosed()) esm.close();

                if (Aware.DEBUG) Log.d(TAG, "Rest of ESM Queue is dismissed!");

                Intent esm_done = new Intent(ESM.ACTION_AWARE_ESM_QUEUE_COMPLETE);
                context.sendBroadcast(esm_done);
            }

            if (intent.getAction().equals(ESM.ACTION_AWARE_ESM_EXPIRED)) {
                Cursor esm = context.getContentResolver().query(ESM_Provider.ESM_Data.CONTENT_URI, null, ESM_Provider.ESM_Data.STATUS + " IN (" + ESM.STATUS_NEW + "," + ESM.STATUS_VISIBLE + ")", null, null);
                if (esm != null && esm.moveToFirst()) {
                    do {
                        ContentValues rowData = new ContentValues();
                        rowData.put(ESM_Provider.ESM_Data.ANSWER_TIMESTAMP, System.currentTimeMillis());
                        rowData.put(ESM_Provider.ESM_Data.STATUS, ESM.STATUS_EXPIRED);
                        context.getContentResolver().update(ESM_Provider.ESM_Data.CONTENT_URI, rowData, ESM_Provider.ESM_Data._ID + "=" + esm.getInt(esm.getColumnIndex(ESM_Provider.ESM_Data._ID)), null);
                    } while (esm.moveToNext());
                }
                if (esm != null && !esm.isClosed()) esm.close();

                if (Aware.DEBUG) Log.d(TAG, "Rest of ESM Queue is expired!");
                Intent esm_done = new Intent(ESM.ACTION_AWARE_ESM_QUEUE_COMPLETE);
                context.sendBroadcast(esm_done);
            }
        }
    }

    private static void processFlow(Context context, String current_answer) {
        if (Aware.DEBUG) {
            Log.d(MyESM.TAG, "Current answer: " + current_answer);
        }

        try {
            //Check flow
            Cursor last_esm = context.getContentResolver().query(ESM_Provider.ESM_Data.CONTENT_URI, null, ESM_Provider.ESM_Data.STATUS + "=" + ESM.STATUS_ANSWERED, null, ESM_Provider.ESM_Data.TIMESTAMP + " DESC LIMIT 1");
            if (last_esm != null && last_esm.moveToFirst()) {

                JSONObject esm_question = new JSONObject(last_esm.getString(last_esm.getColumnIndex(ESM_Provider.ESM_Data.JSON)));
                ESM_Question esm = new ESMFactory().getESM(esm_question.getInt(ESM_Question.esm_type), esm_question, last_esm.getInt(last_esm.getColumnIndex(ESM_Provider.ESM_Data._ID)));

                //Set as branched the flow rules that are not triggered
                JSONArray flows = esm.getFlows();
                for (int i = 0; i < flows.length(); i++) {
                    JSONObject flow = flows.getJSONObject(i);
                    String flowAnswer = flow.getString(ESM_Question.flow_user_answer);
                    JSONObject nextESM = flow.getJSONObject(ESM_Question.flow_next_esm).getJSONObject(EXTRA_ESM);

                    if (flowAnswer.equals(current_answer)) {
                        if (Aware.DEBUG) Log.d(MyESM.TAG, "Following next question: " + nextESM);

                        //Queued ESM
                        ContentValues rowData = new ContentValues();
                        rowData.put(ESM_Provider.ESM_Data.TIMESTAMP, System.currentTimeMillis()); //fixed issue with synching and support ordering of esms by timestamp
                        rowData.put(ESM_Provider.ESM_Data.DEVICE_ID, Aware.getSetting(context, Aware_Preferences.DEVICE_ID));
                        rowData.put(ESM_Provider.ESM_Data.JSON, nextESM.toString());
                        rowData.put(ESM_Provider.ESM_Data.EXPIRATION_THRESHOLD, nextESM.optInt(ESM_Provider.ESM_Data.EXPIRATION_THRESHOLD)); //optional, defaults to 0
                        rowData.put(ESM_Provider.ESM_Data.NOTIFICATION_TIMEOUT, nextESM.optInt(ESM_Provider.ESM_Data.NOTIFICATION_TIMEOUT)); //optional, defaults to 0
                        rowData.put(ESM_Provider.ESM_Data.STATUS, ESM.STATUS_NEW);
                        rowData.put(ESM_Provider.ESM_Data.TRIGGER, nextESM.optString(ESM_Provider.ESM_Data.TRIGGER)); //optional, defaults to ""

                        context.getContentResolver().insert(ESM_Provider.ESM_Data.CONTENT_URI, rowData);
                    } else {
                        if (Aware.DEBUG)
                            Log.d(MyESM.TAG, "Branched split: " + flowAnswer + " Skipping: " + nextESM);

                        //Branched ESM
                        ContentValues rowData = new ContentValues();
                        rowData.put(ESM_Provider.ESM_Data.TIMESTAMP, System.currentTimeMillis()); //fixed issue with synching and support ordering of esms by timestamp
                        rowData.put(ESM_Provider.ESM_Data.DEVICE_ID, Aware.getSetting(context, Aware_Preferences.DEVICE_ID));
                        rowData.put(ESM_Provider.ESM_Data.JSON, nextESM.toString());
                        rowData.put(ESM_Provider.ESM_Data.EXPIRATION_THRESHOLD, nextESM.optInt(ESM_Provider.ESM_Data.EXPIRATION_THRESHOLD)); //optional, defaults to 0
                        rowData.put(ESM_Provider.ESM_Data.NOTIFICATION_TIMEOUT, nextESM.optInt(ESM_Provider.ESM_Data.NOTIFICATION_TIMEOUT)); //optional, defaults to 0
                        rowData.put(ESM_Provider.ESM_Data.STATUS, ESM.STATUS_BRANCHED);
                        rowData.put(ESM_Provider.ESM_Data.TRIGGER, nextESM.optString(ESM_Provider.ESM_Data.TRIGGER)); //optional, defaults to ""

                        context.getContentResolver().insert(ESM_Provider.ESM_Data.CONTENT_URI, rowData);
                    }
                }
            }
            if (last_esm != null && !last_esm.isClosed()) last_esm.close();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
