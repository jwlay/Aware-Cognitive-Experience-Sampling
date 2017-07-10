package com.aware.plugin.cognitive_esm;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import com.aware.Applications;
import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.ESM;
import com.aware.plugin.cognitive_esm.extraESM.MyESM;
import com.aware.providers.ESM_Provider;
import com.aware.ui.ESM_Queue;
import com.aware.ui.PermissionsHandler;
import com.aware.ui.esms.ESMFactory;
import com.aware.utils.Aware_Plugin;

import java.util.ArrayList;

import static com.aware.ESM.EXTRA_ESM;
import static com.aware.ESM.queueESM;

public class Plugin extends Aware_Plugin {

    public static String ACTION_AWARE_COGNITIVE_ESM = "action_aware_cognitive_esm";

    private static Intent esmSrv = null;
    private MyESM.MyESMMonitor esmMonitor;

    @Override
    public void onCreate() {
        super.onCreate();

        TAG = "AWARE::"+getResources().getString(R.string.app_name);

        /**
         * Plugins share their current status, i.e., context using this method.
         * This method is called automatically when triggering
         * {@link Aware#ACTION_AWARE_CURRENT_CONTEXT}
         **/
        CONTEXT_PRODUCER = new ContextProducer() {
            @Override
            public void onContext() {
                //Broadcast your context here
            }
        };

        //Add permissions you need (Android M+).
        //By default, AWARE asks access to the #Manifest.permission.WRITE_EXTERNAL_STORAGE
        REQUIRED_PERMISSIONS.add(Manifest.permission.INTERNET);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //To sync data to the server, you'll need to set this variables from your ContentProvider
        DATABASE_TABLES = Provider.DATABASE_TABLES;
        TABLES_FIELDS = Provider.TABLES_FIELDS;
        CONTEXT_URIS = new Uri[]{ Provider.TableOne_Data.CONTENT_URI }; //this syncs dummy TableOne_Data to server

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_AWARE_COGNITIVE_ESM);
        filter.addAction(MyESM.ACTION_AWARE_TRY_ESM);
        filter.addAction(MyESM.ACTION_AWARE_QUEUE_ESM);
        filter.addAction(MyESM.ACTION_AWARE_ESM_ANSWERED);
        filter.addAction(MyESM.ACTION_AWARE_ESM_DISMISSED);
        filter.addAction(MyESM.ACTION_AWARE_ESM_EXPIRED);
        filter.addAction(MyESM.ACTION_AWARE_ESM_REPLACED);

        esmMonitor = new MyESM.MyESMMonitor();
        registerReceiver(esmMonitor, filter);
    }

    //This function gets called every 5 minutes by AWARE to make sure this plugin is still running.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (PERMISSIONS_OK) {

            DEBUG = Aware.getSetting(this, Aware_Preferences.DEBUG_FLAG).equals("true");

            //Initialize our plugin's settings
            Aware.setSetting(this, Settings.STATUS_PLUGIN_COGNITIVE_EXPERIENCE, true);

            Aware.setSetting(this, Aware_Preferences.STATUS_ESM, true);

            if (Settings.getTests() != null)
                for (String test : Settings.getTests()) {
                    if (DEBUG) Log.v(TAG, "Creating test: "+test);
                    ExecuterParams params = new ExecuterParams(this, test);
                    TestExecuterTask task = new TestExecuterTask();
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
                }
            else if (DEBUG) Log.i(TAG, "No tests found");

            esmSrv = new Intent(this, ESM.class);

            //Initialise AWARE instance in plugin
            Aware.startAWARE(this);
            this.startService(esmSrv);
        } else {
            if (DEBUG) Log.w(TAG, "Permission not ok!");
            Intent permissions = new Intent(this, PermissionsHandler.class);
            permissions.putExtra(PermissionsHandler.EXTRA_REQUIRED_PERMISSIONS, REQUIRED_PERMISSIONS);
            permissions.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(permissions);
        }

        return START_STICKY;
    }

    private static class ExecuterParams {
        Context context;
        String test;

        ExecuterParams(Context context, String test) {
            this.context = context;
            this.test = test;
        }
    }

    private class TestExecuterTask extends AsyncTask<ExecuterParams, Void, Void> {

        @Override
        protected Void doInBackground(ExecuterParams... params) {
            Context context = params[0].context;
            ESMFactory esmFactory = new ESMFactory();
            String test = params[0].test;
            new TestExecuter().createTest(context, esmFactory, test);
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Aware.setSetting(this, Settings.STATUS_PLUGIN_COGNITIVE_EXPERIENCE, false);

        unregisterReceiver(esmMonitor);

        //Stop AWARE instance in plugin
        if (esmSrv != null) this.stopService(esmSrv);
        Aware.stopAWARE(this);
    }
}
