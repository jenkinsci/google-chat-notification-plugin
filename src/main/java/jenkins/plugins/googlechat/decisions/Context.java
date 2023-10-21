package jenkins.plugins.googlechat.decisions;

import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.model.Result;
import hudson.model.Run;
import jenkins.plugins.googlechat.logging.BuildKey;

public class Context {

    private final Run<?, ?> current;
    private final Run<?, ?> previous;

    public Context(Run<?, ?> current, Run<?, ?> previous) {
        this.current = current;
        this.previous = previous;
    }

    public String currentKey() {
        return BuildKey.format(current);
    }

    public Result previousResultOrSuccess() {
        if (previous == null || previous.getResult() == null) {
            return Result.SUCCESS;
        }
        return previous.getResult();
    }

    @Nullable
    public Result currentResult() {
        if (current == null) {
            return null;
        }
        return current.getResult();
    }

    public Result currentResultOrSuccess() {
        if (current == null || current.getResult() == null) {
            return Result.SUCCESS;
        }
        return current.getResult();
    }
}
