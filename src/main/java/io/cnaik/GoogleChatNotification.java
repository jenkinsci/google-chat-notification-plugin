package io.cnaik;

import org.apache.commons.lang3.StringUtils;
import org.jenkinsci.Symbol;
import org.json.JSONException;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import io.cnaik.service.CommonUtil;
import io.cnaik.service.LogUtil;
import io.cnaik.service.ResponseMessageUtil;
import jenkins.plugins.googlechat.JenkinsTokenExpander;
import jenkins.plugins.googlechat.MessageFormat;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;

public class GoogleChatNotification extends Notifier implements SimpleBuildStep {

    private String url;
    private String message;
    private MessageFormat messageFormat;
    private boolean sameThreadNotification;
    private String threadKey;
    private boolean notifyAborted;
    private boolean notifyFailure;
    private boolean notifySingleFailure;
    private boolean notifyRepeatedFailure;
    private boolean notifyRegression;
    private boolean notifyNotBuilt;
    private boolean notifySuccess;
    private boolean notifyUnstable;
    private boolean notifyBackToNormal;
    private boolean suppressInfoLoggers;

    private TaskListener taskListener;
    private FilePath ws;
    private Run build;
    private LogUtil logUtil;
    private ResponseMessageUtil responseMessageUtil;

    @DataBoundConstructor
    public GoogleChatNotification(String url, String message) {
        this.url = url;
        this.message = message;
    }

    @DataBoundSetter
    public void setMessageFormat(MessageFormat messageFormat) {
        this.messageFormat = messageFormat;
    }

    @DataBoundSetter
    public void setSameThreadNotification(boolean sameThreadNotification) {
        this.sameThreadNotification = sameThreadNotification;
    }

    @DataBoundSetter
    public void setThreadKey(String threadKey) {
        this.threadKey = threadKey;
    }

    @DataBoundSetter
    public void setNotifyAborted(boolean notifyAborted) {
        this.notifyAborted = notifyAborted;
    }

    @DataBoundSetter
    public void setNotifyFailure(boolean notifyFailure) {
        this.notifyFailure = notifyFailure;
    }

    @DataBoundSetter
    public void setNotifySingleFailure(boolean notifySingleFailure) {
        this.notifySingleFailure = notifySingleFailure;
    }

    @DataBoundSetter
    public void setNotifyRepeatedFailure(boolean notifyRepeatedFailure) {
        this.notifyRepeatedFailure = notifyRepeatedFailure;
    }

    @DataBoundSetter
    public void setNotifyRegression(boolean notifyRegression) {
        this.notifyRegression = notifyRegression;
    }

    @DataBoundSetter
    public void setNotifyNotBuilt(boolean notifyNotBuilt) {
        this.notifyNotBuilt = notifyNotBuilt;
    }

    @DataBoundSetter
    public void setNotifySuccess(boolean notifySuccess) {
        this.notifySuccess = notifySuccess;
    }

    @DataBoundSetter
    public void setNotifyUnstable(boolean notifyUnstable) {
        this.notifyUnstable = notifyUnstable;
    }

    @DataBoundSetter
    public void setNotifyBackToNormal(boolean notifyBackToNormal) {
        this.notifyBackToNormal = notifyBackToNormal;
    }

    @DataBoundSetter
    public void setSuppressInfoLoggers(boolean suppressInfoLoggers) {
        this.suppressInfoLoggers = suppressInfoLoggers;
    }

    public String getUrl() {
        if (StringUtils.isBlank(url)) {
            return getDescriptor().getUrl();
        } else {
            return url;
        }
    }

    public String getMessage() {
        if (StringUtils.isBlank(message)) {
            return getDescriptor().getMessage();
        } else {
            return message;
        }
    }

    public MessageFormat getMessageFormat() {
        if (messageFormat == null) {
            return getDescriptor().getMessageFormat();
        } else {
            return messageFormat;
        }
    }

    public boolean isSimpleMessageFormat() {
        return MessageFormat.SIMPLE == messageFormat;
    }

    public boolean isCardMessageFormat() {
        return MessageFormat.CARD == messageFormat;
    }

    public String getThreadKey() {
        if (StringUtils.isBlank(threadKey)) {
            return getDescriptor().getThreadKey();
        } else {
            return threadKey;
        }
    }

    public boolean isSameThreadNotification() {
        return sameThreadNotification;
    }

    public boolean isNotifyAborted() {
        return notifyAborted;
    }

    public boolean isNotifyFailure() {
        return notifyFailure;
    }

    public boolean isNotifySingleFailure() {
        return notifySingleFailure;
    }

    public boolean isNotifyRepeatedFailure() {
        return notifyRepeatedFailure;
    }

    public boolean isNotifyRegression() {
        return notifyRegression;
    }

    public boolean isNotifyNotBuilt() {
        return notifyNotBuilt;
    }

    public boolean isNotifySuccess() {
        return notifySuccess;
    }

    public boolean isNotifyUnstable() {
        return notifyUnstable;
    }

    public boolean isNotifyBackToNormal() {
        return notifyBackToNormal;
    }

    public boolean isSuppressInfoLoggers() {
        return suppressInfoLoggers;
    }

    public TaskListener getTaskListener() {
        return taskListener;
    }

    public void setTaskListener(TaskListener taskListener) {
        this.taskListener = taskListener;
    }

    public FilePath getWs() {
        return ws;
    }

    public void setWs(FilePath ws) {
        this.ws = ws;
    }

    public Run getBuild() {
        return build;
    }

    public void setBuild(Run build) {
        this.build = build;
    }

    public LogUtil getLogUtil() {
        return logUtil;
    }

    public void setLogUtil(LogUtil logUtil) {
        this.logUtil = logUtil;
    }

    public ResponseMessageUtil getResponseMessageUtil() {
        return responseMessageUtil;
    }

    public void setResponseMessageUtil(ResponseMessageUtil responseMessageUtil) {
        this.responseMessageUtil = responseMessageUtil;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {

        this.setBuild(build);
        this.setWs(null);
        this.setTaskListener(listener);
        performAction();
        return true;
    }

    @Override
    public void perform(@NonNull Run<?, ?> run, @NonNull FilePath workspace, @NonNull Launcher launcher, @NonNull TaskListener listener) {

        this.setBuild(run);
        this.setWs(workspace);
        this.setTaskListener(listener);
        performAction();
    }

    @Override
    public Descriptor getDescriptor() {
        return (Descriptor) super.getDescriptor();
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    @Symbol("googlechatnotification")
    @Extension
    public static class Descriptor extends BuildStepDescriptor<Publisher> {

        public static final String PLUGIN_DISPLAY_NAME = "Google Chat Notification";

        public static final MessageFormat defaultMessageFormat = MessageFormat.SIMPLE;

        private String url;
        private String message;
        private MessageFormat messageFormat;
        private boolean sameThreadNotification;
        private String threadKey;
        private boolean notifyAborted;
        private boolean notifyFailure;
        private boolean notifyNotBuilt;
        private boolean notifySuccess;
        private boolean notifyUnstable;
        private boolean notifyBackToNormal;
        private boolean suppressInfoLoggers;

        @SuppressFBWarnings(value = "MC_OVERRIDABLE_METHOD_CALL_IN_CONSTRUCTOR", justification = "The Descriptor#load documentation states that \"The constructor of the derived class must call this method\".")
        public Descriptor() {
            load();
        }

        public FormValidation doCheckUrl(@QueryParameter String value) {
            if (value.length() == 0) {
                return FormValidation.error(Messages.googleChatNotificationUrlNotDefined());
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckMessage(@QueryParameter String value, @QueryParameter MessageFormat messageFormat) {
            if (value.length() == 0) {
                return FormValidation.error(Messages.messageNotDefined());
            } else if (MessageFormat.CARD.equals(messageFormat) && !isJSONValid(value)) {
                return FormValidation.error(Messages.messageNotValidJson());
            }
            return FormValidation.ok();
        }

        private boolean isJSONValid(String test) {
            try {
                new org.json.JSONObject(test);
            } catch (JSONException ex) {
                return false;
            }
            return true;
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return PLUGIN_DISPLAY_NAME;
        }

        public ListBoxModel doFillMessageFormatItems() {
            ListBoxModel items = new ListBoxModel();

            items.add(MessageFormat.SIMPLE.getDisplayName(), MessageFormat.SIMPLE.name());
            items.add(MessageFormat.CARD.getDisplayName(), MessageFormat.CARD.name());

            return items;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // set that to properties and call save().
            url = formData.getString("url");
            message = formData.getString("message");
            messageFormat = MessageFormat.valueOf(formData.getString("messageFormat"));
            sameThreadNotification = formData.getBoolean("sameThreadNotification");
            threadKey = formData.getString("threadKey");
            notifyAborted = formData.getBoolean("notifyAborted");
            notifyFailure = formData.getBoolean("notifyFailure");
            notifyNotBuilt = formData.getBoolean("notifyNotBuilt");
            notifySuccess = formData.getBoolean("notifySuccess");
            notifyUnstable = formData.getBoolean("notifyUnstable");
            notifyBackToNormal = formData.getBoolean("notifyBackToNormal");
            suppressInfoLoggers = formData.getBoolean("suppressInfoLoggers");

            // ^Can also use req.bindJSON(this, formData);
            save();
            return super.configure(req, formData);
        }

        public String getUrl() {
            return url;
        }

        public String getMessage() {
            return message;
        }

        public MessageFormat getMessageFormat() {
            return messageFormat != null ? messageFormat : defaultMessageFormat;
        }

        public boolean isSameThreadNotification() {
            return sameThreadNotification;
        }

        public String getThreadKey() {
            return threadKey;
        }

        public boolean isNotifyAborted() {
            return notifyAborted;
        }

        public boolean isNotifyFailure() {
            return notifyFailure;
        }

        public boolean isNotifyNotBuilt() {
            return notifyNotBuilt;
        }

        public boolean isNotifySuccess() {
            return notifySuccess;
        }

        public boolean isNotifyUnstable() {
            return notifyUnstable;
        }

        public boolean isNotifyBackToNormal() {
            return notifyBackToNormal;
        }

        public boolean isSuppressInfoLoggers() {
            return suppressInfoLoggers;
        }
    }

    private void performAction() {
        LogUtil logUtil = new LogUtil(this);
        this.setLogUtil(logUtil);

        var tokenExpander = new JenkinsTokenExpander(taskListener);
        ResponseMessageUtil responseMessageUtil = new ResponseMessageUtil(this, tokenExpander);
        this.setResponseMessageUtil(responseMessageUtil);

        CommonUtil commonUtil = new CommonUtil(this);
        commonUtil.send();
    }

    @Override
    public String toString() {
        return "GoogleChatNotification{" +
                "url='" + url + '\'' +
                ", message='" + message + '\'' +
                ", messageFormat=" + messageFormat +
                ", sameThreadNotification='" + sameThreadNotification + '\'' +
                ", threadKey='" + threadKey + '\'' +
                ", notifyAborted=" + notifyAborted +
                ", notifyFailure=" + notifyFailure +
                ", notifyNotBuilt=" + notifyNotBuilt +
                ", notifySuccess=" + notifySuccess +
                ", notifyUnstable=" + notifyUnstable +
                ", notifyBackToNormal=" + notifyBackToNormal +
                ", suppressInfoLoggers=" + suppressInfoLoggers +
                '}';
    }
}
