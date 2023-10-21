package jenkins.plugins.googlechat.decisions;

import hudson.model.Result;
import io.cnaik.GoogleChatNotification;
import jenkins.plugins.googlechat.logging.BuildAwareLogger;

public class OnSingleFailure implements Condition {

    private final GoogleChatNotification preferences;
    private final BuildAwareLogger log;

    public OnSingleFailure(GoogleChatNotification preferences, BuildAwareLogger log) {
        this.preferences = preferences;
        this.log = log;
    }

    @Override
    public boolean isMetBy(Context context) {
        return context.currentResult() == Result.FAILURE // notify only on single failed build
                && context.previousResultOrSuccess() != Result.FAILURE;
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
