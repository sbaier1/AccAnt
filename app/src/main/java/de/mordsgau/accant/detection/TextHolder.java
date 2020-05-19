package de.mordsgau.accant.detection;

import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

public class TextHolder {
    private final Rect boundingBox;

    private final List<String> text;

    private int componentIndex;

    public TextHolder(Rect boundingBox, List<String> text) {
        this.boundingBox = boundingBox;
        this.text = text;
        this.componentIndex = 0;
    }

    public TextHolder(Rect boundingBox, String text) {
        this.boundingBox = boundingBox;
        this.componentIndex = 0;
        final ArrayList<String> list = new ArrayList<>();
        list.add(text);
        this.text = list;
    }

    public Rect getBoundingBox() {
        return boundingBox;
    }

    public List<String> getComponents() {
        return text;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (String s : text) {
            builder.append(s);
            builder.append("\n");
        }
        return builder.toString();
    }

    public int getComponentIndex() {
        return componentIndex;
    }

    public void setComponentIndex(int componentIndex) {
        this.componentIndex = componentIndex;
    }
}
