package org.jvnet.fix1600;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.MatchingTask;

import java.io.File;
import java.io.IOException;

/**
 * Ant task implementation.
 * 
 * @author Kohsuke Kawaguchi
 */
public class FixTask extends MatchingTask implements Reporter {
    File dir;

    public void setDir(File dir) {
        this.dir = dir;
    }

    public void execute() throws BuildException {
        Tool tool = new Tool(this);
        DirectoryScanner ds = getDirectoryScanner(dir);
        for( String f : ds.getIncludedFiles() ) {
            try {
                tool.processClassFile(new File(dir,f));
            } catch (IOException e) {
                throw new BuildException("Failed to process "+f,e);
            }
        }
    }

    public void found(File f) {
        log("Found 0x1600 at "+f.getPath(), Project.MSG_INFO);
    }
}
