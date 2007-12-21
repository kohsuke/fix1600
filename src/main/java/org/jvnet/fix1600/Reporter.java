package org.jvnet.fix1600;

import java.io.File;

/**
 * @author Kohsuke Kawaguchi
 */
public interface Reporter {
    void found(File f);
}
