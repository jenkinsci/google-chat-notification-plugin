package jenkins.plugins.googlechat.decisions;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import io.cnaik.GoogleChatNotification;
import jenkins.plugins.googlechat.logging.BuildAwareLogger;

public class NotificationConditions implements Predicate<Context> {

    private final List<Condition> conditions;

    public NotificationConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public static NotificationConditions create(GoogleChatNotification preferences, BuildAwareLogger log) {
        return new NotificationConditions(Arrays.asList(
                new OnAborted(preferences, log),
                new OnSingleFailure(preferences, log),
                new OnRepeatedFailure(preferences, log),
                new OnEveryFailure(preferences, log),
                new OnRegression(preferences, log),
                new OnNotBuilt(preferences, log),
                new OnBackToNormal(preferences, log),
                new OnSuccess(preferences, log),
                new OnUnstable(preferences, log)));
    }

    public boolean noneConditionsUserPreferencesMatches() {
        return conditions.stream().noneMatch(p -> p.userPreferenceMatches());
    }

    @Override
    public boolean test(Context context) {
        return conditions.stream().anyMatch(p -> p.test(context));
    }
}
