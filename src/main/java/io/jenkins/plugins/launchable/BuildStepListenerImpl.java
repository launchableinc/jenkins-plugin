package io.jenkins.plugins.launchable;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.BuildStepListener;
import hudson.tasks.BuildStep;
import hudson.tasks.junit.JUnitResultArchiver;

import javax.inject.Inject;
import java.io.File;

@Extension
public class BuildStepListenerImpl extends BuildStepListener {
    @Inject
    Configuration configuration;

    public void started(AbstractBuild build, BuildStep bs, BuildListener listener) {
        // noop
    }

    public void finished(AbstractBuild build, BuildStep bs, BuildListener listener, boolean canContinue) {
        if (bs instanceof JUnitResultArchiver) {
            File report = new File(build.getRootDir(), "junitResult.xml");
            if (!report.exists())       return; // be defensive just in case

            // TODO
            System.out.println("Sending "+report+" to Launchable");
        }
    }
}
