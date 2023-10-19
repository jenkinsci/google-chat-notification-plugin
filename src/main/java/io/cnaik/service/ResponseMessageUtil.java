package io.cnaik.service;

import java.util.Locale;
import java.util.Optional;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import hudson.FilePath;
import hudson.model.Run;
import io.cnaik.GoogleChatNotification;
import jenkins.plugins.googlechat.GoogleChatRequest;
import jenkins.plugins.googlechat.TokenExpander;

public class ResponseMessageUtil {

    private GoogleChatNotification googleChatNotification;
    private FilePath ws;
    private Run build;
    private LogUtil logUtil;
    private TokenExpander tokenExpander;

    public ResponseMessageUtil(GoogleChatNotification googleChatNotification, TokenExpander tokenExpander) {
        this.googleChatNotification = googleChatNotification;
        this.ws = googleChatNotification.getWs();
        this.build = googleChatNotification.getBuild();
        this.logUtil = googleChatNotification.getLogUtil();
        this.tokenExpander = tokenExpander;
    }

    public GoogleChatRequest createTextMessage() {
        var threadKey = getThreadKeyForMessages();
        var message = StringEscapeUtils.unescapeJava(replaceJenkinsKeywords(googleChatNotification.getMessage()));

        return GoogleChatRequest.newSimpleRequest()
                .withMessage(message)
                .withThread(threadKey)
                .build();
    }

    public Optional<GoogleChatRequest> createCardMessage() {
        String cardConfigString = replaceJenkinsKeywords(replaceBuildStatusKeywordWithColorCode(googleChatNotification.getMessage()));

        return toJson(cardConfigString)
                .map(cardConfig -> {
                    var requestBuilder = GoogleChatRequest.newCardRequest()
                            .withCardConfig(cardConfig);

                    if (cardConfig.has("thread")) {
                        logUtil.printLog(
                                "WARN: Since the provided JSON message already contains a 'thread' key, the plugin parameters 'sameThreadNotification' and 'threadKey' will be ignored");
                    } else {
                        var threadKey = getThreadKeyForMessages();
                        requestBuilder.withThread(threadKey);
                    }

                    return requestBuilder.build();
                });
    }

    private Optional<JSONObject> toJson(String message) {
        try {
            return Optional.of(new JSONObject(message));
        } catch (JSONException exception) {
            logUtil.printLog("Exception while trying to process JSON message: " + exception.getMessage());
            return Optional.empty();
        }
    }

    private String getThreadKeyForMessages() {
        String threadKey = null;
        if (googleChatNotification.isSameThreadNotification()) {
            threadKey = StringUtils.defaultIfBlank(replaceJenkinsKeywords(googleChatNotification.getThreadKey()), getJobName());
        }
        logUtil.printLog("Will send message to the thread: " + threadKey);
        return threadKey;
    }

    private String getJobName() {
        return tokenExpander.expand("${JOB_NAME}", build, ws);
    }

    private String replaceJenkinsKeywords(String inputString) {

        if (StringUtils.isEmpty(inputString)) {
            return inputString;
        }

        return tokenExpander.expand(inputString, build, ws);
    }

    private String replaceBuildStatusKeywordWithColorCode(String inputString) {

        String outputString = inputString;

        if (StringUtils.isEmpty(outputString)) {
            return outputString;
        }

        if (outputString.contains("${BUILD_STATUS}")) {

            String buildStatus = tokenExpander.expand("${BUILD_STATUS}", build, ws);

            if (StringUtils.isNotEmpty(buildStatus)
                    && buildStatus.toUpperCase(Locale.ENGLISH).contains("FAIL")) {
                outputString = outputString.replace("${BUILD_STATUS}", "<font color=\"#ff0000\">${BUILD_STATUS}</font>");
            } else {
                outputString = outputString.replace("${BUILD_STATUS}", "<font color=\"#5DBCD2\">${BUILD_STATUS}</font>");
            }
        } else {
            return outputString;
        }
        return outputString;
    }
}
