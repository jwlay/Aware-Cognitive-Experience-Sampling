package com.aware.plugin.cognitive_esm.definition;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Jan Wohlfahrt-Laymann on 2017-05-14.
 */

@Root(name= "Component", strict = false)
public class Component {

    @Element(name = "name", required = false)
    private String name;

    @Element(name = "score", required = false)
    private int score;

    @ElementList(name = "task", inline = true)
    private List<Task> tasks;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
