package io.jenkins.plugins.launchable;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.tasks.junit.TestResult;
import hudson.util.Secret;
import hudson.util.TextFile;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.GzipCompressingEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    /**
     * @param dir        Directory that's supposed to contain test results. See {@link TestResult} for the data format.
     * @param properties Additional contextual data to submit along with the test results.
     */
    /*package*/ void slurp(File dir, PropsBuilder<?> properties) throws IOException {
        try {
            File report = new File(dir, "junitResult.xml");
            if (!report.exists()) return; // be defensive just in case

            if (apiKey==null)     return; // not yet configured

            // attempted to use JDK HttpRequest, but gave up due to the lack of multipart support
            // TODO: how do I obtain a properly configured HttpClient for the proxy setting in Jenkins?
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

                String endpoint = System.getenv("INSIGHT_UPLOAD_URL");
                if (endpoint==null) {
                    endpoint = DEFAULT_UPLOAD_URL;
                }
                HttpPost hc = new HttpPost(endpoint);

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addTextBody("metadata", properties.build().toString(), ContentType.APPLICATION_JSON);
                builder.addBinaryBody("file", report, ContentType.APPLICATION_XML, "junitResult.xml");

                hc.setEntity(new GzipCompressingEntity(builder.build()));
                hc.addHeader("Authorization", "Bearer " + apiKey.getPlainText());

                try (CloseableHttpResponse response = httpClient.execute(hc)) {
                    if (response.getStatusLine().getStatusCode() >= 300) {
                        // treat redirect as an error, for the time being. we submit a big payload, so we don't want
                        // to be forced to repeat the payload after we send the whole thing once.
                        LOGGER.log(Level.WARNING, "Failed to submit test results: {0}", response.getStatusLine());
                    }
                }
            }
        } catch (IOException e) {
            // don't let our bug get in the way of orderly execution of jobs, as that'd be the fasest way to
            // get kicked out of installations.
            LOGGER.log(Level.WARNING, "Failed to submit test results", e);
        }
    }

    private static final Logger LOGGER = Logger.getLogger(Ingester.class.getName());
    private static final String DEFAULT_UPLOAD_URL = "https://api.mercury.launchableinc.com/TODO";
}
