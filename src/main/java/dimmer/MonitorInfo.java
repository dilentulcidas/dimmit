package dimmer;

/**
 * Generic class that stores the info for a display. Should be applicable for all operating systems.
 */
public class MonitorInfo
{
    private final String monitorId;
    private final boolean isPrimary;

    public MonitorInfo(String monitorId, boolean isPrimary)
    {
        this.monitorId = monitorId;
        this.isPrimary = isPrimary;
    }

    public String getMonitorId()
    {
        return monitorId;
    }

    public boolean isPrimary()
    {
        return isPrimary;
    }
}
