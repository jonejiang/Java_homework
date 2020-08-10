package book.web.rule;

import org.junit.Assert;

import java.io.File;
import java.io.IOException;

public class DefaultJavaResolutionStrategy implements
        ResolutionStrategy {

    public File getJavaExecutable() {

        //Locate java
        final String javaHome = System.getenv("JAVA_HOME");
        final File java;
        try {
            java = new File(javaHome + "/bin/java" + (System
                    .getProperty("os.name").toLowerCase().contains
                            ("win") ? ".exe" : ""))
                    .getCanonicalFile();
        } catch (IOException e) {
            throw new AssertionError("Failed to determine " +
                    "canonical path to java using JAVA_HOME: " +
                    javaHome, e);
        }

        Assert.assertTrue("Ensure that JAVA_HOME points to " + "a "
                + "valid java installation", java.exists());

        return java;
    }
}
