package com.aware.plugin.cognitive_esm.definition;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Jan Wohlfahrt-Laymann on 2017-05-14.
 */

@Root(name = "task", strict = false)
public class Task {

    @Element(name = "Question", required = false)
    private String Question;

    @Element(name = "score", required = false)
    private int score;

    @ElementList(name = "Aware", inline = true)
    private List<Aware> aware;

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<Aware> getAware() {
        return aware;
    }

    public void setAware(List<Aware> aware) {
        this.aware = aware;
    }
}
