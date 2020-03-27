package dimmer;

import java.util.List;

import dimmer.windows.WindowsMonitorsInfoRetriever;

public class DimmerForWindows implements DimmerManager
{
    private final List<MonitorInfo> monitorsInfos;

    DimmerForWindows()
    {
        monitorsInfos = WindowsMonitorsInfoRetriever.get();
    }

    @Override
    public List<MonitorInfo> getInformationOfAllMonitors()
    {
        return monitorsInfos;
    }

    @Override
    public void dimAllExcept(List<String> selectedMonitorIds)
    {

    }

    @Override
    public void dim(String monitorId)
    {

    }

    @Override
    public void undim(String untickedMonitorId)
    {

    }

//    /**
//     * Detects if the active app in the main monitor is in full screen or not.
//     * Ideally we want to check for all monitors and not just the main monitor...
//     *
//     * Source: https://stackoverflow.com/a/60501359/9988736
//     *
//     * ===================================
//     * Scrapping this idea for now: can't seem to find anything on listening for an app
//     * to become full screen. In that case the only other option for the brightness dimming to automate
//     * would be to have a timer run periodically like every 5 seconds to check if any of the
//     * monitors have a full screen app...
//     */
//    private boolean isAppInFullScreenInMainMonitor()
//    {
//
//        // Get the active window
//        WinDef.HWND activeWindow = User32.INSTANCE.GetForegroundWindow();
//        // Get the desktop window (will represent the whole screen space)
//        WinDef.HWND desktopWindow = User32.INSTANCE.GetDesktopWindow();
//
//        // Create RECT value holders to get the dimensions of active window and desktop window
//        WinDef.RECT activeRectangle = new WinDef.RECT();
//        WinDef.RECT desktopWindowRectangle = new WinDef.RECT();
//
//        // Populate the RECT variables above from the HWND
//        User32.INSTANCE.GetWindowRect(activeWindow, activeRectangle);
//        User32.INSTANCE.GetWindowRect(desktopWindow, desktopWindowRectangle);
//
//        System.out.println("Desktop: "+desktopWindowRectangle.toString()+ " !!!!! active window: "+activeRectangle.toString());
//        return activeRectangle.toString().equals( desktopWindowRectangle.toString() );
//    }
}
