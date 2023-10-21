package jenkins.plugins.googlechat.decisions;

import hudson.model.Result;
import io.cnaik.GoogleChatNotification;
import jenkins.plugins.googlechat.logging.BuildAwareLogger;

public class OnBackToNormal implements Condition {

    private final GoogleChatNotification preferences;
    private final BuildAwareLogger log;

    public OnBackToNormal(GoogleChatNotification preferences, BuildAwareLogger log) {
        this.preferences = preferences;
        this.log = log;
    }

    @Override
    public boolean isMetBy(Context context) {
        Result previousResult = context.previousResultOrSuccess();
        return context.currentResult() == Result.SUCCESS
                && (previousResult == Result.FAILURE || previousResult == Result.UNSTABLE);
    }

    @Override
    public boolean userPreferenceMatches() {
        return preferences.isNotifyBackToNormal();
    }

    @Override
    public BuildAwareLogger log() {
        return log;
    }
}
