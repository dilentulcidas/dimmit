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
        // Get the monitors which weren't selected
        List<MonitorInfo> untickedMonitors = monitorsInfos.stream()
                .filter(monitor -> !tickedMonitors.contains(monitor))
                .collect(Collectors.toList());

        untickedMonitors.forEach(this::dim);

        // Undim the ticked monitors
        tickedMonitors.forEach(this::undim);
    }

    @Override
    public void dim(MonitorInfo monitorInfo)
    {
        try
        {
            WindowsBrightnessHandler brightnessHandler = WindowsBrightnessHandlerFactory.from(monitorInfo);
            // Save current brightness if haven't saved already
            formerBrightnessValues.putIfAbsent(monitorInfo.getMonitorId(), brightnessHandler.getCurrentBrightness());
            brightnessHandler.setBrightness(0);

            // Limitation of WMI - if there are at least two software adjustable brightness monitors,
            // normally laptop screens, then it'll dim those monitors too... (see WMIBrightnessHandler::setBrightness)
            if (monitorInfo.isBrightnessDynamicallyAdjustable())
            {
                // Keep track of the current brightness values of all the dynamically adjustable monitors
                // as WMI will set their brightness to zero
                monitorsInfos.stream()
                        .filter(mInfo -> mInfo.isBrightnessDynamicallyAdjustable() && !mInfo.equals(monitorInfo))
                        .forEach(mInfo ->
                        {
                            WindowsBrightnessHandler bHandler = WindowsBrightnessHandlerFactory.from(mInfo);
                            // Save current brightness if haven't saved already
                            formerBrightnessValues.putIfAbsent(mInfo.getMonitorId(), bHandler.getCurrentBrightness());
                            try
                            {
                                bHandler.setBrightness(0);
                            }
                            catch (Exception e)
                            {
                                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        });
            }
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
                // Already been undimmed. Do nothing...
                System.out.println("Monitor with model [" + monitorInfo.getMenuItemName() + "] is already undimmed!");
            }

            // Limitation of WMI - if there are at least two software adjustable brightness monitors,
            // normally laptop screens, then it'll undim those monitors too... (see WMIBrightnessHandler::setBrightness)
            if (monitorInfo.isBrightnessDynamicallyAdjustable())
            {
                // Undim  all the dynamically brightness adjustable monitors
                monitorsInfos.stream()
                        .filter(MonitorInfo::isBrightnessDynamicallyAdjustable)
                        .forEach(mInfo ->
                        {
                            if (formerBrightnessValues.containsKey(mInfo.getMonitorId()))
                            {
                                WindowsBrightnessHandler bHandler = WindowsBrightnessHandlerFactory.from(mInfo);
                                try
                                {
                                    bHandler.setBrightness(Integer.parseInt(formerBrightnessValues.get(mInfo.getMonitorId())));
                                }
                                catch (Exception e)
                                {
                                    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                                }
                                formerBrightnessValues.remove(mInfo.getMonitorId());
                            } else
                            {
                                // Already been undimmed. Do nothing...
                                System.out.println("Dynamically adjustable brightness monitor with model [" + monitorInfo.getMenuItemName() + "] is already undimmed!");
                            }
                        });
            }
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
