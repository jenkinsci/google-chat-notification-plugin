package jenkins.plugins.googlechat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;

import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import hudson.util.Secret;

public class StandardGoogleChatServiceTest {

    @Mock
    private HttpClient httpClient;

    private AutoCloseable autoCloseable;

    @Before
    public void setup() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    public void shouldReturnFalseWhenNoNotificationUrls() {
        var request = GoogleChatRequest.newSimpleRequest().withMessage("Hello!").build();
        var service = new StandardGoogleChatServiceStub(httpClient);
        assertThat(service.publish(request)).isFalse();
    }

    @Test
    public void shouldReturnFalseWhenOnlyInvalidNotificationUrls() {
        var request = GoogleChatRequest.newSimpleRequest().withMessage("Hello!").build();
        var service = new StandardGoogleChatServiceStub(httpClient);
        assertThat(service.publish(request, "invalid URL", "ftp://invalid", "test://jenkins.com")).isFalse();
    }

    @Test
    public void shouldReturnFalseWhenHttpResponseNotOk() throws IOException, InterruptedException {
        var request = GoogleChatRequest.newSimpleRequest().withMessage("Hello!").build();
        var service = new StandardGoogleChatServiceStub(httpClient);

        var response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(400);
        when(response.body()).thenReturn("Not OK!");

        when(httpClient.send(any(HttpRequest.class), any(BodyHandler.class)))
                .thenReturn(response);

        assertThat(service.publish(request, "http://google.com/test?token=123")).isFalse();
    }

    @Test
    public void shouldReturnFalseWhenExceptionIsThrown() throws IOException, InterruptedException {
        var request = GoogleChatRequest.newSimpleRequest().withMessage("Hello!").build();
        var service = new StandardGoogleChatServiceStub(httpClient);

        when(httpClient.send(any(HttpRequest.class), any(BodyHandler.class)))
                .thenThrow(IOException.class);

        assertThat(service.publish(request, "http://google.com/test?token=123")).isFalse();
    }

    @Test
    public void shouldPublishSimpleMessageToHttpUrl() throws IOException, InterruptedException {
        var request = GoogleChatRequest.newSimpleRequest().withMessage("Hello!").build();
        var service = new StandardGoogleChatServiceStub(httpClient);

        var response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("OK!");

        when(httpClient.send(any(HttpRequest.class), any(BodyHandler.class)))
                .thenReturn(response);

        assertThat(service.publish(request, "http://google.com/test?token=123")).isTrue();
    }

    @Test
    public void shouldPublishSimpleMessageToCredentialUrl() throws IOException, InterruptedException {
        var request = GoogleChatRequest.newSimpleRequest().withMessage("Hello!").build();
        var credentialsObtainer = mock(CredentialsObtainer.class);
        var service = new StandardGoogleChatServiceStub(httpClient, credentialsObtainer);

        var response = mock(HttpResponse.class);

        var stringCredentials = mock(StringCredentials.class);
        var secret = mock(Secret.class);

        when(secret.getPlainText()).thenReturn("http://google.com/test?token=123");
        when(stringCredentials.getSecret()).thenReturn(secret);
        when(credentialsObtainer.lookupCredentials(anyString())).thenReturn(stringCredentials);

        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("OK!");

        when(httpClient.send(any(HttpRequest.class), any(BodyHandler.class)))
                .thenReturn(response);

        assertThat(service.publish(request, "id:credential_id_for_room1")).isTrue();
    }

    @Test
    public void shouldPublishSimpleThreadedMessageToHttpUrl() throws IOException, InterruptedException {
        var request = GoogleChatRequest.newSimpleRequest().withMessage("Hello!").withThread("test").build();
        var service = new StandardGoogleChatServiceStub(httpClient);

        var response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("OK!");

        when(httpClient.send(any(HttpRequest.class), any(BodyHandler.class)))
                .thenReturn(response);

        assertThat(service.publish(request, "http://google.com/test?token=123")).isTrue();
    }

    public static class StandardGoogleChatServiceStub extends StandardGoogleChatService {

        private final HttpClient httpClient;
        private final CredentialsObtainer credentialsObtainer;

        public StandardGoogleChatServiceStub(HttpClient httpClient, CredentialsObtainer credentialsObtainer) {
            this.httpClient = httpClient;
            this.credentialsObtainer = credentialsObtainer;
        }

        public StandardGoogleChatServiceStub(HttpClient httpClient) {
            this(httpClient, null);
        }

        @Override
        protected HttpClient getHttpClient() {
            return httpClient;
        }

        @Override
        protected CredentialsObtainer getCredentialsObtainer() {
            return credentialsObtainer;
        }
    }
}
