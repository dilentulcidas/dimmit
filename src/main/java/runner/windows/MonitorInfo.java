package runner.windows;

import com.sun.jna.platform.win32.WinDef;

/**
 * Stores the info for one display
 */
public class MonitorInfo
{
    private final String monitorId;
    private final WinDef.RECT screenSize;
    private final boolean isPrimary;
    private final String currentBrightness;

    public MonitorInfo(String monitorId, WinDef.RECT screenSize, boolean isPrimary, String currentBrightness)
    {
        this.monitorId = monitorId;
        this.screenSize = screenSize;
        this.isPrimary = isPrimary;
        this.currentBrightness = currentBrightness;
    }

    public String getMonitorId()
    {
        return monitorId;
    }

    public WinDef.RECT getScreenSize()
    {
        return screenSize;
    }

    public boolean isPrimary()
    {
        return isPrimary;
    }

    public String getCurrentBrightness()
    {
        return currentBrightness;
    }
}
