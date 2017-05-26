package com.aware.plugin.cognitive_esm;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.aware.Aware;

import java.io.IOException;
import java.util.ArrayList;

public class Settings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    //Plugin settings in XML @xml/preferences
    public static final String STATUS_PLUGIN_TEMPLATE = "status_plugin_template";
    public static final String COGNITIVE_TESTS = "cognitive_tests";

    //Plugin settings UI elements
    private static CheckBoxPreference status;
    private static ListPreference Ctests;

    //Cognitive Tests
    private static ArrayList<String> tests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        tests = new ArrayList<>();
        try {
            for (String item : getApplicationContext().getAssets().list("")) {
                tests.add(item);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Ctests = (ListPreference) findPreference(COGNITIVE_TESTS);
        Ctests.setEntries(tests.toArray(new CharSequence[tests.size()]));
    }

    @Override
    protected void onResume() {
        super.onResume();

        status = (CheckBoxPreference) findPreference(STATUS_PLUGIN_TEMPLATE);
        Ctests = (ListPreference) findPreference(COGNITIVE_TESTS);
        if( Aware.getSetting(this, STATUS_PLUGIN_TEMPLATE).length() == 0 ) {
            Aware.setSetting( this, STATUS_PLUGIN_TEMPLATE, true ); //by default, the setting is true on install
        }
        if (Aware.getSetting(this, COGNITIVE_TESTS).length() == 0) {
            Aware.setSetting(this, COGNITIVE_TESTS, true);
        }
        Ctests.setEntries(tests.toArray(new CharSequence[tests.size()]));
        status.setChecked(Aware.getSetting(getApplicationContext(), STATUS_PLUGIN_TEMPLATE).equals("true"));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference setting = findPreference(key);
        if( setting.getKey().equals(STATUS_PLUGIN_TEMPLATE) ) {
            Aware.setSetting(this, key, sharedPreferences.getBoolean(key, false));
            status.setChecked(sharedPreferences.getBoolean(key, false));
        }
        if (Aware.getSetting(this, STATUS_PLUGIN_TEMPLATE).equals("true")) {
            Aware.startPlugin(getApplicationContext(), "com.aware.plugin.cognitive_esm");
        } else {
            Aware.stopPlugin(getApplicationContext(), "com.aware.plugin.cognitive_esm");
        }
    }

    public static ListPreference getTests() {
        return Ctests;
    }
}
