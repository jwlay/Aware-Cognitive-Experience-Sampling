package com.aware.plugin.cognitive_esm;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aware.Aware;

import java.io.IOException;
import java.util.ArrayList;

public class Settings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    //Plugin settings in XML @xml/preferences
    public static final String STATUS_PLUGIN_COGNITIVE_EXPERIENCE = "status_plugin_cognitive_esm";
    public static final String TEST_NAMES = "test_names";

    //Plugin settings UI elements
    private static CheckBoxPreference status;
    private static EditTextPreference testNames;

    public static String[] getTests() {
        if (testNames == null) return null;
        return testNames.getText().split(",");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFragmentManager().executePendingTransactions();

        status = (CheckBoxPreference) findPreference(STATUS_PLUGIN_COGNITIVE_EXPERIENCE);
        testNames = (EditTextPreference) findPreference(TEST_NAMES);
        if( Aware.getSetting(this, STATUS_PLUGIN_COGNITIVE_EXPERIENCE).length() == 0 ) {
            Aware.setSetting( this, STATUS_PLUGIN_COGNITIVE_EXPERIENCE, true ); //by default, the setting is true on install
        }
        status.setChecked(Aware.getSetting(getApplicationContext(), STATUS_PLUGIN_COGNITIVE_EXPERIENCE).equals("true"));
        testNames.setText(Aware.getSetting(getApplicationContext(), TEST_NAMES));
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference setting = findPreference(key);
        if( setting.getKey().equals(STATUS_PLUGIN_COGNITIVE_EXPERIENCE) ) {
            Aware.setSetting(getApplicationContext(), key, sharedPreferences.getBoolean(key, false));
            status.setChecked(sharedPreferences.getBoolean(key, false));
        }
        if ( setting.getKey().equals(TEST_NAMES) ) {
            Aware.setSetting(getApplicationContext(), key, sharedPreferences.getString(key, null));
            testNames.setText(Aware.getSetting(getApplicationContext(), TEST_NAMES));
        }
        if (Aware.getSetting(this, STATUS_PLUGIN_COGNITIVE_EXPERIENCE).equals("true")) {
            Aware.startPlugin(getApplicationContext(), "com.aware.plugin.cognitive_esm");
        } else {
            Aware.stopPlugin(getApplicationContext(), "com.aware.plugin.cognitive_esm");
        }
    }
}
