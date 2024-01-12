package io.cnaik.service;

import hudson.model.Run;
import io.cnaik.GoogleChatNotification;
import io.cnaik.Messages;
import jenkins.plugins.googlechat.GoogleChatRequest;
import jenkins.plugins.googlechat.GoogleChatService;
import jenkins.plugins.googlechat.StandardGoogleChatService;
import jenkins.plugins.googlechat.decisions.Context;
import jenkins.plugins.googlechat.decisions.NotificationConditions;

public class CommonUtil {

    private GoogleChatNotification googleChatNotification;
    private Run build;
    private LogUtil logUtil;
    private ResponseMessageUtil responseMessageUtil;
    private final GoogleChatService googleChatService;

    public CommonUtil(GoogleChatNotification googleChatNotification) {
        this.googleChatNotification = googleChatNotification;
        this.build = googleChatNotification.getBuild();
        this.logUtil = googleChatNotification.getLogUtil();
        this.responseMessageUtil = googleChatNotification.getResponseMessageUtil();
        this.googleChatService = new StandardGoogleChatService();
    }

    public void send() {
        boolean sendNotificationFlag = checkWhetherToSend();
        logUtil.printLog(Messages.sendGoogleChatNotificationCondition(sendNotificationFlag));

        if (sendNotificationFlag) {
            var request = createGoogleChatRequest();
            logUtil.printLog("Request body:\n%s", Messages.finalFormattedText(request.getBody()));

            String[] urlDetails = googleChatNotification.getUrl().split(",");
            var success = googleChatService.publish(request, urlDetails);
            if (!success) {
                logUtil.printLog(Messages.operationMayHaveFailed());
            }
        }
    }

    private GoogleChatRequest createGoogleChatRequest() {
        GoogleChatRequest request;
        if (googleChatNotification.isCardMessageFormat()) {
            request = responseMessageUtil.createCardMessage().orElse(null);
        } else {
            request = responseMessageUtil.createTextMessage();
        }
        return request;
    }

    private boolean checkWhetherToSend() {
        if (build == null || build.getResult() == null || googleChatNotification == null) {
            return false;
        } else {
            var conditions = NotificationConditions.create(googleChatNotification, logUtil.getLogger());
            return conditions.noneConditionsUserPreferencesMatches() // all the notify* options are disabled, so will activate all of them
                    || conditions.test(new Context(build, build.getPreviousBuild()));
        }
    }
}
