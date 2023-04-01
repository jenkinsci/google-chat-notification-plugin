package io.cnaik.service;

import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.cnaik.GoogleChatNotification;
import io.cnaik.model.google.*;
import org.apache.commons.lang3.StringUtils;
import org.jenkinsci.plugins.tokenmacro.TokenMacro;
import org.json.JSONObject;

public class ResponseMessageUtil {

    private GoogleChatNotification googleChatNotification;
    private TaskListener taskListener;
    private FilePath ws;
    private Run build;
    private LogUtil logUtil;

    public ResponseMessageUtil(GoogleChatNotification googleChatNotification) {
        this.googleChatNotification = googleChatNotification;
        this.taskListener = googleChatNotification.getTaskListener();
        this.ws = googleChatNotification.getWs();
        this.build = googleChatNotification.getBuild();
        this.logUtil = googleChatNotification.getLogUtil();
    }

    public String createTextMessage() {
        return escapeSpecialCharacter(replaceJenkinsKeywords(googleChatNotification.getMessage()));
    }

    public String createCardMessage() {
        return replaceJenkinsKeywords(replaceBuildStatusKeywordWithColorCode(googleChatNotification.getMessage()));
    }

    private String escapeSpecialCharacter(String input) {

        String output = input;

        if(StringUtils.isNotEmpty(output)) {
            output = output.replace("{", "\\{");
            output = output.replace("}", "\\}");
            output = output.replace("'", "\\'");
        }

        return output;
    }

    private String replaceJenkinsKeywords(String inputString) {

        if(StringUtils.isEmpty(inputString)) {
            return inputString;
        }

        try {

            return TokenMacro.expandAll(build, ws, taskListener, inputString, false, null);

        } catch (Exception e) {
            logUtil.printLog("Exception in Token Macro expansion: " + e);
        }
        return inputString;
    }

    private String replaceBuildStatusKeywordWithColorCode(String inputString) {

        String outputString = inputString;

        if(StringUtils.isEmpty(outputString)) {
            return outputString;
        }

        if(outputString.contains("${BUILD_STATUS}")) {

            try {
                String buildStatus = TokenMacro.expandAll(build, ws, taskListener, "${BUILD_STATUS}", false, null);

                if(StringUtils.isNotEmpty(buildStatus)
                        && buildStatus.toUpperCase().contains("FAIL")) {
                    outputString =  outputString.replace("${BUILD_STATUS}", "<font color=\"#ff0000\">${BUILD_STATUS}</font>");
                } else {
                    outputString =  outputString.replace("${BUILD_STATUS}", "<font color=\"#5DBCD2\">${BUILD_STATUS}</font>");
                }

            } catch (Exception e) {
                outputString = inputString;
                logUtil.printLog("Exception in Token Macro expansion: " + e);
            }
        } else {
            return outputString;
        }
        return outputString;
    }

    public TextParagraph createTextParagraph(String text) {
        return new TextParagraph(text);
    }

    public Widgets[] createWidgets(int size) {
        if(size <= 0) {
            size = 1;
        }
        return new Widgets[size];
    }

    public Widgets[] addNewWidget(Widgets[] widgets, int index, TextParagraph textParagraph) {
        Widgets widget = new Widgets(textParagraph);
        widgets[index] = widget;
        return widgets;
    }

    public Sections[] createSections(int size) {
        if(size <= 0) {
            size = 1;
        }
        return new Sections[size];
    }

    public Sections[] addNewSection(Sections[] sections, int index, Widgets[] widgets) {
        Sections section = new Sections(widgets);
        sections[index] = section;
        return sections;
    }

    public Cards[] createCards(int size) {

        if(size <= 0) {
            size = 1;
        }
        return new Cards[size];
    }

    public Cards[] addNewCard(Cards[] cards, int index, Sections[] sections, Header header) {
        Cards card = new Cards(sections, header);
        cards[index] = card;
        return cards;
    }
}
