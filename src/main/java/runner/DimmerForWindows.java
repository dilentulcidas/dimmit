package runner;

import java.util.Map;

/**
 * Uses WMI powershell queries to get the current brightness and set the brightness.
 * JNA library to listen for an app that goes full screen.
 *
 * todo: find a fullscreen app listener
 */
public class DimmerForWindows implements DimmerRunner
{
    @Override
    public void run()
    {
        // todo: listener for a new full screen app being launched
        // - get all monitor ids of monitors which has full screen app running
        // - dim all monitors except monitor ids above
        // - put in a map the former brightness values of the dimmed monitors

        // todo: listener for a full screen app being exited
        // - check if there are still monitors with full screen app running
        // - if yes: dim the monitor id which full-screen app was exited. if no: undim all
        // and revert to previous brightness values before dim
    }

    static class WindowsBrightnessManager
    {
        /**
         * Get the current brightness levels of all monitors
         * @return Map with format {monitor_id, brightness_level}
         */
        private static Map<String, Integer> getBrightnessLevels()
        {
            return null;
        }

        /**
         * Dims the monitor with the provided monitorId
         */
        private static void dim(String monitorId)
        {

        }
    }
}
