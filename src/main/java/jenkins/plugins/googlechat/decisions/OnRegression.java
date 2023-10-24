package jenkins.plugins.googlechat.decisions;

import java.util.HashSet;
import java.util.Set;

import hudson.tasks.junit.TestResultAction;
import hudson.tasks.test.TestResult;
import io.cnaik.GoogleChatNotification;
import jenkins.plugins.googlechat.logging.BuildAwareLogger;

public class OnRegression implements Condition {

    private final GoogleChatNotification preferences;
    private final BuildAwareLogger log;

    public OnRegression(GoogleChatNotification preferences, BuildAwareLogger log) {
        this.preferences = preferences;
        this.log = log;
    }

    @Override
    public boolean isMetBy(Context context) {
        return context.currentResultOrSuccess().isWorseThan(context.previousResultOrSuccess())
                || moreTestFailuresThanPrevious(context);
    }

    @Override
    public boolean userPreferenceMatches() {
        return preferences.isNotifyRegression();
    }

    @Override
    public BuildAwareLogger log() {
        return log;
    }

    private boolean moreTestFailuresThanPrevious(Context context) {
        var currentTestResult = context.getCurrentTestResult();
        var previousTestResult = context.getPreviousTestResult();

        if (currentTestResult != null && previousTestResult != null) {
            if (currentTestResult.getFailCount() > previousTestResult.getFailCount())
                return true;

            // test if different tests failed.
            return !getFailedTestIds(currentTestResult).equals(getFailedTestIds(previousTestResult));
        }

        return false;
    }

    private Set<String> getFailedTestIds(TestResultAction testResultAction) {
        Set<String> failedTestIds = new HashSet<>();
        var failedTests = testResultAction.getFailedTests();

        for (TestResult result : failedTests) {
            failedTestIds.add(result.getId());
        }

        return failedTestIds;
    }
}
