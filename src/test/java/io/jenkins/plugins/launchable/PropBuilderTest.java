package io.jenkins.plugins.launchable;

import com.cloudbees.hudson.plugins.folder.Folder;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import net.sf.json.JSONObject;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class PropBuilderTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void basics() throws Exception {
        Folder folder1 = j.jenkins.createProject(Folder.class,"foo");
        FreeStyleProject project = folder1.createProject(FreeStyleProject.class,"bar");
        FreeStyleBuild build = project.scheduleBuild2(0).get();

        JSONObject props = new PropsBuilder<>(build).build();
        MatcherAssert.assertThat(props, is(new JSONObject()
            .accumulate("job", new JSONObject()
                .accumulate("fullName", "foo/bar")
                .accumulate("type", FreeStyleProject.class.getName())
                .accumulate("components", new JSONObject[] {
                    new JSONObject()
                        .accumulate("name", "foo")
                        .accumulate("type", Folder.class.getName()),
                    new JSONObject()
                        .accumulate("name", "bar")
                        .accumulate("type", FreeStyleProject.class.getName())
                }))
            .accumulate("build", new JSONObject()
                .accumulate("number", 1)
                .accumulate("displayName", "#1")
            )
        ));
    }
}
