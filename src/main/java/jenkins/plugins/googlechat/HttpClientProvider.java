package jenkins.plugins.googlechat;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.List;

import hudson.ProxyConfiguration;
import jenkins.model.Jenkins;

public class HttpClientProvider {

    public HttpClient getNewClient() {
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
}
