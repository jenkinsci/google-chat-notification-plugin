package io.cnaik.model.google;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.Arrays;

public final class Sections implements Serializable {

    private static final long serialVersionUID = 3030511392102078232L;
    
    private final Widgets[] widgets;

    public Sections(Widgets[] widgets) {
        this.widgets = SerializationUtils.clone(widgets);
    }

    public Widgets[] getWidgets() {
        return SerializationUtils.clone(this.widgets);
    }

    @Override
    public String toString() {
        return "Sections{" +
                "widgets=" + Arrays.toString(widgets) +
                '}';
    }
}
