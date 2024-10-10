package io.jenkins.plugins.launchable;

import hudson.model.Run;
import net.sf.json.JSONObject;

/**
 * Extensive properties builder for a Run object.
 */
class PropsBuilder<B extends Run<?,?>> {
    protected final B run;

    public PropsBuilder(B run) {
        this.run = run;
    }

    public JSONObject build() {
        return new JSONObject()
            .accumulate("job", buildJobProperties())
            .accumulate("build", buildRunProperties());
    }

    protected JSONObject buildRunProperties() {
        return new JSONObject()
            .accumulate("number", run.getNumber())
            .accumulate("displayName", run.getDisplayName());
    }

    protected JSONObject buildJobProperties() {
        return new JSONObject()
            .accumulate("fullName", run.getParent().getFullName())
            .accumulate("type", run.getParent().getClass().getName());
    }
}
