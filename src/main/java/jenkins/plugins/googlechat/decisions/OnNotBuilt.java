package jenkins.plugins.googlechat.decisions;

import hudson.model.Result;
import io.cnaik.GoogleChatNotification;
import jenkins.plugins.googlechat.logging.BuildAwareLogger;

public class OnNotBuilt implements Condition {

    private final GoogleChatNotification preferences;
    private final BuildAwareLogger log;

    public OnNotBuilt(GoogleChatNotification preferences, BuildAwareLogger log) {
        this.preferences = preferences;
        this.log = log;
    }

    @Override
    public boolean isMetBy(Context context) {
        return context.currentResult() == Result.NOT_BUILT;
    }

    @Override
    public boolean userPreferenceMatches() {
        return preferences.isNotifyNotBuilt();
    }

    @Override
    public BuildAwareLogger log() {
        return log;
    }
}
