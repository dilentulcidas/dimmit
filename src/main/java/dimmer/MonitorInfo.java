package dimmer;

/**
 * Generic class that stores the info for a display. Should be applicable for all operating systems.
 */
public class MonitorInfo
{
    private final String monitorId;
    private final boolean isPrimary;
    private final boolean isBrightnessDynamicallyAdjustable;

    public MonitorInfo(String monitorId, boolean isPrimary, boolean isBrightnessDynamicallyAdjustable)
    {
        this.monitorId = monitorId;
        this.isPrimary = isPrimary;
        this.isBrightnessDynamicallyAdjustable = isBrightnessDynamicallyAdjustable;
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
