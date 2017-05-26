package com.aware.plugin.cognitive_esm.definition;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Jan Wohlfahrt-Laymann on 2017-05-14.
 */

@Root
public class TestDefinition {

    @Attribute(name = "noNamespaceSchemaLocation")
    private String schema;

    @Element(name = "name")
    private String name;

    @Element(name = "short_name")
    private String short_name;

    @Element(name = "text2speech", required = false)
    private boolean text2speech;

    @ElementList(name = "Component", inline = true)
    private List<Component> components;

    @ElementList(name = "Schedule", inline = true)
    private List<Schedule> schedules;

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public boolean isText2speech() {
        return text2speech;
    }

    public void setText2speech(boolean text2speech) {
        this.text2speech = text2speech;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }
}
