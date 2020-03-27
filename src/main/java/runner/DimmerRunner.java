package runner;

/**
 * Covers the implementation of the dimmer for each of the supported operating systems.
 * Puts the app running in the background with a system tray.
 */
public interface DimmerRunner
{
    void run() throws IllegalStateException;
}
