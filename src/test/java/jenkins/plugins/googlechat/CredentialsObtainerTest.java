package jenkins.plugins.googlechat;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import hudson.model.Item;
import hudson.model.Run;
import hudson.security.ACL;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for CredentialsObtainer to ensure lookup uses the build/item context
 * and does not fall back to Jenkins.get() + ACL.SYSTEM by default.
 */
public class CredentialsObtainerTest {

    private CredentialsObtainer credentialsObtainer;

    private MockedStatic<CredentialsProvider> credentialsProviderStatic;
    private MockedStatic<Jenkins> jenkinsStatic;

    @Before
    public void setUp() {
        credentialsObtainer = new CredentialsObtainer();
        // Prepare static mocks
        credentialsProviderStatic = Mockito.mockStatic(CredentialsProvider.class);
        jenkinsStatic = Mockito.mockStatic(Jenkins.class);
    }

    @After
    public void tearDown() {
        credentialsProviderStatic.close();
        jenkinsStatic.close();
    }

    @Test
    public void lookupUsesItemContext_and_doesNotUseSystemContext() {
        // Arrange
        Run<?, ?> run = mock(Run.class);
        Item item = mock(Item.class);
        when(run.getParent()).thenAnswer(invocation -> item);

        // Prepare a system-scoped credential that would be returned if lookup used Jenkins.get() + ACL.SYSTEM
        StringCredentials systemCred = mock(StringCredentials.class);
        List<StringCredentials> systemList = Collections.singletonList(systemCred);

        // When CredentialsProvider.lookupCredentials is invoked with Jenkins.get() and ACL.SYSTEM return the systemList
        Jenkins jenkinsInstance = mock(Jenkins.class);
        jenkinsStatic.when(Jenkins::get).thenReturn(jenkinsInstance);
        // We don't expect the plugin to call CredentialsProvider.lookupCredentials with (StringCredentials.class, jenkinsInstance, ACL.SYSTEM, ...),
        // but if it did, return the systemList to simulate an unsafe behavior.
        credentialsProviderStatic.when(() ->
                        CredentialsProvider.lookupCredentials(
                                eq(StringCredentials.class),
                                eq(jenkinsInstance),
                                eq(ACL.SYSTEM),
                                any(List.class)))
                .thenReturn(systemList);

        // When CredentialsProvider.lookupCredentials is invoked with the item context, return empty list (credential not visible in item context)
        credentialsProviderStatic.when(() ->
                        CredentialsProvider.lookupCredentials(
                                eq(StringCredentials.class),
                                eq(item),
                                any(),
                                any(List.class)))
                .thenReturn(Collections.emptyList());

        // Act
        var result = credentialsObtainer.lookupCredentials("some-id", run);

        // Assert: because the CredentialsObtainer should use item context, the credential returned should be null
        assertThat(result).isNull();

        // Verify: ensure CredentialsProvider.lookupCredentials was invoked with the item (not with ACL.SYSTEM) at least once
        credentialsProviderStatic.verify(() ->
                CredentialsProvider.lookupCredentials(
                        eq(StringCredentials.class),
                        eq(item),
                        any(),
                        any(List.class)));
    }

    @Test
    public void deprecatedLookupReturnsNull_forSafety() {
        // Act
        var result = credentialsObtainer.lookupCredentials("any-id");

        // Assert
        assertThat(result).isNull();
    }
}