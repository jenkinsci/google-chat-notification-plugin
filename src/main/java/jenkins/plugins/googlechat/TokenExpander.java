package jenkins.plugins.googlechat;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Run;

public interface TokenExpander {

    String expand(String template, AbstractBuild<?, ?> build);

    String expand(String template, Run<?, ?> build, FilePath workspace);
}
