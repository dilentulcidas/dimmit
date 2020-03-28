package dimmer;

/**
 * Generic class that stores the info for a display. Should be applicable for all operating systems.
 */
public class MonitorInfo
{
    private final String menuItemName; // How this monitor will be displayed in the menu item when right clicked
    private final int monitorIndex;
    private final String monitorId;
    private final boolean isPrimary;
    private final boolean isBrightnessDynamicallyAdjustable;

    public MonitorInfo(String menuItemName, int monitorIndex, String monitorId, boolean isPrimary, boolean isBrightnessDynamicallyAdjustable)
    {
        this.menuItemName = menuItemName;
        this.monitorIndex = monitorIndex;
        this.monitorId = monitorId;
        this.isPrimary = isPrimary;
        this.isBrightnessDynamicallyAdjustable = isBrightnessDynamicallyAdjustable;
    }

    public String getMenuItemName()
    {
        return menuItemName;
    }

    public int getMonitorIndex()
    {
        return monitorIndex;
    }

    public String getMonitorId()
    {
        return monitorId;
    }

    public boolean isPrimary()
    {
        return isPrimary;
    }

    public boolean isBrightnessDynamicallyAdjustable()
    {
        return isBrightnessDynamicallyAdjustable;
    }
}
