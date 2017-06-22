package com.aware.plugin.cognitive_esm.definition;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jan Wohlfahrt-Laymann on 2017-05-22.
 */

@Root(name="Schedule", strict = false)
public class Schedule {

    @Element(name="id")
    private String id;

    @ElementList(entry="component", required = false, inline = true)
    private List<String> components = new ArrayList<String>();

    @ElementList(entry="hour", required = false, inline = true)
    private List<Integer> hour = new ArrayList<Integer>();

    @ElementList(entry="minute", required = false, inline = true)
    private List<Integer> minute = new ArrayList<Integer>();

    @ElementList(entry="weekday", required = false, inline = true)
    private List<String> weekday = new ArrayList<String>();

    @ElementList(entry="month", required = false, inline = true)
    private List<String> month = new ArrayList<String>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getComponents() {
        return components;
    }

    public void setComponents(List<String> components) {
        this.components = components;
    }

    public List<Integer> getHour() {
        return hour;
    }

    public void setHour(List<Integer> hour) {
        this.hour = hour;
    }

    public List<Integer> getMinute() {
        return minute;
    }

    public void setMinute(List<Integer> minute) {
        this.minute = minute;
    }

    public List<String> getWeekday() {
        return weekday;
    }

    public void setWeekday(List<String> weekday) {
        this.weekday = weekday;
    }

    public List<String> getMonth() {
        return month;
    }

    public void setMonth(List<String> month) {
        this.month = month;
    }
}
