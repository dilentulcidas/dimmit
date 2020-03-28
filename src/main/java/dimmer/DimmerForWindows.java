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
        monitorsInfos.stream()
                .filter(monitor -> !selectedMonitors.contains(monitor)) // Get the monitors which weren't selected
                .forEach(monitorToDim ->
                {
                    WindowsBrightnessHandler brightnessHandler = WindowsBrightnessHandlerFactory.from(monitorToDim);
                    // Save current brightness if haven't saved already
                    formerBrightnessValues.putIfAbsent(monitorToDim.getMonitorId(), brightnessHandler.getCurrentBrightness());
                    // Change brightness to zero
                    brightnessHandler.setBrightness(0);
                });
    }

    @Override
    public void dim(MonitorInfo monitorInfo)
    {
        WindowsBrightnessHandler brightnessHandler = WindowsBrightnessHandlerFactory.from(monitorInfo);
        formerBrightnessValues.putIfAbsent(monitorInfo.getMonitorId(), brightnessHandler.getCurrentBrightness());
        brightnessHandler.setBrightness(0);
    }

    @Override
    public void undim(MonitorInfo monitorInfo)
    {
        WindowsBrightnessHandler brightnessHandler = WindowsBrightnessHandlerFactory.from(monitorInfo);
        brightnessHandler.setBrightness(Integer.parseInt(formerBrightnessValues.get(monitorInfo.getMonitorId())));
        formerBrightnessValues.remove(monitorInfo.getMonitorId());
    }
}
