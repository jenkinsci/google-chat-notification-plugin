package jenkins.plugins.googlechat;

import com.cloudbees.plugins.credentials.CredentialsMatcher;
import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import hudson.model.Item;
import hudson.model.Run;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;

import java.util.Collections;

public class CredentialsObtainer {

    public StringCredentials lookupCredentials(String credentialId, Run<?, ?> run) {
        var domainRequirements = Collections.<DomainRequirement>emptyList();
        CredentialsMatcher matcher = CredentialsMatchers.withId(credentialId);

        if (run != null) {
            Item item = run.getParent();
            var auth = Jenkins.getAuthentication();
            var credentials = CredentialsProvider.lookupCredentials(StringCredentials.class, item, auth, domainRequirements);
            return CredentialsMatchers.firstOrNull(credentials, matcher);
        } else {
            return null;
        }
    }

    @Deprecated
    public StringCredentials lookupCredentials(String credentialId) {
        return null;
    }
}
