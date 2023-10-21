package jenkins.plugins.googlechat.decisions;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import jenkins.plugins.googlechat.logging.BuildAwareLogger;

public class NotificationConditionsTest {

    @Test
    public void shouldMatchIfAnyConditionMatches() {
        NotificationConditions conditions = new NotificationConditions(Arrays.asList(
                new ConditionStub(false),
                new ConditionStub(false),
                new ConditionStub(true)));

        boolean actual = conditions.test(null);

        assertTrue(actual);
    }

    static class ConditionStub implements Condition {

        private boolean result;

        ConditionStub(boolean result) {
            this.result = result;
        }

        @Override
        public boolean test(Context context) {
            return result;
        }

        @Override
        public boolean isMetBy(Context context) {
            return result;
        }

        @Override
        public boolean userPreferenceMatches() {
            return result;
        }

        @Override
        public BuildAwareLogger log() {
            return null;
        }
    }
}
