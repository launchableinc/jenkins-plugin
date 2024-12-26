package io.jenkins.plugins.launchable;

import hudson.Extension;
import hudson.model.listeners.RunListener;
import org.jenkinsci.plugins.workflow.flow.GraphListener;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

import javax.inject.Inject;
import java.util.logging.Logger;

import static java.util.logging.Level.*;

/**
 * Records test results reported into {@link WorkflowRun}.
 *
 * <p>
 * Our earlier attempt to implement {@link GraphListener} backfired, as when 'junitReport' step was called
 * multiple times, we end up reporting all test results accumulated up to that point, resulting in duplicate
 * data reporting.
 *
 * <p>
 * This approach defers the data ingestion all the way down to the end of a workflow run.
 */
@Extension
public class RunListenerImpl extends RunListener<WorkflowRun> {
    @Inject
    Ingester ingester;

    @Override
    public void onFinalized(WorkflowRun run) {
        try {
            ingester.slurp(run.getRootDir(), new PropsBuilder<>(run));
        } catch (Exception e) {
            // Priority #1: Do no harm to people's build
            LOGGER.log(WARNING, "Failed to send JUnit result to Launchable", e);
        }
    }

    private static final Logger LOGGER = Logger.getLogger("io.jenkins.plugins.launchable.RunListenerImpl");
}
