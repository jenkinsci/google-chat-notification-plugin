package io.cnaik.model.google;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.Arrays;

public final class Cards implements Serializable {

    private static final long serialVersionUID = -1653005285015984998L;
    
    private final Sections[] sections;
    private final Header header;

    public Cards(Sections[] sections, Header header) {
        this.sections = SerializationUtils.clone(sections);
        this.header = SerializationUtils.clone(header);
    }

    public Sections[] getSections() {
        return SerializationUtils.clone(sections);
    }

    public Header getHeader() {
        return SerializationUtils.clone(header);
    }

    @Override
    public String toString() {
        return "Cards{" +
                "sections=" + Arrays.toString(sections) +
                ", header=" + header +
                '}';
    }
}
