package com.aware.plugin.cognitive_esm;

import com.aware.plugin.cognitive_esm.definition.TestDefinition;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * Created by Jan Wohlfahrt-Laymann on 2017-05-17.
 */

public class TestDeserializer {


    public static TestDefinition deserializeXml(String Xml) throws Exception {
        Serializer serializer = new Persister();
        return serializer.read(TestDefinition.class, Xml);
    }
}
