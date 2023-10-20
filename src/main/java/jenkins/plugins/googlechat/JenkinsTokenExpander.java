package jenkins.plugins.googlechat;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jenkinsci.plugins.tokenmacro.MacroEvaluationException;
import org.jenkinsci.plugins.tokenmacro.TokenMacro;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.cnaik.Messages;

public class JenkinsTokenExpander implements TokenExpander {

    private static final Logger logger = Logger.getLogger(JenkinsTokenExpander.class.getName());

    private final TaskListener listener;

    public JenkinsTokenExpander(TaskListener listener) {
        this.listener = listener;
    }

    @Override
    public String expand(String template, AbstractBuild<?, ?> build) {
        try {
            return TokenMacro.expandAll(build, listener, template, false, null);
        } catch (MacroEvaluationException | IOException | InterruptedException e) {
            logger.log(Level.SEVERE, Messages.failedToProcessTokenMacros(), e);
            return "[UNPROCESSABLE] " + template;
        }
    }

    @Override
    public String expand(String template, Run<?, ?> build, FilePath workspace) {
        try {
            return TokenMacro.expandAll(build, workspace, listener, template, false, null);
        } catch (MacroEvaluationException | IOException | InterruptedException e) {
            logger.log(Level.SEVERE, Messages.failedToProcessTokenMacros(), e);
            return "[UNPROCESSABLE] " + template;
        }
    }
}
