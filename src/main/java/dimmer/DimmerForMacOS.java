package dimmer;

import java.util.List;

public class DimmerForMacOS implements DimmerManager
{
    @Override
    public List<MonitorInfo> getInformationOfAllMonitors()
    {
        throw new IllegalStateException("Operating system not supported!");
    }

    @Override
    public void dimAllExcept(List<String> selectedMonitorIds)
    {
        throw new IllegalStateException("Operating system not supported!");
    }

    @Override
    public void dim(String monitorId)
    {
        throw new IllegalStateException("Operating system not supported!");
    }

    @Override
    public void undim(String monitorId)
    {
        throw new IllegalStateException("Operating system not supported!");
    }
}
