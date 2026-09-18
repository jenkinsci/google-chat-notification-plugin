package jenkins.plugins.googlechat;

import hudson.model.Run;

public interface GoogleChatService {

    boolean publish(Run<?, ?> run, GoogleChatRequest request, String... notificationUrls);

    String getResponseString();
}
