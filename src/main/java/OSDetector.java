package main.java;

import java.util.Locale;

import main.java.runner.OSType;

/**
 * Checks which operating system the user is running
 */
public class OSDetector
{
    static OSType getOperatingSystemType() throws IllegalStateException
    {
        String detectedOS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if ((detectedOS.indexOf("mac") >= 0) || (detectedOS.indexOf("darwin") >= 0)) {
            return OSType.MacOS;
        } else if (detectedOS.indexOf("win") >= 0) {
            return OSType.Windows;
        } else if (detectedOS.indexOf("nux") >= 0) {
            return OSType.Linux;
        } else {
            throw new IllegalStateException("Operating system not supported!");
        }
    }
}
