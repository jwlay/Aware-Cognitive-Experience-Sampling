package com.aware.plugin.cognitive_esm.definition;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Jan Wohlfahrt-Laymann on 2017-05-23.
 */

@Root(name="ImageInstructions", strict = false)
public class Instructions {

    @Element(name="Text")
    private String text;

    @Element(name="ImageUrl", required = false)
    private String ImageUrl;

    @Element(name="encodedImage", required = false)
    private String encodedImage;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getEncodedImage() {
        return encodedImage;
    }

    public void setEncodedImage(String encodedImage) {
        this.encodedImage = encodedImage;
    }
}
