package com.aware.plugin.cognitive_esm.definition;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Jan Wohlfahrt-Laymann on 2017-06-04.
 */

@Root(name = "Shape", strict = false)
public class Shape {

    @Element(name = "type")
    private String type;

    @Element(name = "xPos")
    private int xPos;

    @Element(name = "yPos")
    private int yPos;

    @Element(name = "radius")
    private int radius;

    @Element(name = "color")
    private String color;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
