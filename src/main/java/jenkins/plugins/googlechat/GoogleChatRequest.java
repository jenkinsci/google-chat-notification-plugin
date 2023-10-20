package jenkins.plugins.googlechat;

import org.json.JSONObject;
import org.springframework.web.util.UriUtils;

import edu.umd.cs.findbugs.annotations.Nullable;

public class GoogleChatRequest {

    private static final String DEFAULT_ENCODING = "UTF-8";

    private final String message;
    private final String thread;
    private final JSONObject cardConfig;

    private GoogleChatRequest(@Nullable String message, @Nullable String thread, @Nullable JSONObject cardConfig) {
        if (message != null && cardConfig != null) {
            throw new IllegalArgumentException("Invalid arguments: can't create a request with both a text message and a card message");
        } else if (message == null && cardConfig == null) {
            throw new IllegalArgumentException("Invalid arguments: must create a request with either a text message or a card message");
        }

        this.message = message;
        this.thread = thread;
        this.cardConfig = cardConfig;
    }

    public static GoogleChatRequestSimpleBuilder newSimpleRequest() {
        return new GoogleChatRequestSimpleBuilder();
    }

    public static GoogleChatRequestCardBuilder newCardRequest() {
        return new GoogleChatRequestCardBuilder();
    }

    public JSONObject getBody() {
        JSONObject body = cardConfig != null ? cardConfig : new JSONObject();

        if (message != null) {
            body.put("text", message);
        }

        if (thread != null) {
            body.put("thread", createThreadJson(thread));
        }

        return body;
    }

    private JSONObject createThreadJson(String threadKey) {
        var thread = new JSONObject();
        thread.put("threadKey", UriUtils.encodePath(threadKey, DEFAULT_ENCODING));
        return thread;
    }

    public String getThread() {
        return thread;
    }

    public static class GoogleChatRequestSimpleBuilder {

        private String message;
        private String thread;

        private GoogleChatRequestSimpleBuilder() {
        }

        public GoogleChatRequestSimpleBuilder withMessage(String message) {
            this.message = message;
            return this;
        }

        public GoogleChatRequestSimpleBuilder withThread(String thread) {
            this.thread = thread;
            return this;
        }

        public GoogleChatRequest build() {
            return new GoogleChatRequest(message, thread, null);
        }
    }

    public static class GoogleChatRequestCardBuilder {

        private JSONObject cardConfig;
        private String thread;

        private GoogleChatRequestCardBuilder() {
        }

        public GoogleChatRequestCardBuilder withCardConfig(JSONObject cardConfig) {
            this.cardConfig = cardConfig;
            this.thread = getThreadKey(cardConfig);
            return this;
        }

        private String getThreadKey(JSONObject cardConfig) {
            if (cardConfig.has("thread")) {
                var thread = cardConfig.getJSONObject("thread");
                return thread.has("threadKey") ? thread.getString("threadKey") : null;
            } else {
                return null;
            }
        }

        public GoogleChatRequestCardBuilder withThread(String thread) {
            this.thread = thread;
            return this;
        }

        public GoogleChatRequest build() {
            return new GoogleChatRequest(null, thread, cardConfig);
        }
    }
}
