package io.cnaik.model.google;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

public class Widgets implements Serializable {

    private static final long serialVersionUID = 7793136321179413465L;
    
    private TextParagraph textParagraph;

    public Widgets(TextParagraph textParagraph) {
        this.textParagraph = SerializationUtils.clone(textParagraph);
    }

    public TextParagraph getTextParagraph() {
        return SerializationUtils.clone(this.textParagraph);
    }

    @Override
    public String toString() {
        return "Widgets{" +
                "textParagraph=" + textParagraph +
                '}';
    }
}
