package jenkins.plugins.googlechat;

import hudson.ProxyConfiguration;
import io.cnaik.Messages;
import io.cnaik.model.google.MessageReplyOption;
import jenkins.model.Jenkins;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StandardGoogleChatService implements GoogleChatService {

    private static final Logger logger = Logger.getLogger(StandardGoogleChatService.class.getName());

    private static final long TIMEOUT = 15 * 1000;

    private String responseString;

    @Override
    public String getResponseString() {
        return responseString;
    }

    @Override
    public boolean publish(GoogleChatRequest request, String... notificationUrls) {
        boolean success = false;
        String[] url;

        for (String urlDetail : notificationUrls) {

            success = call(urlDetail, request);

            if (!success && StringUtils.isNotEmpty(urlDetail)
                    && urlDetail.trim().startsWith("id:")) {

                url = urlDetail.trim().split("id:");

                var credentialsObtainer = getCredentialsObtainer();
                var stringCredentials = credentialsObtainer.lookupCredentials(url[1]);

                if (stringCredentials != null) {
                    success = call(stringCredentials.getSecret().getPlainText(), request);
                }
            }

            if (!success) {
                logger.log(Level.WARNING, Messages.invalidGoogleChatNotificationUrl(urlDetail));
            }
        }

        return success;
    }

    protected CredentialsObtainer getCredentialsObtainer() {
        return new CredentialsObtainer();
    }

    private boolean call(String urlDetail, GoogleChatRequest request) {
        var success = true;

        if (checkIfValidURL(urlDetail)) {
            try {
                if (request.getThread() != null) {
                    urlDetail = urlDetail + "&messageReplyOption=" + MessageReplyOption.REPLY_MESSAGE_FALLBACK_TO_NEW_THREAD;
                    logger.log(Level.INFO, Messages.willSendMessageToThread(request.getThread()));
                }

                var client = getHttpClient();

                var httpRequest = HttpRequest.newBuilder()
                        .uri(URI.create(urlDetail))
                        .POST(HttpRequest.BodyPublishers.ofString(request.getBody().toString()))
                        .timeout(Duration.ofSeconds(TIMEOUT))
                        .build();

                var response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                responseString = response.body();

                if (response.statusCode() != 200) {
                    logger.log(Level.WARNING, Messages.googleChatPostMayHaveFailed(responseString, response.statusCode()));
                    success = false;
                }

            } catch (InterruptedException | IOException e) {
                logger.log(Level.WARNING, Messages.exceptionPostingGoogleChatMessage(e.getMessage()));
                success = false;
            }
        } else {
            success = false;
        }

        return success;
    }

    protected HttpClient getHttpClient() {
        HttpClient.Builder builder = HttpClient.newBuilder();
        Jenkins jenkins = Jenkins.getInstanceOrNull();
        if (jenkins != null) {
            ProxyConfiguration proxy = jenkins.proxy;
            if (proxy != null) {
                builder.proxy(getProxySelector(proxy));
                if (proxy.getUserName() != null) {
                    builder.authenticator(getProxyAuthenticator(proxy));
                }
            }
        }
        return builder.build();
    }

    private static ProxySelector getProxySelector(ProxyConfiguration proxy) {
        return new ProxySelector() {
            @Override
            public List<Proxy> select(URI uri) {
                if (proxy.getNoProxyHostPatterns().stream().anyMatch(pattern -> pattern.matcher(uri.getHost()).matches())) {
                    return List.of(Proxy.NO_PROXY);
                }
                return List.of(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy.name, proxy.port)));
            }

            @Override
            public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {

            }
        };
    }

    private static Authenticator getProxyAuthenticator(ProxyConfiguration proxy) {
        return new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                if (getRequestorType() == RequestorType.PROXY && getRequestingHost().equalsIgnoreCase(proxy.name) && getRequestingPort() == proxy.port) {
                    return new PasswordAuthentication(proxy.getUserName(), proxy.getSecretPassword().getPlainText().toCharArray());
                }
                return null;
            }
        };
    }

    private boolean checkIfValidURL(String url) {
        return (StringUtils.isNotEmpty(url)
                && (url.trim().contains("https") || url.trim().contains("http"))
                && url.trim().contains("?"));
    }
}
