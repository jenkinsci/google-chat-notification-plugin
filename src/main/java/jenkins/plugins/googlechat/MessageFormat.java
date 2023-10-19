package jenkins.plugins.googlechat;

public enum MessageFormat {

    SIMPLE("simple text message"),
    CARD("card message");

    private final String displayName;

    MessageFormat(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public static MessageFormat forDisplayName(String displayName) {
        for (MessageFormat messageFormat : values()) {
            if (messageFormat.getDisplayName().equals(displayName)) {
                return messageFormat;
            }
        }
        return null;
    }
}
