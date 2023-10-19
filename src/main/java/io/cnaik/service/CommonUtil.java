package io.cnaik.service;

import hudson.model.Result;
import hudson.model.Run;
import io.cnaik.GoogleChatNotification;
import jenkins.plugins.googlechat.GoogleChatRequest;
import jenkins.plugins.googlechat.GoogleChatService;
import jenkins.plugins.googlechat.StandardGoogleChatService;

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
        boolean sendNotificationFlag = checkPipelineFlag();

        logUtil.printLog("Send Google Chat Notification condition is : " + sendNotificationFlag);

        if (!sendNotificationFlag) {
            return;
        }

        GoogleChatRequest request;

        if (googleChatNotification.isCardMessageFormat()) {
            request = responseMessageUtil.createCardMessage().orElse(null);
        } else {
            request = responseMessageUtil.createTextMessage();
        }

        logUtil.printLog("Final formatted text: " + request.getBody());

        String[] urlDetails = googleChatNotification.getUrl().split(",");

        var success = googleChatService.publish(request, urlDetails);
        if (!success) {
            logUtil.printLog("Operation may have failed. Please check system log for details.");
        }
    }

    private boolean checkWhetherToSend() {

        boolean result = false;

        if (build == null || build.getResult() == null || googleChatNotification == null) {
            return result;
        }

        var prevRun = build.getPreviousBuild();
        Result previousResult = (prevRun != null) ? prevRun.getResult() : Result.SUCCESS;

        if (googleChatNotification.isNotifyAborted()
                && Result.ABORTED == build.getResult()) {

            result = true;

        } else if (googleChatNotification.isNotifyFailure()
                && Result.FAILURE == build.getResult()) {

            result = true;

        } else if (googleChatNotification.isNotifyNotBuilt()
                && Result.NOT_BUILT == build.getResult()) {

            result = true;

        } else if (googleChatNotification.isNotifySuccess()
                && Result.SUCCESS == build.getResult()) {

            result = true;

        } else if (googleChatNotification.isNotifyUnstable()
                && Result.UNSTABLE == build.getResult()) {

            result = true;

        } else if (googleChatNotification.isNotifyBackToNormal() && Result.SUCCESS == build.getResult()
                && (Result.ABORTED == previousResult
                        || Result.FAILURE == previousResult
                        || Result.UNSTABLE == previousResult
                        || Result.NOT_BUILT == previousResult)) {

            result = true;

        }

        return result;
    }

    private boolean checkPipelineFlag() {

        if (googleChatNotification != null &&
                !googleChatNotification.isNotifyAborted() &&
                !googleChatNotification.isNotifyBackToNormal() &&
                !googleChatNotification.isNotifyFailure() &&
                !googleChatNotification.isNotifyNotBuilt() &&
                !googleChatNotification.isNotifySuccess() &&
                !googleChatNotification.isNotifyUnstable()) {
            return true;
        }
        return checkWhetherToSend();
    }
}
