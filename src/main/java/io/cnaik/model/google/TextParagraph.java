package io.cnaik.model.google;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

public class TextParagraph implements Serializable {

    private static final long serialVersionUID = -309638084164843267L;
    
    private String text;

    public TextParagraph(String text) {
        this.text = SerializationUtils.clone(text);
    }

    public String getText() {
        return SerializationUtils.clone(this.text);
    }

    @Override
    public String toString() {
        return "TextParagraph{" +
                "text='" + text + '\'' +
                '}';
    }
}
