package dimmer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

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
    public void dimAllExcept(List<MonitorInfo> tickedMonitors)
    {
        List<MonitorInfo> untickedMonitors = monitorsInfos.stream()
                .filter(monitor -> !tickedMonitors.contains(monitor))
                .collect(Collectors.toList());
        untickedMonitors.forEach(this::dim);
        tickedMonitors.forEach(this::undim);
    }

    @Override
    public void dim(MonitorInfo monitorInfo)
    {
        try
        {
            WindowsBrightnessHandler brightnessHandler = WindowsBrightnessHandlerFactory.from(monitorInfo);
            formerBrightnessValues.putIfAbsent(monitorInfo.getMonitorId(), brightnessHandler.getCurrentBrightness());
            brightnessHandler.setBrightness(0);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void undim(MonitorInfo monitorInfo)
    {
        try
        {
            if (formerBrightnessValues.containsKey(monitorInfo.getMonitorId()))
            {
                WindowsBrightnessHandler brightnessHandler = WindowsBrightnessHandlerFactory.from(monitorInfo);
                brightnessHandler.setBrightness(Integer.parseInt(formerBrightnessValues.get(monitorInfo.getMonitorId())));
                formerBrightnessValues.remove(monitorInfo.getMonitorId());
            } else
            {
                System.out.println("Monitor with model [" + monitorInfo.getMenuItemName() + "] is already undimmed!");
            }
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void undimAll()
    {
        monitorsInfos.forEach(this::undim);
    }
}
