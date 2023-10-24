package jenkins.plugins.googlechat.decisions;

import hudson.model.Result;
import io.cnaik.GoogleChatNotification;
import jenkins.plugins.googlechat.logging.BuildAwareLogger;

public class OnEveryFailure implements Condition {

    private final GoogleChatNotification preferences;
    private final BuildAwareLogger log;

    public OnEveryFailure(GoogleChatNotification preferences, BuildAwareLogger log) {
        this.preferences = preferences;
        this.log = log;
    }

    @Override
    public boolean isMetBy(Context context) {
        return context.currentResult() == Result.FAILURE;
    }

    @Override
    public boolean userPreferenceMatches() {
        return preferences.isNotifyFailure();
    }

    @Override
    public BuildAwareLogger log() {
        return log;
    }
}
