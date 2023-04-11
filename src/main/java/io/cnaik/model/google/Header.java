package io.cnaik.model.google;

import java.io.Serializable;

public final class Header implements Serializable {

    private static final long serialVersionUID = 2736041656260337631L;
    
    private final String title;
    private final String subtitle;
    private final String imageUrl;

    public Header(String title, String subtitle, String imageUrl) {
        this.title = title;
        this.subtitle = subtitle;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String toString() {
        return "Header{" +
                "title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
