package dimmer.windows.brightness;

import dimmer.MonitorInfo;

public class WindowsBrightnessHandlerFactory
{
    private WindowsBrightnessHandlerFactory() {}

    /**
     * A {@link WindowsBrightnessHandler} obj is created to handle the given monitor
     */
    public static WindowsBrightnessHandler from(MonitorInfo monitor)
    {
        if (monitor.isBrightnessDynamicallyAdjustable())
        {
            return new WMIBrightnessHandler(monitor);
        }
        else
        {
            return new WinAPIBrightnessHandler(monitor);
        }
    }
}
