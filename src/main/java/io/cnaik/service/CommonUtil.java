package io.cnaik.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import hudson.FilePath;
import hudson.ProxyConfiguration;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.cnaik.GoogleChatNotification;
import jenkins.model.Jenkins;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.util.EntityUtils;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.jenkinsci.plugins.tokenmacro.TokenMacro;

public class CommonUtil {

    private GoogleChatNotification googleChatNotification;
    private TaskListener taskListener;
    private FilePath ws;
    private Run build;
    private LogUtil logUtil;
    private ResponseMessageUtil responseMessageUtil;

    private static final int TIME_OUT = 15 * 1000;

    public CommonUtil(GoogleChatNotification googleChatNotification) {
        this.googleChatNotification = googleChatNotification;
        this.taskListener = googleChatNotification.getTaskListener();
        this.ws = googleChatNotification.getWs();
        this.build = googleChatNotification.getBuild();
        this.logUtil = googleChatNotification.getLogUtil();
        this.responseMessageUtil = googleChatNotification.getResponseMessageUtil();
    }

    public void send() {

        boolean sendNotificationFlag = checkPipelineFlag();

        if (logUtil.printLogEnabled()) {
            logUtil.printLog("Send Google Chat Notification condition is : " + sendNotificationFlag);
        }

        if(!sendNotificationFlag) {
            return;
        }

        String json = "";

        if(false) {
            json = responseMessageUtil.createCardMessage();
        } else {
            class Response {
                public String text;

                public void setText(String text) {
                    this.text = text;
                }
            }
            Response response = new Response() {
                {
                    setText(responseMessageUtil.createTextMessage());
                }
            };
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
            try {
            json = mapper.writeValueAsString(response);
            } catch (Exception e) {
            }
        }

        if (logUtil.printLogEnabled()) {
            logUtil.printLog("Final formatted text: " + json);
        }

        notifyForEachUrl(json);
    }

    private void notifyForEachUrl(String json) {

        String[] urlDetails = googleChatNotification.getUrl().split(",");
        boolean response;
        String[] url;

        for(String urlDetail: urlDetails) {

            response = call(urlDetail, json);

            if (!response && StringUtils.isNotEmpty(urlDetail)
                    && urlDetail.trim().startsWith("id:")) {

                url = urlDetail.trim().split("id:");

                CredentialUtil credentialUtil = new CredentialUtil();
                StringCredentials stringCredentials = credentialUtil.lookupCredentials(url[1]);

                if (stringCredentials != null
                        && stringCredentials.getSecret() != null) {

                    response = call(stringCredentials.getSecret().getPlainText(), json);
                }
            }

            if (!response) {
                logUtil.printLog("Invalid Google Chat Notification URL found: " + urlDetail);
            }
        }
    }

    private boolean checkWhetherToSend() {

        boolean result = false;

        if(build == null || build.getResult() == null || googleChatNotification == null) {
            return result;
        }

        Run prevRun = build.getPreviousBuild();
        Result previousResult = (prevRun != null) ? prevRun.getResult() : Result.SUCCESS;

        if(googleChatNotification.isNotifyAborted()
                && Result.ABORTED == build.getResult()) {

            result = true;

        } else if(googleChatNotification.isNotifyFailure()
                && Result.FAILURE == build.getResult()) {

            result = true;

        } else if(googleChatNotification.isNotifyNotBuilt()
                && Result.NOT_BUILT == build.getResult()) {

            result = true;

        } else if(googleChatNotification.isNotifySuccess()
                && Result.SUCCESS == build.getResult()) {

            result = true;

        } else if(googleChatNotification.isNotifyUnstable()
                && Result.UNSTABLE == build.getResult()) {

            result = true;

        } else if(googleChatNotification.isNotifyBackToNormal() && Result.SUCCESS == build.getResult()
                && (   Result.ABORTED == previousResult
                || Result.FAILURE == previousResult
                || Result.UNSTABLE == previousResult
                || Result.NOT_BUILT == previousResult) ) {

            result = true;

        }

        return result;
    }

    private boolean checkPipelineFlag() {

        if(googleChatNotification != null &&
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

    private boolean checkIfValidURL(String url) {
        return (StringUtils.isNotEmpty(url)
                && (url.trim().contains("https") || url.trim().contains("http"))
                && url.trim().contains("?"));
    }

    private boolean call(String urlDetail, String json) {

        if (checkIfValidURL(urlDetail)) {
            try {

                if(googleChatNotification.isSameThreadNotification()) {
                    String jobName = TokenMacro.expandAll(build, ws, taskListener, "${JOB_NAME}", false, null);
                    urlDetail = urlDetail + "&threadKey=" + URIUtil.encodePath(jobName);
                }

                HttpPost post = new HttpPost(urlDetail);
                StringEntity stringEntity = new StringEntity(json);
                post.setEntity(stringEntity);
                post.setHeader("Content-type", "application/json");

                post.setConfig(getTimeoutConfig());

                CloseableHttpClient client = getHttpClient();
                CloseableHttpResponse response = client.execute(post);

                int responseCode = response.getStatusLine().getStatusCode();
                if(responseCode != HttpStatus.SC_OK) {
                    HttpEntity entity = response.getEntity();
                    String responseString = EntityUtils.toString(entity);

                    if(logUtil.printLogEnabled()) {
                        logUtil.printLog("Google Chat post may have failed. Response: " + responseString + " , Response Code: " + responseCode);
                    }
                }

            } catch (Exception e) {
                logUtil.printLog("Exception while posting Google Chat message: " + e.getMessage());
            }
            return true;
        }
        return false;
    }

    private CloseableHttpClient getHttpClient() {

        final HttpClientBuilder clientBuilder = HttpClients.custom();
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        clientBuilder.setDefaultCredentialsProvider(credentialsProvider);

        if (Jenkins.getInstance() != null) {
            ProxyConfiguration proxy = Jenkins.getInstance().proxy;
            if (proxy != null) {
                final HttpHost proxyHost = new HttpHost(proxy.name, proxy.port);
                final HttpRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxyHost);
                clientBuilder.setRoutePlanner(routePlanner);

                String username = proxy.getUserName();
                String password = proxy.getPassword();
                // Consider it to be passed if username specified. Sufficient?

                if(logUtil.printLogEnabled()) {
                    logUtil.printLog("Using proxy authentication (user=" + username + "), (host=" + proxy.name + "), (port=" + proxy.port + ")");
                }

                if (username != null && !"".equals(username.trim())) {
                    credentialsProvider.setCredentials(new AuthScope(proxyHost),
                            new UsernamePasswordCredentials(username, password));
                }
            }
        }

        return clientBuilder.build();
    }

    private RequestConfig getTimeoutConfig() {

        RequestConfig.Builder requestConfig = RequestConfig.custom();

        requestConfig.setConnectTimeout(TIME_OUT);
        requestConfig.setConnectionRequestTimeout(TIME_OUT);
        requestConfig.setSocketTimeout(TIME_OUT);

        return requestConfig.build();
    }
}
