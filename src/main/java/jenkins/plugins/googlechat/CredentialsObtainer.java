package jenkins.plugins.googlechat;

import java.util.Collections;

import org.jenkinsci.plugins.plaincredentials.StringCredentials;

import com.cloudbees.plugins.credentials.CredentialsMatcher;
import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;

import hudson.security.ACL;
import jenkins.model.Jenkins;

public class CredentialsObtainer {

    public StringCredentials lookupCredentials(String credentialId) {
        var credentials = CredentialsProvider.lookupCredentials(StringCredentials.class, Jenkins.get(), ACL.SYSTEM, Collections.<DomainRequirement>emptyList());
        CredentialsMatcher matcher = CredentialsMatchers.withId(credentialId);
        return CredentialsMatchers.firstOrNull(credentials, matcher);
    }
}
