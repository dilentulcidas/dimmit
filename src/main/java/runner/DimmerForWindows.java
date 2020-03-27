package main.java.runner;

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
