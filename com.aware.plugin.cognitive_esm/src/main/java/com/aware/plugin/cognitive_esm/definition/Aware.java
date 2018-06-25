package com.aware.plugin.cognitive_esm.definition;

import org.json.JSONArray;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jan Wohlfahrt-Laymann on 2017-05-14.
 */

@Root(name = "Aware", strict = false)
public class Aware {

    @Element(name = "ESM_Type")
    private String ESM_Type;

    @Element(name = "Command", required = false)
    private String Command;

    @Element(name = "Title")
    private String Title;

    @Element(name= "text2speech", required = false)
    private Boolean Text2Speech;

    private String Instructions;

    @ElementList(entry = "Instructions", required = false, inline = true)
    private List<String> randomInstructions = new ArrayList<String>();

    private Instructions ImageInstructions;

    @ElementList(entry = "ImageInstructions", required = false, inline = true)
    private List<Instructions> RandomImageInstructions = new ArrayList<Instructions>();

    @Element(name = "CommandInstructions", required = false)
    private SpecialInstructions CommandInstructions;

    @ElementList(name= "Options", inline = false, required = false)
    private List<String> options;

    @Element(name = "Solution", required = false)
    private String Solution;

    public String getESM_Type() {
        return ESM_Type;
    }

    public void setESM_Type(String ESM_Type) {
        this.ESM_Type = ESM_Type;
    }

    public void setCommand(String command) {
        this.Command = command;
    }

    public String getCommand() {
        return Command;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public Boolean getText2Speech() {
        return Text2Speech;
    }

    public void setText2Speech(Boolean text2Speech) {
        Text2Speech = text2Speech;
    }

    public String getInstructions() {
        return Instructions;
    }

    public void setInstructions(String instructions) {
        Instructions = instructions;
    }

    public List<String> getRandomInstructions() { return randomInstructions; }

    public void setRandomInstructions(List<String> randomInstructions) { this.randomInstructions = randomInstructions; }

    public com.aware.plugin.cognitive_esm.definition.Instructions getImageInstructions() {
        return ImageInstructions;
    }

    public void setImageInstructions(com.aware.plugin.cognitive_esm.definition.Instructions imageInstructions) {
        ImageInstructions = imageInstructions;
    }

    public List<com.aware.plugin.cognitive_esm.definition.Instructions> getRandomImageInstructions() {
        return RandomImageInstructions;
    }

    public void setRandomImageInstructions(List<com.aware.plugin.cognitive_esm.definition.Instructions> randomImageInstructions) {
        RandomImageInstructions = randomImageInstructions;
    }

    public void setCommandInstructions(SpecialInstructions commandInstructions) {
        CommandInstructions = commandInstructions;
    }

    public SpecialInstructions getCommandInstructions() {
        return CommandInstructions;
    }

    public String getSolution() {
        return Solution;
    }

    public void setSolution(String solution) {
        Solution = solution;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public List<String> getOptions() {
        return options;
    }

    public JSONArray getOptionsAsJSON() {
        JSONArray json = new JSONArray();
        for (String op : options)
            json.put(op);
        return json;
    }
}
