package io.cnaik.service;

import java.util.logging.Logger;

import io.cnaik.GoogleChatNotification;
import jenkins.plugins.googlechat.logging.BuildKey;
import jenkins.plugins.googlechat.logging.GoogleChatNotificationsLogger;

/**
 * @deprecated Use {@link GoogleChatNotificationsLogger}
 */
@Deprecated
public class LogUtil {

    private GoogleChatNotification googleChatNotification;
    private GoogleChatNotificationsLogger logger;

    public LogUtil(GoogleChatNotification googleChatNotification) {
        this.googleChatNotification = googleChatNotification;
        this.logger = new GoogleChatNotificationsLogger(Logger.getLogger(GoogleChatNotification.class.getName()),
                googleChatNotification.getTaskListener(),
                googleChatNotification.isSuppressInfoLoggers());
    }

    public void printLog(String message) {
        var key = BuildKey.format(googleChatNotification.getBuild());
        logger.info(key, message);
    }
}
