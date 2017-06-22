package com.aware.plugin.cognitive_esm.definition;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Jan Wohlfahrt-Laymann on 2017-06-04.
 */

@Root(name= "CommandInstructions", strict = false)
public class SpecialInstructions {

    @Element(name = "Text")
    private String text;

    @ElementList(name = "Shapes", inline = false)
    private List<Shape> shapeList;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Shape> getShapeList() {
        return shapeList;
    }

    public void setShapeList(List<Shape> shapeList) {
        this.shapeList = shapeList;
    }
}
