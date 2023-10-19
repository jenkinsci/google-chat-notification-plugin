package jenkins.plugins.googlechat.logging;

import hudson.model.TaskListener;

public interface BuildAwareLogger {
    
    void debug(String key, String message, Object... args);

    void info(String key, String message, Object... args);

    TaskListener getTaskListener();
}
