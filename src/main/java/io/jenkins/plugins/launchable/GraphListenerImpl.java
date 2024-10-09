package io.jenkins.plugins.launchable;

import hudson.Extension;
import org.jenkinsci.plugins.workflow.flow.GraphListener;
import org.jenkinsci.plugins.workflow.graph.FlowNode;

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
                // owner is actually WorkflowRun
                ingester.slurp(node.getExecution().getOwner().getRootDir());
            }
        } catch (IOException e) {
            // Priority #1: Do no harm to people's build
            LOGGER.log(Level.WARNING, "Failed to send JUnit result to Launchable", e);
        }
    }

    private static final Logger LOGGER = Logger.getLogger("io.jenkins.plugins.launchable.GraphListenerImpl");
}
