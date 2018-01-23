package com.aware.plugin.cognitive_esm;

import android.os.AsyncTask;
import android.util.Log;

import com.aware.plugin.cognitive_esm.definition.TestDefinition;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Jan Wohlfahrt-Laymann on 2017-05-17.
 */

public class TestDeserializer {


    public static TestDefinition deserializeXml(String Xml) throws Exception {
        Log.v("TestDeserializer",Xml);
        Serializer serializer = new Persister();
        TestDefinition testDefinition = serializer.read(TestDefinition.class, Xml);
        return testDefinition;
    }

}
