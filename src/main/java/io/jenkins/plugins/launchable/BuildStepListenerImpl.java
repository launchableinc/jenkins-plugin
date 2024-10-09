package io.jenkins.plugins.launchable;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.BuildStepListener;
import hudson.tasks.BuildStep;
import hudson.tasks.junit.JUnitResultArchiver;

import javax.inject.Inject;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This gets invoked when classic "freestyle" project runs its step
 */
@Extension
public class BuildStepListenerImpl extends BuildStepListener {
    @Inject
    Ingester ingester;

    public void started(AbstractBuild build, BuildStep bs, BuildListener listener) {
        // noop
    }

    public void finished(AbstractBuild build, BuildStep bs, BuildListener listener, boolean canContinue) {
        try {
            // if somebody configured JUnit result archiving step we hit here,
            // so we just slurp the result unprocessed to the server side.
            if (bs instanceof JUnitResultArchiver) {
                ingester.slurp(build.getRootDir());
            }
        } catch (IOException e) {
            // Priority #1: Do no harm to people's build
            LOGGER.log(Level.WARNING, "Failed to send JUnit result to Launchable", e);
        }
    }

    private static final Logger LOGGER = Logger.getLogger("io.jenkins.plugins.launchable.BuildStepListenerImpl");
}
