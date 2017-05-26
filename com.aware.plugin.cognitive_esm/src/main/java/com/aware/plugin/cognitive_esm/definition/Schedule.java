package com.aware.plugin.cognitive_esm.definition;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Jan Wohlfahrt-Laymann on 2017-05-22.
 */

@Root(name="Schedule", strict = false)
public class Schedule {

    @Element(name="id")
    private String id;

    //initialize hour and minute to 999 because not a valid hour & number (allow checking for initialization)
    @Element(name="hour", required = false)
    private int hour = 999;

    @Element(name="minute", required = false)
    private int minute = 999;

    @Element(name="weekday", required = false)
    private String weekday;

    @Element(name="month", required = false)
    private String month;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
