package com.aware.plugin.cognitive_esm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;

import com.aware.Applications;
import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.ESM;
import com.aware.ui.ESM_Queue;
import com.aware.ui.esms.ESMFactory;
import com.aware.utils.Aware_Plugin;

import java.util.ArrayList;

import static com.aware.ESM.EXTRA_ESM;

public class Plugin extends Aware_Plugin {

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

        //REQUIRED_PERMISSIONS.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        //To sync data to the server, you'll need to set this variables from your ContentProvider
        DATABASE_TABLES = Provider.DATABASE_TABLES;
        TABLES_FIELDS = Provider.TABLES_FIELDS;
        CONTEXT_URIS = new Uri[]{ Provider.TableOne_Data.CONTENT_URI }; //this syncs dummy TableOne_Data to server
    }

    //This function gets called every 5 minutes by AWARE to make sure this plugin is still running.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (PERMISSIONS_OK) {

            DEBUG = Aware.getSetting(this, Aware_Preferences.DEBUG_FLAG).equals("true");

            //Initialize our plugin's settings
            Aware.setSetting(this, Settings.STATUS_PLUGIN_TEMPLATE, true);

            Aware.setSetting(this, Aware_Preferences.STATUS_ESM, true);

            TestExecuter executer = new TestExecuter();

            ESMFactory esmFactory = new ESMFactory();

            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            long startTime = System.currentTimeMillis();
            /*while ((Settings.getTests() == null) && (System.currentTimeMillis()-startTime)<10000) {
                Log.
            }*/
            if (Settings.getTests() != null)
                for (String test : Settings.getTests()) {
                    test.replaceAll(" ","");
                    if (test.length() != 0) {
                        Log.v(TAG, "Creating test: "+test);
                        executer.createTest(this, esmFactory, test);
                    }
                }
            else Log.i(TAG, "No tests found");

            //Initialise AWARE instance in plugin
            Aware.startAWARE(this);
        } else Log.v(TAG, "Permission not ok!");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Aware.setSetting(this, Settings.STATUS_PLUGIN_TEMPLATE, false);

        //Stop AWARE instance in plugin
        Aware.stopAWARE(this);
    }
}
