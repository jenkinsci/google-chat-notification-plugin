package jenkins.plugins.googlechat.logging;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Run;

public class BuildKey {

    private static final String UNKNOWN = "[UNKNOWN BUILD]";

    public static String format(Run<?, ?> run) {
        if (run == null) {
            return UNKNOWN;
        }

        return "[" + run.getFullDisplayName() + "]";
    }

    public static String format(AbstractBuild<?, ?> build) {
        if (build == null) {
            return UNKNOWN;
        }

        AbstractProject<?, ?> project = build.getProject();
        if (project == null) {
            return UNKNOWN;
        }

        return "[" + project.getFullDisplayName() + " #" + build.getNumber() + "]";
    }
}
