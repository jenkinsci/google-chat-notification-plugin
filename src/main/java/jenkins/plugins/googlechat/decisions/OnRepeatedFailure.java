package jenkins.plugins.googlechat.decisions;

import hudson.model.Result;
import io.cnaik.GoogleChatNotification;
import jenkins.plugins.googlechat.logging.BuildAwareLogger;

public class OnRepeatedFailure implements Condition {

    private final GoogleChatNotification preferences;
    private final BuildAwareLogger log;

    public OnRepeatedFailure(GoogleChatNotification preferences, BuildAwareLogger log) {
        this.preferences = preferences;
        this.log = log;
    }

    @Override
    public boolean isMetBy(Context context) {
        return context.currentResult() == Result.FAILURE // notify only on repeated failures
                && context.previousResultOrSuccess() == Result.FAILURE;
    }

    @Override
    public boolean userPreferenceMatches() {
        return preferences.isNotifyRepeatedFailure();
    }

    @Override
    public BuildAwareLogger log() {
        return log;
    }
}
