package io.cnaik.service;

import java.util.Optional;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import hudson.FilePath;
import hudson.model.Result;
import hudson.model.Run;
import io.cnaik.GoogleChatNotification;
import io.cnaik.Messages;
import jenkins.plugins.googlechat.GoogleChatRequest;
import jenkins.plugins.googlechat.TokenExpander;

public class ResponseMessageUtil {

    private static final String JENKINS_BUILD_STATUS_ENV_VAR = "${BUILD_STATUS}";
    private static final String JENKINS_JOB_NAME_ENV_VAR = "${JOB_NAME}";

    private static final String HEX_COLOR_TEAL = "#5DBCD2";
    private static final String HEX_COLOR_RED = "#ff0000";

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
                    var requestBuilder = GoogleChatRequest.newCardRequest().withCardConfig(cardConfig);

                    if (cardConfig.has("thread")) {
                        logUtil.printLog(Messages.providedJsonMessageContainsThreadKey());
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
            logUtil.printLog(Messages.exceptionProcessingJsonMessage(exception.getMessage()));
            return Optional.empty();
        }
    }

    private String getThreadKeyForMessages() {
        String threadKey = null;
        if (googleChatNotification.isSameThreadNotification()) {
            threadKey = StringUtils.defaultIfBlank(replaceJenkinsKeywords(googleChatNotification.getThreadKey()), getJobName());
        }
        logUtil.printLog(Messages.willSendMessageToThread(threadKey));
        return threadKey;
    }

    private String getJobName() {
        return tokenExpander.expand(JENKINS_JOB_NAME_ENV_VAR, build, ws);
    }

    private String replaceJenkinsKeywords(String inputString) {
        return tokenExpander.expand(inputString, build, ws);
    }

    private String replaceBuildStatusKeywordWithColorCode(String inputString) {
        String outputString = inputString;

        if (StringUtils.isEmpty(outputString)) {
            return outputString;
        }

        if (outputString.contains(JENKINS_BUILD_STATUS_ENV_VAR)) {
            var buildStatus = build.getResult();

            if (buildStatus == Result.FAILURE) {
                outputString = outputString.replace(JENKINS_BUILD_STATUS_ENV_VAR,
                        "<font color=\"" + HEX_COLOR_RED + "\">" + JENKINS_BUILD_STATUS_ENV_VAR + "</font>");
            } else {
                outputString = outputString.replace(JENKINS_BUILD_STATUS_ENV_VAR,
                        "<font color=\"" + HEX_COLOR_TEAL + "\">" + JENKINS_BUILD_STATUS_ENV_VAR + "</font>");
            }
        } else {
            return outputString;
        }

        return outputString;
    }
}
