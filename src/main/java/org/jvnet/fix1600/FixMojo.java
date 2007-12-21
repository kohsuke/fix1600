package org.jvnet.fix1600;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Kohsuke Kawaguchi
 * @phase process-classes
 * @goal fix
 */
public class FixMojo extends AbstractMojo {
    /**
     * The Maven project object
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if(project.getBuild()==null || project.getBuild().getOutputDirectory()==null)
            return; // skip - no class files here

        File classesDir = new File(project.getBuild().getOutputDirectory());
        getLog().debug("outputDir="+classesDir);

        try {
            new Tool(new Reporter() {
                public void found(File f) {
                    getLog().info("Fixed 0x1600 at "+f.getPath());
                }
            }).processDirectory(classesDir);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to fix 0x1600",e);
        }
    }

}
