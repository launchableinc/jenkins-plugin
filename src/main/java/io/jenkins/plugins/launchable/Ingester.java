package io.jenkins.plugins.launchable;

import hudson.Extension;
import hudson.util.Secret;
import hudson.util.TextFile;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.IOException;

@Extension
public class Ingester extends GlobalConfiguration {
    private Secret apiKey;

    public Secret getApiKey() {
        return apiKey;
    }

    public void setApiKey(Secret apiKey) {
        this.apiKey = apiKey;
        save();
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        return super.configure(req, json);
    }

    /*package*/ void slurp(File dir) throws IOException {
        File report = new File(dir,"junitResult.xml");
        if (!report.exists())       return; // be defensive just in case

        System.out.println("Sending "+report+" to Launchable");
        System.out.println(new TextFile(report).read());
    }
}