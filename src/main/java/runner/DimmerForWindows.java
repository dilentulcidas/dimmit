package runner;

import java.util.List;
import java.util.Map;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import runner.windows.MonitorInfo;
import runner.windows.MonitorsInfoRetriever;

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

        // experiment. print every second
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask()
//        {
//            @Override
//            public void run()
//            {
//                System.out.println("There's full screen: "+isAppInFullScreen());
//            }
//        }, 0, 1000);

        List<MonitorInfo> monitorInfos = MonitorsInfoRetriever.get();
        monitorInfos.forEach(e -> System.out.println(e.getMonitorId() + ":" + e.getScreenSize() + ":" + e.isPrimary() + ":" + e.getCurrentBrightness()));
    }

    static class BrightnessManager
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

    static class FullScreenDetector
    {
        /**
         * Checks all monitors whether there's an active app
         * with full screen.
         *
         * @return Map of format {monitor_id, hasFullScreen}
         */
        private static Map<String, Boolean> getDisplaysAndFullScreenValue()
        {
            return null;
        }
    }

    /**
     * Detects if the active app in the main monitor is in full screen or not.
     * Ideally we want to check for all monitors and not just the main monitor...
     *
     * Source: https://stackoverflow.com/a/60501359/9988736
     */
    private boolean isAppInFullScreenInMainMonitor()
    {
        // todo: find a way to get the foreground window for all monitors. we got the desktopwindow size

        // Get the active window
        WinDef.HWND activeWindow = User32.INSTANCE.GetForegroundWindow();
        // Get the desktop window (will represent the whole screen space)
        WinDef.HWND desktopWindow = User32.INSTANCE.GetDesktopWindow();

        // Create RECT value holders to get the dimensions of active window and desktop window
        WinDef.RECT activeRectangle = new WinDef.RECT();
        WinDef.RECT desktopWindowRectangle = new WinDef.RECT();

        // Populate the RECT variables above from the HWND
        User32.INSTANCE.GetWindowRect(activeWindow, activeRectangle);
        User32.INSTANCE.GetWindowRect(desktopWindow, desktopWindowRectangle);

        System.out.println("Desktop: "+desktopWindowRectangle.toString()+ " !!!!! active window: "+activeRectangle.toString());
        return activeRectangle.toString().equals( desktopWindowRectangle.toString() );
    }
}
