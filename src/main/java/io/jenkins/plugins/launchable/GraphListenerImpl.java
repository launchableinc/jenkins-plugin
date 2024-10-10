package io.jenkins.plugins.launchable;

import hudson.Extension;
import hudson.model.Queue;
import org.jenkinsci.plugins.workflow.flow.GraphListener;
import org.jenkinsci.plugins.workflow.graph.FlowNode;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

import javax.inject.Inject;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Extension
public class GraphListenerImpl implements GraphListener {
    @Inject
    Ingester ingester;

    @Override
    public void onNewHead(FlowNode node) {
        try {
            if (node.getDisplayFunctionName().equals("junit")) {// TODO: probably not the most robust check
                Queue.Executable owner = node.getExecution().getOwner().getExecutable();
                if (owner instanceof WorkflowRun) {// another defensive check just to be safe
                    WorkflowRun run = (WorkflowRun) owner;
                    ingester.slurp(run.getRootDir(), new PropsBuilder<>(run));
                }
            }
        } catch (IOException e) {
            // Priority #1: Do no harm to people's build
            LOGGER.log(Level.WARNING, "Failed to send JUnit result to Launchable", e);
        }
    }

    private static final Logger LOGGER = Logger.getLogger("io.jenkins.plugins.launchable.GraphListenerImpl");
}
