package jenkins.plugins.googlechat;

public interface GoogleChatService {

    boolean publish(GoogleChatRequest request, String... notificationUrls);
    
    String getResponseString();
}
