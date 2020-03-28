package dimmer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dimmer.windows.WindowsMonitorsInfoRetriever;
import dimmer.windows.brightness.WindowsBrightnessHandler;
import dimmer.windows.brightness.WindowsBrightnessHandlerFactory;

public class DimmerForWindows implements DimmerManager
{
    private final List<MonitorInfo> monitorsInfos;
    private Map<String, String> formerBrightnessValues;

    DimmerForWindows()
    {
        monitorsInfos = WindowsMonitorsInfoRetriever.get();
        formerBrightnessValues = new HashMap<>();
    }

    @Override
    public List<MonitorInfo> getInformationOfAllMonitors()
    {
        return monitorsInfos;
    }

    @Override
    public MonitorInfo findByMonitorId(String monitorId)
    {
        return monitorsInfos.stream()
                .filter(monitor -> monitor.getMonitorId().toLowerCase().equals(monitorId.toLowerCase()))
                .findFirst()
                .get();
    }

    @Override
    public void dimAllExcept(List<MonitorInfo> selectedMonitors)
    {
        selectedMonitors.forEach(monitor ->
        {
            WindowsBrightnessHandler brightnessHandler = WindowsBrightnessHandlerFactory.from(monitor);
            // Save current brightness
            formerBrightnessValues.put(monitor.getMonitorId(), brightnessHandler.getCurrentBrightness());
            // Change brightness to zero
            brightnessHandler.setBrightnessToZero();
        });
    }

    @Override
    public void dim(MonitorInfo monitorInfo)
    {
        WindowsBrightnessHandler brightnessHandler = WindowsBrightnessHandlerFactory.from(monitorInfo);
        // todo
    }

    @Override
    public void undim(MonitorInfo monitorInfo)
    {
        WindowsBrightnessHandler brightnessHandler = WindowsBrightnessHandlerFactory.from(monitorInfo);
        // todo
    }
}
