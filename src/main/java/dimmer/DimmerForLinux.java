package dimmer;

import java.util.List;

public class DimmerForLinux implements DimmerManager
{
    @Override
    public List<MonitorInfo> getInformationOfAllMonitors()
    {
        throw new IllegalStateException("Operating system not supported!");
    }

    @Override
    public MonitorInfo findByMonitorId(String monitorId)
    {
        throw new IllegalStateException("Operating system not supported!");
    }

    @Override
    public void dimAllExcept(List<MonitorInfo> selectedMonitors)
    {
        throw new IllegalStateException("Operating system not supported!");
    }

    @Override
    public void dim(MonitorInfo monitorInfo)
    {
        throw new IllegalStateException("Operating system not supported!");
    }

    @Override
    public void undim(MonitorInfo monitorInfo)
    {
        throw new IllegalStateException("Operating system not supported!");
    }
}
